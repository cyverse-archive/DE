package configurate

import (
	"testing"

	"github.com/olebedev/config"
)

func configurator() (*config.Config, error) {
	path := "../test/test_config.yaml"
	return Init(path)
}

func TestNew(t *testing.T) {
	cfg, err := configurator()
	if err != nil {
		t.Error(err)
	}
	if cfg == nil {
		t.Errorf("configurate.New() returned nil")
	}
}

func TestAMQPConfig(t *testing.T) {
	cfg, err := configurator()
	if err != nil {
		t.Error(err)
	}
	actual, err := cfg.String("amqp.uri")
	if err != nil {
		t.Error(err)
	}
	expected := "amqp://guest:guest@rabbit:5672/"
	if actual != expected {
		t.Errorf("The amqp.uri was %s instead of %s", actual, expected)
	}
}

// func TestValid(t *testing.T) {
// 	cfg, err := configurator()
// 	if err != nil {
// 		t.Error(err)
// 		t.Fail()
// 	}
// 	if !cfg.Valid() {
// 		t.Errorf("configurate.Valid() return false")
// 	}
// }
