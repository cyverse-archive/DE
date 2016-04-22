package main

import (
	"dockerops"
	"logcabin"
	"messaging"
	"model"
	"strconv"
)

func cleanup(job *model.Job) {
	logcabin.Info.Printf("Performing aggressive clean up routine...")
	logcabin.Info.Println("Finding all input containers")
	inputContainers, err := dckr.ContainersWithLabel(dockerops.TypeLabel, strconv.Itoa(dockerops.InputContainer), true)
	if err != nil {
		logcabin.Error.Print(err)
		inputContainers = []string{}
	}
	for _, ic := range inputContainers {
		logcabin.Info.Printf("Nuking input container %s", ic)
		err = dckr.NukeContainer(ic)
		if err != nil {
			logcabin.Error.Print(err)
		}
	}
	logcabin.Info.Println("Finding all step containers")
	stepContainers, err := dckr.ContainersWithLabel(dockerops.TypeLabel, strconv.Itoa(dockerops.StepContainer), true)
	if err != nil {
		logcabin.Error.Print(err)
	}
	for _, sc := range stepContainers {
		logcabin.Info.Printf("Nuking step container %s", sc)
		err = dckr.NukeContainer(sc)
		if err != nil {
			logcabin.Error.Print(err)
		}
	}
	logcabin.Info.Println("Finding all data containers")
	dataContainers, err := dckr.ContainersWithLabel(dockerops.TypeLabel, strconv.Itoa(dockerops.DataContainer), true)
	if err != nil {
		logcabin.Error.Print(err)
	}
	for _, dc := range dataContainers {
		logcabin.Info.Printf("Nuking data container %s", dc)
		err = dckr.NukeContainer(dc)
		if err != nil {
			logcabin.Error.Print(err)
		}
	}
}

// Exit returns a function that can be called by a TimeTracker's Timer, which
// should be created with timer.AfterFunc(). exit is the channel that this
// function reads from, finalExit is the channel that this channel writes to
// when it's done doing its thing.
func Exit(exit, finalExit chan messaging.StatusCode) {
	var err error
	exitCode := <-exit
	switch exitCode {
	case messaging.StatusTimeLimit, messaging.StatusKilled:
		//Annihilate the input/steps/data containers even if they're running,
		//but allow the output containers to run. Yanking the rug out from the
		//containers should force the Run() function to 'fall through' to any clean
		//up steps.
		logcabin.Warning.Printf("Received an exit code of %d, cleaning up", int(exitCode))
		for _, dc := range job.DataContainers() {
			logcabin.Info.Printf("Nuking image %s:%s", dc.Name, dc.Tag)
			err = dckr.NukeImage(dc.Name, dc.Tag)
			if err != nil {
				logcabin.Error.Print(err)
			}
		}

		cleanup(job)

		//wait for the exit code from the Run function.
		<-exit

		//Aggressively clean up the rest of the job.
		logcabin.Info.Printf("Nuking all containers with the label %s=%s", model.DockerLabelKey, job.InvocationID)
		err = dckr.NukeContainersByLabel(model.DockerLabelKey, job.InvocationID)
		if err != nil {
			logcabin.Error.Print(err)
		}

	default:
		logcabin.Warning.Printf("Received an exit code of %d, cleaning up", int(exitCode))
		logcabin.Info.Printf("Finding all containers with the label %s=%s", model.DockerLabelKey, job.InvocationID)
		jobContainers, err := dckr.ContainersWithLabel(model.DockerLabelKey, job.InvocationID, true)
		if err != nil {
			logcabin.Error.Print(err)
			jobContainers = []string{}
		}
		for _, jc := range jobContainers {
			logcabin.Info.Printf("Nuking container %s", jc)
			err = dckr.NukeContainer(jc)
			if err != nil {
				logcabin.Error.Print(err)
			}
		}
	}
	finalExit <- exitCode
}
