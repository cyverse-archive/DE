package main

import (
	"bytes"
	"configurate"
	"encoding/json"
	"fmt"
	"logcabin"
	"messaging"
	"os/exec"
	"path"
	"path/filepath"

	"github.com/streadway/amqp"
)

// ExecCondorQ runs the condor_q -long command and returns its output.
func ExecCondorQ() ([]byte, error) {
	var (
		output []byte
		err    error
	)
	csPath, err := exec.LookPath("condor_q")
	if err != nil {
		return output, err
	}
	if !path.IsAbs(csPath) {
		csPath, err = filepath.Abs(csPath)
		if err != nil {
			return output, err
		}
	}
	cmd := exec.Command(csPath, "-long")
	pathEnv, err := configurate.C.String("condor.path_env_var")
	if err != nil {
		pathEnv = ""
	}
	condorCfg, err := configurate.C.String("condor.condor_config")
	if err != nil {
		condorCfg = ""
	}
	cmd.Env = []string{
		fmt.Sprintf("PATH=%s", pathEnv),
		fmt.Sprintf("CONDOR_CONFIG=%s", condorCfg),
	}
	output, err = cmd.CombinedOutput()
	logcabin.Info.Printf("Output of condor_submit:\n%s\n", output)
	if err != nil {
		return output, err
	}
	return output, nil
}

// ExecCondorRm runs condor_rm, passing it the condor ID. Returns the output
// of the command and passibly an error.
func ExecCondorRm(condorID string) ([]byte, error) {
	var (
		output []byte
		err    error
	)
	crPath, err := exec.LookPath("condor_rm")
	logcabin.Info.Printf("condor_rm found at %s", crPath)
	if err != nil {
		return output, err
	}
	if !path.IsAbs(crPath) {
		crPath, err = filepath.Abs(crPath)
		if err != nil {
			return output, err
		}
	}
	pathEnv, err := configurate.C.String("condor.path_env_var")
	if err != nil {
		pathEnv = ""
	}
	condorConfig, err := configurate.C.String("condor.condor_config")
	if err != nil {
		condorConfig = ""
	}
	cmd := exec.Command(crPath, condorID)
	cmd.Env = []string{
		fmt.Sprintf("PATH=%s", pathEnv),
		fmt.Sprintf("CONDOR_CONFIG=%s", condorConfig),
	}
	output, err = cmd.CombinedOutput()
	logcabin.Info.Printf("condor_rm output for job %s:\n%s\n", condorID, output)
	if err != nil {
		return output, err
	}
	return output, nil
}

// CondorID looks up the HTCondor job ID for the given InvocationID. It does
// so by executing condor_q and parsing the longform output.
func CondorID(output []byte, invID string) []string {
	var (
		retval       []string
		condorID     []byte
		jobID        []byte
		condorIDKey  = []byte("ClusterId")
		newLineBytes = []byte("\n")
		equalBytes   = []byte(" = ")
		ipcUUIDBytes = []byte("IpcUuid")
		invocationID = []byte(fmt.Sprintf("\"%s\"", invID))
	)
	chunks := bytes.Split(output, []byte("\n\n"))
	for _, chunk := range chunks {
		lines := bytes.Split(chunk, newLineBytes)
		for _, line := range lines {
			if bytes.Contains(line, equalBytes) {
				lineChunks := bytes.Split(line, equalBytes)
				if len(lineChunks) >= 2 {
					key := lineChunks[0]
					value := lineChunks[1]
					switch {
					case bytes.Equal(key, condorIDKey):
						condorID = bytes.TrimSpace(value)
					case bytes.Equal(key, ipcUUIDBytes):
						jobID = bytes.TrimSpace(value)
					}
				}
			}
		}
		if len(condorID) > 0 && len(jobID) > 0 {
			if bytes.Equal(jobID, invocationID) {
				retval = append(retval, string(bytes.Trim(condorID, "\"")))
			}
		}
	}
	return retval
}

// RegisterStopHandler registers a handler for all stop requests.
func RegisterStopHandler(client *messaging.Client) {
	client.AddConsumer(messaging.JobsExchange, "condor-launcher-stops", messaging.StopRequestKey("*"), func(d amqp.Delivery) {
		var err error
		d.Ack(false)
		stopRequest := &messaging.StopRequest{}
		if err = json.Unmarshal(d.Body, stopRequest); err != nil {
			logcabin.Error.Print(err)
			return
		}
	})
}
