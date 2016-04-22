package main

import (
	"bytes"
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
	logcabin.Init("image-janitor", "image-janitor")
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

		if isRemovable && img != "<none>:<none>" {
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
		name = strings.Join(parts[0:len(parts)-1], ":")
		tag = parts[len(parts)-1]
		if err = client.SafelyRemoveImage(name, tag); err != nil {
			return err
		}
	}
	return err
}

// removeUnusedImages removes all of the images returned by removeImage() from
// the connected Docker Engine.
func removeUnusedImages(client *dockerops.Docker, readFrom string) {
	logcabin.Info.Println("Removing unused Docker images")
	listing, err := jobFiles(readFrom)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	for _, f := range listing {
		logcabin.Info.Printf("Job file %s found in %s", f, readFrom)
	}
	jobList, err := jobs(listing)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	for _, j := range jobList {
		logcabin.Info.Printf("Job %s found in %s", j.InvocationID, readFrom)
	}
	imagesFromJobs := jobImages(jobList)
	for _, i := range imagesFromJobs {
		logcabin.Info.Printf("Image %s is referenced in a job", i)
	}
	imagesFromDocker, err := client.Images()
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	for _, d := range imagesFromDocker {
		logcabin.Info.Printf("Image %s was listed by Docker", d)
	}
	rmables := removableImages(imagesFromJobs, imagesFromDocker)
	excludes, err := readExcludes(readFrom)
	if err != nil {
		logcabin.Error.Println(err)
	}
	for _, removableImage := range rmables {
		if _, ok := excludes[removableImage]; !ok {
			logcabin.Info.Printf("Removing image %s...", removableImage)
			if err = removeImage(client, removableImage); err != nil {
				logcabin.Error.Printf("Error removing image %s: %s", removableImage, err)
			} else {
				logcabin.Info.Printf("Done removing image %s", removableImage)
			}
		} else {
			logcabin.Info.Printf("Skipping removal of %s", removableImage)
		}
	}
	logcabin.Info.Println("Done removing unused Docker images")
	danglingImages, err := client.DanglingImages()
	if err != nil {
		logcabin.Error.Println(err)
	}
	for _, di := range danglingImages {
		logcabin.Info.Printf("Removing dangling image %s", di)
		if err = client.SafelyRemoveImageByID(di); err != nil {
			logcabin.Error.Println(err)
		} else {
			logcabin.Info.Printf("Done removing dangling image %s", di)
		}
	}
}

func readExcludes(readFrom string) (map[string]bool, error) {
	retval := make(map[string]bool)
	excludesPath := path.Join(readFrom, "excludes")
	excludesBytes, err := ioutil.ReadFile(excludesPath)
	if err != nil {
		return retval, err
	}
	lines := bytes.Split(excludesBytes, []byte("\n"))
	for _, line := range lines {
		retval[string(line)] = true
	}
	return retval, nil
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
	r, err := os.Open(*readFrom)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	r.Close()
	logcabin.Info.Printf("Parsing interval %s", *interval)
	if timerDuration, err = time.ParseDuration(*interval); err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Printf("Successfully parsed interval %s", *interval)
	if *cfgPath == "" {
		logcabin.Error.Fatal("--config must be set.")
	}
	logcabin.Info.Printf("Reading config from %s", *cfgPath)
	if r, err = os.Open(*cfgPath); err != nil {
		logcabin.Error.Fatal(*cfgPath)
	}
	r.Close()
	err = configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Printf("Done reading config from %s", *cfgPath)
	logcabin.Info.Printf("Connecting to Docker at %s", *dockerURI)
	client, err := dockerops.NewDocker(*dockerURI)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Printf("Done connecting to Docker at %s", *dockerURI)
	timer := time.NewTicker(timerDuration)
	for {
		select {
		case <-timer.C:
			removeUnusedImages(client, *readFrom)
		}
	}
}
