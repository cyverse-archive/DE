package main

import (
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"logcabin"
	"net/http"
	"os"
	"os/exec"
	"strconv"
	"sync"
	"time"
)

var (
	interval     = flag.String("interval", "5s", "The length of time between cache refreshes. Must be in Go's duration string format.")
	port         = flag.String("port", ":60000", "The port to listen on.")
	version      = flag.Bool("version", false, "Print the version information")
	gitref       string
	appver       string
	builtby      string
	condorStatus = "condor_status"
)

func init() {
	flag.Parse()
}

// AppVersion prints version information to stdout
func AppVersion() {
	if appver != "" {
		fmt.Printf("App-Version: %s\n", appver)
	}
	if gitref != "" {
		fmt.Printf("Git-Ref: %s\n", gitref)
	}
	if builtby != "" {
		fmt.Printf("Built-By: %s\n", builtby)
	}
}

type slotStorer struct {
	numSlots  int64
	slotsUsed int64
	mutex     *sync.RWMutex
}

// New returns a new *slotStorer
func New() *slotStorer {
	return &slotStorer{
		mutex: &sync.RWMutex{},
	}
}

// Values returns the number of slots in the cluster and the number that are
// currently being used.
func (s *slotStorer) Values() (numSlots int64, slotsUsed int64) {
	s.mutex.RLock()
	defer s.mutex.RUnlock()
	numSlots = s.numSlots
	slotsUsed = s.slotsUsed
	return numSlots, slotsUsed
}

// Refresh runs condor_status and parses the number of available and used slots
// from the output.
func (s *slotStorer) Refresh() error {
	var (
		numSlots  int64
		slotsUsed int64
		err       error
		cmd       *exec.Cmd
		output    []byte
	)
	logcabin.Info.Println("Refreshing slot information from condor_status")
	cmd = exec.Command(condorStatus)
	if output, err = cmd.CombinedOutput(); err != nil {
		return err
	}
	output = bytes.TrimSpace(output)
	lines := bytes.Split(output, []byte("\n"))
	if len(lines) <= 0 {
		return fmt.Errorf("There where %d lines in the condor_status output", len(lines))
	}
	chunks := bytes.Fields(bytes.TrimSpace(lines[len(lines)-1]))
	if len(chunks) < 4 {
		return fmt.Errorf("There were only %d fields in the last line of the condor_status output", len(chunks))
	}
	if numSlots, err = strconv.ParseInt(string(chunks[1]), 10, 64); err != nil {
		return err
	}
	if slotsUsed, err = strconv.ParseInt(string(chunks[3]), 10, 64); err != nil {
		return err
	}
	logcabin.Info.Printf("Number of slots: %d\tSlots used: %d", numSlots, slotsUsed)
	s.mutex.Lock()
	defer s.mutex.Unlock()
	s.numSlots = numSlots
	s.slotsUsed = slotsUsed
	return nil
}

type response struct {
	NumSlots  int64 `json:"num_slots"`
	SlotsUsed int64 `json:"slots_used"`
}

func (s *slotStorer) Respond(w http.ResponseWriter, r *http.Request) {
	numSlots, slotsUsed := s.Values()
	resp := &response{
		NumSlots:  numSlots,
		SlotsUsed: slotsUsed,
	}
	jsoned, err := json.Marshal(resp)
	if err != nil {
		logcabin.Error.Print(err)
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(err.Error()))
	}
	output := string(jsoned)
	logcabin.Info.Printf("request:\n%#v\nresponse:\n%s", r, output)
	fmt.Fprintf(w, output)
}

func main() {
	var (
		err      error
		duration time.Duration
		t        *time.Ticker
		s        *slotStorer
	)
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if duration, err = time.ParseDuration(*interval); err != nil {
		logcabin.Error.Fatal(err)
	}
	s = New()
	if err = s.Refresh(); err != nil {
		logcabin.Error.Fatal(err)
	}
	http.HandleFunc("/", s.Respond)
	t = time.NewTicker(duration)
	go func() {
		for _ = range t.C {
			if err := s.Refresh(); err != nil {
				logcabin.Error.Print(err)
			}
		}
	}()
	logcabin.Error.Fatal(http.ListenAndServe(*port, nil))
}
