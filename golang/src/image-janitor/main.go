package main

import (
	"configurate"
	"dockerops"
	"flag"
	"fmt"
	"io/ioutil"
	"logcabin"
	"model"
	"os"
	"path"
	"regexp"
	"strings"
	"time"
)

var (
	version       = flag.Bool("version", false, "Print version information.")
	interval      = flag.String("interval", "1m", "Time between clean up attempts.")
	cfgPath       = flag.String("config", "/etc/jobservices.yml", "Path to the config.")
	readFrom      = flag.String("read-from", "/opt/image-janitor", "The directory that job files are read from.")
	dockerURI     = flag.String("docker", "unix:///var/run/docker.sock", "The URI for connecting to docker.")
	gitref        string
	appver        string
	builtby       string
	filenameRegex = regexp.MustCompile(`(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\.json$`)
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

// jobFiles lists the files in the directory that have a filename matching the
// filenameRegex pattern.
func jobFiles(dir string) ([]string, error) {
	var retval []string
	entries, err := ioutil.ReadDir(dir)
	if err != nil {
		return nil, err
	}
	for _, entry := range entries {
		if entry.Mode().IsRegular() {
			if !filenameRegex.Match([]byte(entry.Name())) {
				continue
			}
			retval = append(retval, path.Join(dir, entry.Name()))
		}
	}
	return retval, nil
}

// jobs returns a list of model.Job's that were read from the file paths passed
// in.
func jobs(filepaths []string) ([]model.Job, error) {
	var retval []model.Job
	for _, filepath := range filepaths {
		data, err := ioutil.ReadFile(filepath)
		if err != nil {
			return retval, err
		}
		job, err := model.NewFromData(data)
		if err != nil {
			return retval, err
		}
		retval = append(retval, *job)
	}
	return retval, nil
}

// jobImages returns a uniquified list of container images referenced in the
// model.Job's that were passed in.
func jobImages(jobs []model.Job) []string {
	unique := make(map[string]bool)
	for _, job := range jobs {
		jobImages := job.ContainerImages()
		for _, ji := range jobImages {
			repoTag := fmt.Sprintf("%s:%s", ji.Name, ji.Tag)
			unique[repoTag] = true
		}
	}
	var retval []string
	for tag := range unique {
		retval = append(retval, tag)
	}
	return retval
}

// removableImages takes in a list of images referred to in the jobs and a list
// of images returned by Docker and returns the ones that can be safely removed.
// Images are considered safe if they're listed in the Docker images but not
// in the job images.
func removableImages(jobImages, dockerImages []string) []string {
	imageMap := make(map[string]bool)
	for _, di := range dockerImages {
		imageMap[di] = true
	}
	for _, ji := range jobImages {
		imageMap[ji] = false
	}
	var retval []string
	for img, isRemovable := range imageMap {
		if isRemovable {
			retval = append(retval, img)
		}
	}
	return retval
}

// removeImage uses the dockerops.Docker client to safely remove the specified
// image.
func removeImage(client *dockerops.Docker, image string) error {
	var (
		err       error
		parts     []string
		name, tag string
	)
	parts = strings.Split(image, ":")
	if len(parts) > 1 {
		name = parts[0]
		tag = parts[1]
		if err = client.SafelyRemoveImage(name, tag); err != nil {
			return err
		}
	}
	return err
}

// removeUnusedImages removes all of the images returned by removeImage() from
// the connected Docker Engine.
func removeUnusedImages(client *dockerops.Docker, readFrom string) {
	listing, err := jobFiles(readFrom)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	jobList, err := jobs(listing)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	imagesFromJobs := jobImages(jobList)
	imagesFromDocker, err := client.Images()
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	rmables := removableImages(imagesFromJobs, imagesFromDocker)
	for _, removableImage := range rmables {
		if err = removeImage(client, removableImage); err != nil {
			logcabin.Error.Print(err)
		}
	}
}

func main() {
	var (
		err           error
		timerDuration time.Duration
	)
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if _, err = os.Open(*readFrom); err != nil {
		logcabin.Error.Fatal(err)
	}
	if timerDuration, err = time.ParseDuration(*interval); err != nil {
		logcabin.Error.Fatal(err)
	}
	if *cfgPath == "" {
		logcabin.Error.Fatal("--config must be set.")
	}
	if _, err = os.Open(*cfgPath); err != nil {
		logcabin.Error.Fatal(*cfgPath)
	}
	err = configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	client, err := dockerops.NewDocker(*dockerURI)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	timer := time.NewTicker(timerDuration)
	for {
		select {
		case <-timer.C:
			removeUnusedImages(client, *readFrom)
		}
	}
}
