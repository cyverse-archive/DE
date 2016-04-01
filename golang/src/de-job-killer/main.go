// de-job-killer
//
// A tool for either killing a job that's currently running or to mark a job
// as Failed.
//
// This tool works by either sending out a stop request for a job or by sending
// out a job status update message that marks the job as failed.
package main

import (
	"configurate"
	"flag"
	"fmt"
	"log"
	"logcabin"
	"messaging"
	"model"
	"os"
)

var (
	killJob   = flag.Bool("kill", false, "Send out a stop request. Conflicts with --send-status.")
	statusMsg = flag.Bool("send-status", false, "Send out a job status. Conflicts with --kill.")
	version   = flag.Bool("version", false, "Print the version information.")
	config    = flag.String("config", "", "Path to the jobservices config. Required.")
	uuid      = flag.String("uuid", "", "The job UUID to operate against.")
	gitref    string
	appver    string
	builtby   string
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

func doKillJob(client *messaging.Client, uuid string) error {
	var err error
	if err = client.SendStopRequest(uuid, "admin", "Sent from de-job-killer."); err != nil {
		return err
	}
	return nil
}

func doStatusMessage(client *messaging.Client, uuid string) error {
	var err error
	fauxJob := &model.Job{
		InvocationID: uuid,
	}
	update := &messaging.UpdateMessage{
		Job:     fauxJob,
		State:   messaging.FailedState,
		Message: "Marked as failed by an admin",
	}
	if err = client.PublishJobUpdate(update); err != nil {
		return err
	}
	return nil
}

func main() {
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if *config == "" {
		flag.PrintDefaults()
		log.Fatal("--config must be set.")
	}
	if *uuid == "" {
		flag.PrintDefaults()
		log.Fatal("--uuid must be set.")
	}
	if *killJob && *statusMsg {
		log.Fatal("--kill and --send-status conflict.")
	}
	err := configurate.Init(*config)
	if err != nil {
		log.Fatal(err)
	}
	uri, err := configurate.C.String("amqp.uri")
	if err != nil {
		log.Fatal(err)
	}
	client, err := messaging.NewClient(uri, true)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer client.Close()
	client.SetupPublishing(messaging.JobsExchange)
	go client.Listen()
	switch {
	case *killJob:
		if err = doKillJob(client, *uuid); err != nil {
			log.Fatal(err)
		}
	case *statusMsg:
		if err = doStatusMessage(client, *uuid); err != nil {
			log.Fatal(err)
		}
	}
}
