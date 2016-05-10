// Package configurate provides common configuration functionality.
// It supports reading configuration settings from a YAML file. All of the job
// services are intended to read from the same configuration file.
package configurate

import (
	"io/ioutil"
	"os"

	"github.com/olebedev/config"
)

// Init initializes the underlying config.
func Init(path string) (*config.Config, error) {
	f, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	contents, err := ioutil.ReadAll(f)
	if err != nil {
		return nil, err
	}
	return config.ParseYaml(string(contents))
}
