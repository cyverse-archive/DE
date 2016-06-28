package main

import (
	"dockerops"
	"fmt"
	"logcabin"
	"messaging"
	"model"
	"os"
	"strings"
	"time"
)

func getTicker(timeLimit int, exit chan messaging.StatusCode) (chan int, error) {
	if timeLimit <= 0 {
		return nil, fmt.Errorf("TimeLimit was not %d instead of > 0", timeLimit)
	}

	stepDuration, err := time.ParseDuration(fmt.Sprintf("%ds", timeLimit))
	if err != nil {
		return nil, fmt.Errorf("Could not parse duration: %s", err)
	}

	stepTicker := time.NewTicker(stepDuration)
	quitTicker := make(chan int)

	go func(*time.Ticker, chan int) {
		select {
		case <-stepTicker.C:
			logcabin.Info.Print("ticker received message to exit")
			exit <- messaging.StatusTimeLimit
		case <-quitTicker:
			logcabin.Info.Print("ticker received message to quit")
		}
	}(stepTicker, quitTicker)

	return quitTicker, nil
}

// JobRunner provides the functionality needed to run jobs.
type JobRunner struct {
	client *messaging.Client
	dckr   *dockerops.Docker
	exit   chan messaging.StatusCode
	job    *model.Job
	status messaging.StatusCode
}

func (r *JobRunner) pullDataImages() error {
	var err error
	for _, dc := range r.job.DataContainers() {
		running(r.client, r.job, fmt.Sprintf("Pulling container image %s:%s", dc.Name, dc.Tag))
		err = r.dckr.Pull(dc.Name, dc.Tag)
		if err != nil {
			r.status = messaging.StatusDockerPullFailed
			running(r.client, r.job, fmt.Sprintf("Error pulling container image '%s:%s': %s", dc.Name, dc.Tag, err.Error()))
			return err
		}
		running(r.client, r.job, fmt.Sprintf("Done pulling container image %s:%s", dc.Name, dc.Tag))
	}
	return err
}

func (r *JobRunner) createDataContainers() error {
	var err error
	for _, dc := range r.job.DataContainers() {
		running(r.client, r.job, fmt.Sprintf("Creating data container %s-%s", dc.NamePrefix, job.InvocationID))
		_, err = r.dckr.CreateDataContainer(&dc, r.job.InvocationID)
		if err != nil {
			r.status = messaging.StatusDockerPullFailed
			running(r.client, r.job, fmt.Sprintf("Error creating data container %s-%s", dc.NamePrefix, job.InvocationID))
			return err
		}
		running(r.client, r.job, fmt.Sprintf("Done creating data container %s-%s", dc.NamePrefix, job.InvocationID))
	}
	return err
}

func (r *JobRunner) pullStepImages() error {
	var err error
	for _, ci := range r.job.ContainerImages() {
		running(r.client, r.job, fmt.Sprintf("Pulling tool container %s:%s", ci.Name, ci.Tag))
		err = r.dckr.Pull(ci.Name, ci.Tag)
		if err != nil {
			r.status = messaging.StatusDockerPullFailed
			running(r.client, r.job, fmt.Sprintf("Error pulling tool container '%s:%s': %s", ci.Name, ci.Tag, err.Error()))
			return err
		}
		running(r.client, r.job, fmt.Sprintf("Done pulling tool container %s:%s", ci.Name, ci.Tag))
	}
	return err
}

func (r *JobRunner) downloadInputs() error {
	var err error
	var exitCode int
	for idx, input := range r.job.Inputs() {
		running(r.client, r.job, fmt.Sprintf("Downloading %s", input.IRODSPath()))
		exitCode, err = dckr.DownloadInputs(r.job, &input, idx)
		if exitCode != 0 || err != nil {
			if err != nil {
				running(r.client, r.job, fmt.Sprintf("Error downloading %s: %s", input.IRODSPath(), err.Error()))
			} else {
				running(r.client, r.job, fmt.Sprintf("Error downloading %s: Transfer utility exited with %d", input.IRODSPath(), exitCode))
			}
			r.status = messaging.StatusInputFailed
			return err
		}
		running(r.client, r.job, fmt.Sprintf("Finished downloading %s", input.IRODSPath()))
	}
	return err
}

