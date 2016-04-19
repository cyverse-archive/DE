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
)

var (
	version       = flag.Bool("version", false, "Print version information.")
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

func main() {
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if *cfgPath == "" {
		logcabin.Error.Fatal("--config must be set.")
	}
	err := configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	client, err := dockerops.NewDocker(*dockerURI)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	listing, err := jobFiles(*readFrom)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	for _, entry := range listing {
		fmt.Println(entry)
	}
	jobList, err := jobs(listing)
	if err != nil {
		logcabin.Error.Print(err)
	}
	for _, j := range jobList {
		fmt.Println(j.InvocationID)
	}
	imagesFromJobs := jobImages(jobList)
	for _, imageName := range imagesFromJobs {
		fmt.Println(imageName)
	}
	imagesFromDocker, err := client.Images()
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	rmables := removableImages(imagesFromJobs, imagesFromDocker)
	for _, removableImage := range rmables {
		fmt.Println(removableImage)
	}
}
