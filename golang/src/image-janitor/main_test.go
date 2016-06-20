package main

import (
	"configurate"
	"dockerops"
	"os"
	"testing"

	"golang.org/x/net/context"

	"github.com/olebedev/config"
)

var (
	cfg *config.Config
)

func shouldrun() bool {
	if os.Getenv("RUN_INTEGRATION_TESTS") != "" {
		return true
	}
	return false
}

func uri() string {
	return "http://dind:2375"
}

func Client() (*dockerops.Docker, error) {
	client, err := dockerops.NewDocker(context.Background(), cfg, uri())
	if err != nil {
		return nil, err
	}
	return client, nil
}

func inittests(t *testing.T) {
	var err error
	cfg, err = configurate.Init("../test/test_config.yaml")
	if err != nil {
		t.Error(err)
	}
	cfg.Set("irods.base", "/path/to/irodsbase")
	cfg.Set("irods.host", "hostname")
	cfg.Set("irods.port", "1247")
	cfg.Set("irods.user", "user")
	cfg.Set("irods.pass", "pass")
	cfg.Set("irods.zone", "test")
	cfg.Set("irods.resc", "")
	cfg.Set("condor.log_path", "/path/to/logs")
	cfg.Set("condor.porklock_tag", "test")
	cfg.Set("condor.filter_files", "foo,bar,baz,blippy")
	cfg.Set("condor.request_disk", "0")
}

func TestJobFiles(t *testing.T) {
	app := New(cfg)
	listing, err := app.jobFiles("../test/")
	if err != nil {
		t.Error(err)
	}
	expectedLength := 3
	actualLength := len(listing)
	if actualLength != expectedLength {
		t.Errorf("length of listing was %d instead of %d", actualLength, expectedLength)
	}
	found0 := false
	found1 := false
	found2 := false
	for _, filepath := range listing {
		if filepath == "../test/00000000-0000-0000-0000-000000000000.json" {
			found0 = true
		}
		if filepath == "../test/00000000-0000-0000-0000-000000000001.json" {
			found1 = true
		}
		if filepath == "../test/00000000-0000-0000-0000-000000000002.json" {
			found2 = true
		}
	}
	if !found0 {
		t.Error("Path ../test/00000000-0000-0000-0000-000000000000.json was not found")
	}
	if !found1 {
		t.Error("Path ../test/00000000-0000-0000-0000-000000000001.json was not found")
	}
	if !found2 {
		t.Error("Path ../test/00000000-0000-0000-0000-000000000002.json was not found")
	}
}

func TestJobs(t *testing.T) {
	inittests(t)
	app := New(cfg)
	paths, err := app.jobFiles("../test/")
	if err != nil {
		t.Error(err)
	}
	listing, err := app.jobs(paths)
	if err != nil {
		t.Error(err)
	}
	actualLength := len(listing)
	expectedLength := 3
	if actualLength != expectedLength {
		t.Errorf("length of listing was %d instead of %d", actualLength, expectedLength)
	}
	found0 := false
	found1 := false
	found2 := false
	for _, j := range listing {
		switch j.InvocationID {
		case "07b04ce2-7757-4b21-9e15-0b4c2f44be26":
			found0 = true
		case "07b04ce2-7757-4b21-9e15-0b4c2f44be27":
			found1 = true
		case "07b04ce2-7757-4b21-9e15-0b4c2f44be28":
			found2 = true
		}
	}
	if !found0 {
		t.Error("InvocationID 07b04ce2-7757-4b21-9e15-0b4c2f44be26 was not found")
	}
	if !found1 {
		t.Error("InvocationID 07b04ce2-7757-4b21-9e15-0b4c2f44be27 was not found")
	}
	if !found2 {
		t.Error("InvocationID 07b04ce2-7757-4b21-9e15-0b4c2f44be28 was not found")
	}
}

func TestJobImages(t *testing.T) {
	inittests(t)
	app := New(cfg)
	paths, err := app.jobFiles("../test/")
	if err != nil {
		t.Error(err)
	}
	listing, err := app.jobs(paths)
	if err != nil {
		t.Error(err)
	}
	images := app.jobImages(listing)
	actualLength := len(images)
	expectedLength := 2
	if actualLength != expectedLength {
		t.Errorf("Number of images was %d instead of %d", actualLength, expectedLength)
	}
	found0 := false
	found1 := false
	for _, i := range images {
		switch i {
		case "gims.iplantcollaborative.org:5000/backwards-compat:latest":
			found0 = true
		case "gims.iplantcollaborative.org:5000/fake-image:latest":
			found1 = true
		}
	}
	if !found0 {
		t.Error("Did not find the backwards-compat image")
	}
	if !found1 {
		t.Error("Did not find the fake-image image")
	}
}

func TestRemovableImages(t *testing.T) {
	inittests(t)
	app := New(cfg)
	paths, err := app.jobFiles("../test/")
	if err != nil {
		t.Error(err)
	}
	listing, err := app.jobs(paths)
	if err != nil {
		t.Error(err)
	}
	jImages := app.jobImages(listing)
	dImages := []string{
		"gims.iplantcollaborative.org:5000/backwards-compat:latest",
		"gims.iplantcollaborative.org:5000/fake-image:latest",
		"not-listed",
	}
	rImages := app.removableImages(jImages, dImages)
	actualLength := len(rImages)
	expectedLength := 1
	if actualLength != expectedLength {
		t.Errorf("The number of removable images was %d instead of %d", actualLength, expectedLength)
	}
	actual := rImages[0]
	expected := "not-listed"
	if actual != expected {
		t.Errorf("Removable image was %s instead of %s", actual, expected)
	}
}

func TestRemoveImage(t *testing.T) {
	if !shouldrun() {
		return
	}
	app := New(cfg)
	client, err := Client()
	if err != nil {
		t.Error(err)
	}
	err = client.Pull("alpine", "latest")
	if err != nil {
		t.Error(err)
	}
	err = app.removeImage(client, "alpine:latest")
	if err != nil {
		t.Error(err)
	}
	images, err := client.Images()
	if err != nil {
		t.Error(err)
	}
	found := false
	for _, i := range images {
		if i == "alpine:latest" {
			found = true
		}
	}
	if found {
		t.Error("alpine:latest was found")
	}
}

func TestRemoveUnusedImages(t *testing.T) {
	if !shouldrun() {
		return
	}
	app := New(cfg)
	client, err := Client()
	if err != nil {
		t.Error(err)
	}
	err = client.Pull("alpine", "latest")
	if err != nil {
		t.Error(err)
	}
	app.removeUnusedImages(client, "../test/")
	images, err := client.Images()
	if err != nil {
		t.Error(err)
	}
	found := false
	for _, i := range images {
		if i == "alpine:latest" {
			found = true
		}
	}
	if found {
		t.Error("alpine:latest was found")
	}
}
