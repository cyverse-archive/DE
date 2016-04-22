package main

import (
	"dockerops"
	"fmt"
	"logcabin"
	"messaging"
	"model"
	"os"
	"strings"
)

// Environment returns a []string containing the environment variables that
// need to get set for every job.
func Environment(job *model.Job) []string {
	current := os.Environ()
	current = append(current, fmt.Sprintf("IPLANT_USER=%s", job.Submitter))
	current = append(current, fmt.Sprintf("IPLANT_EXECUTION_ID=%s", job.InvocationID))
	return current
}

// Run executes the job, and returns the exit code on the exit channel.
func Run(client *messaging.Client, dckr *dockerops.Docker, exit chan messaging.StatusCode) {
	status := messaging.Success
	host, err := os.Hostname()
	if err != nil {
		logcabin.Error.Print(err)
		host = "UNKNOWN"
	}
	// let everyone know the job is running
	running(client, job, fmt.Sprintf("Job %s is running on host %s", job.InvocationID, host))

	transferTrigger, err := os.Create("logs/de-transfer-trigger.log")
	if err != nil {
		logcabin.Error.Print(err)
	} else {
		_, err = transferTrigger.WriteString("This is only used to force HTCondor to transfer files.")
		if err != nil {
			logcabin.Error.Print(err)
		}
	}

	if _, err = os.Stat("iplant.cmd"); err != nil {
		if err = os.Rename("iplant.cmd", "logs/iplant.cmd"); err != nil {
			logcabin.Error.Print(err)
		}
	}

	// Pull the data containers
	for _, dc := range job.DataContainers() {
		running(client, job, fmt.Sprintf("Pulling container image %s:%s", dc.Name, dc.Tag))
		err = dckr.Pull(dc.Name, dc.Tag)
		if err != nil {
			logcabin.Error.Print(err)
			status = messaging.StatusDockerPullFailed
			running(client, job, fmt.Sprintf("Error pulling container '%s:%s': %s", dc.Name, dc.Tag, err.Error()))
			break
		}
		running(client, job, fmt.Sprintf("Done pulling container %s:%s", dc.Name, dc.Tag))
	}

	// Create the data containers
	if status == messaging.Success {
		for _, dc := range job.DataContainers() {
			running(client, job, fmt.Sprintf("Creating data container %s-%s", dc.NamePrefix, job.InvocationID))
			_, _, err = dckr.CreateDataContainer(&dc, job.InvocationID)
			if err != nil {
				logcabin.Error.Print(err)
				status = messaging.StatusDockerPullFailed
				running(client, job, fmt.Sprintf("Error creating data container %s-%s", dc.NamePrefix, job.InvocationID))
				break
			}
			running(client, job, fmt.Sprintf("Done creating data container %s-%s", dc.NamePrefix, job.InvocationID))
		}
	}

	// Pull the job step containers
	if status == messaging.Success {
		for _, ci := range job.ContainerImages() {
			running(client, job, fmt.Sprintf("Pulling tool container %s:%s", ci.Name, ci.Tag))
			err = dckr.Pull(ci.Name, ci.Tag)
			if err != nil {
				logcabin.Error.Print(err)
				status = messaging.StatusDockerPullFailed
				running(client, job, fmt.Sprintf("Error pulling tool container '%s:%s': %s", ci.Name, ci.Tag, err.Error()))
				break
			}
			running(client, job, fmt.Sprintf("Done pulling tool container %s:%s", ci.Name, ci.Tag))
		}
	}
	var exitCode int
	// If pulls didn't succeed then we can't guarantee that we've got the
	// correct versions of the tools. Don't bother pulling in data in that case,
	// things are already screwed up.
	if status == messaging.Success {
		for idx, input := range job.Inputs() {
			running(client, job, fmt.Sprintf("Downloading %s", input.IRODSPath()))
			exitCode, err = dckr.DownloadInputs(job, &input, idx)
			if exitCode != 0 || err != nil {
				if err != nil {
					logcabin.Error.Print(err)
					running(client, job, fmt.Sprintf("Error downloading %s: %s", input.IRODSPath(), err.Error()))
				} else {
					running(client, job, fmt.Sprintf("Error downloading %s: Transfer utility exited with %d", input.IRODSPath(), exitCode))
				}
				status = messaging.StatusInputFailed
				break
			}
			running(client, job, fmt.Sprintf("Finished downloading %s", input.IRODSPath()))
		}
	}

	// Only attempt to run the steps if the input downloads succeeded. No reason
	// to run the steps if there's no/corrupted data to operate on.
	if status == messaging.Success {
		for idx, step := range job.Steps {
			running(client, job,
				fmt.Sprintf(
					"Running tool container %s:%s with arguments: %s",
					step.Component.Container.Image.Name,
					step.Component.Container.Image.Tag,
					strings.Join(step.Arguments(), " "),
				),
			)
			exitCode, err = dckr.RunStep(&step, job.InvocationID, idx)
			if exitCode != 0 || err != nil {
				if err != nil {
					logcabin.Error.Print(err)
					running(client, job,
						fmt.Sprintf(
							"Error running tool container %s:%s with arguments '%s': %s",
							step.Component.Container.Image.Name,
							step.Component.Container.Image.Tag,
							strings.Join(step.Arguments(), " "),
							err.Error(),
						),
					)
				} else {
					running(client, job,
						fmt.Sprintf(
							"Tool container %s:%s with arguments '%s' exit with code: %d",
							step.Component.Container.Image.Name,
							step.Component.Container.Image.Tag,
							strings.Join(step.Arguments(), " "),
							exitCode,
						),
					)
				}
				status = messaging.StatusStepFailed
				break
			}
			running(client, job,
				fmt.Sprintf("Tool container %s:%s with arguments '%s' finished successfully",
					step.Component.Container.Image.Name,
					step.Component.Container.Image.Tag,
					strings.Join(step.Arguments(), " "),
				),
			)
		}
	}

	// Always attempt to transfer outputs. There might be logs that can help
	// debug issues when the job fails.
	running(client, job, fmt.Sprintf("Beginning to upload outputs to %s", job.OutputDirectory()))
	exitCode, err = dckr.UploadOutputs(job)
	if exitCode != 0 || err != nil {
		if err != nil {
			logcabin.Error.Print(err)
			running(client, job, fmt.Sprintf("Error uploading outputs to %s: %s", job.OutputDirectory(), err.Error()))
		} else {
			if client == nil {
				logcabin.Warning.Println("client is nil")
			}
			if job == nil {
				logcabin.Warning.Println("job is nil")
			}
			od := job.OutputDirectory()
			running(client, job, fmt.Sprintf("Transfer utility exited with a code of %d when uploading outputs to %s", exitCode, od))
		}
		status = messaging.StatusOutputFailed
	}
	running(client, job, fmt.Sprintf("Done uploading outputs to %s", job.OutputDirectory()))

	// Always inform upstream of the job status.
	if status != messaging.Success {
		fail(client, job, fmt.Sprintf("Job exited with a status of %d", status))
	} else {
		success(client, job)
	}
	exit <- status
}