func (r *JobRunner) runAllSteps(exit chan messaging.StatusCode) error {
	var err error
	var exitCode int

	for idx, step := range r.job.Steps {
		running(r.client, r.job,
			fmt.Sprintf(
				"Running tool container %s:%s with arguments: %s",
				step.Component.Container.Image.Name,
				step.Component.Container.Image.Tag,
				strings.Join(step.Arguments(), " "),
			),
		)

		step.Environment["IPLANT_USER"] = job.Submitter
		step.Environment["IPLANT_EXECUTION_ID"] = job.InvocationID

		// TimeLimits set to 0 mean that there isn't a time limit.
		var timeLimitEnabled bool
		if step.Component.TimeLimit > 0 {
			logcabin.Info.Printf("Time limit is set to %d", step.Component.TimeLimit)
			timeLimitEnabled = true
		} else {
			logcabin.Info.Print("time limit is disabled")
		}

		// Start up the ticker
		var tickerQuit chan int
		if timeLimitEnabled {
			tickerQuit, err = getTicker(step.Component.TimeLimit, exit)
			if err != nil {
				logcabin.Error.Print(err)
				timeLimitEnabled = false
			} else {
				logcabin.Info.Print("started up time limit ticker")
			}
		}

		exitCode, err = dckr.RunStep(&step, r.job.InvocationID, idx)

		// Shut down the ticker
		if timeLimitEnabled {
			tickerQuit <- 1
			logcabin.Info.Print("sent message to stop time limit ticker")
		}

		if exitCode != 0 || err != nil {
			if err != nil {
				running(r.client, r.job,
					fmt.Sprintf(
						"Error running tool container %s:%s with arguments '%s': %s",
						step.Component.Container.Image.Name,
						step.Component.Container.Image.Tag,
						strings.Join(step.Arguments(), " "),
						err.Error(),
					),
				)
			} else {
				running(r.client, r.job,
					fmt.Sprintf(
						"Tool container %s:%s with arguments '%s' exit with code: %d",
						step.Component.Container.Image.Name,
						step.Component.Container.Image.Tag,
						strings.Join(step.Arguments(), " "),
						exitCode,
					),
				)
			}
			r.status = messaging.StatusStepFailed
			return err
		}
		running(r.client, r.job,
			fmt.Sprintf("Tool container %s:%s with arguments '%s' finished successfully",
				step.Component.Container.Image.Name,
				step.Component.Container.Image.Tag,
				strings.Join(step.Arguments(), " "),
			),
		)
	}
	return err
}

func (r *JobRunner) uploadOutputs() error {
	var (
		err      error
		exitCode int
	)

	exitCode, err = dckr.UploadOutputs(r.job)
	if exitCode != 0 || err != nil {
		if err != nil {
			running(r.client, r.job, fmt.Sprintf("Error uploading outputs to %s: %s", r.job.OutputDirectory(), err.Error()))
		} else {
			if r.client == nil {
				logcabin.Warning.Println("client is nil")
			}
			if r.job == nil {
				logcabin.Warning.Println("job is nil")
			}
			od := r.job.OutputDirectory()
			running(r.client, r.job, fmt.Sprintf("Transfer utility exited with a code of %d when uploading outputs to %s", exitCode, od))
		}
		r.status = messaging.StatusOutputFailed
	}

	running(r.client, r.job, fmt.Sprintf("Done uploading outputs to %s", r.job.OutputDirectory()))

	return err
}

// Run executes the job, and returns the exit code on the exit channel.
func Run(client *messaging.Client, dckr *dockerops.Docker, exit chan messaging.StatusCode) {
	runner := &JobRunner{
		client: client,
		dckr:   dckr,
		exit:   exit,
		job:    job,
		status: messaging.Success,
	}

	host, err := os.Hostname()
	if err != nil {
		logcabin.Error.Print(err)
		host = "UNKNOWN"
	}

	// let everyone know the job is running
	running(runner.client, runner.job, fmt.Sprintf("Job %s is running on host %s", runner.job.InvocationID, host))

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

	// Pull the data container images
	if err = runner.pullDataImages(); err != nil {
		logcabin.Error.Print(err)
	}

	// Create the data containers
	if runner.status == messaging.Success {
		if err = runner.createDataContainers(); err != nil {
			logcabin.Error.Print(err)
		}
	}

	// Pull the job step containers
	if runner.status == messaging.Success {
		if err = runner.pullStepImages(); err != nil {
			logcabin.Error.Print(err)
		}
	}

	// If pulls didn't succeed then we can't guarantee that we've got the
	// correct versions of the tools. Don't bother pulling in data in that case,
	// things are already screwed up.
	if runner.status == messaging.Success {
		if err = runner.downloadInputs(); err != nil {
			logcabin.Error.Print(err)
		}
	}

	// Only attempt to run the steps if the input downloads succeeded. No reason
	// to run the steps if there's no/corrupted data to operate on.
	if runner.status == messaging.Success {
		if err = runner.runAllSteps(exit); err != nil {
			logcabin.Error.Print(err)
		}
	}

	// Always attempt to transfer outputs. There might be logs that can help
	// debug issues when the job fails.
	running(runner.client, runner.job, fmt.Sprintf("Beginning to upload outputs to %s", runner.job.OutputDirectory()))
	if err = runner.uploadOutputs(); err != nil {
		logcabin.Error.Print(err)
	}

	// Always inform upstream of the job status.
	if runner.status != messaging.Success {
		fail(runner.client, runner.job, fmt.Sprintf("Job exited with a status of %d", runner.status))
	} else {
		success(runner.client, runner.job)
	}

	exit <- runner.status
}
