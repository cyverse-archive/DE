package configurate

import "testing"

func configurator() error {
	path := "../test/test_config.yaml"
	return Init(path)
}

func TestNew(t *testing.T) {
	err := configurator()
	if err != nil {
		t.Error(err)
	}
	if C == nil {
		t.Errorf("configurate.New() returned nil")
	}
}

func TestAMQPConfig(t *testing.T) {
	err := configurator()
	if err != nil {
		t.Error(err)
	}
	actual, err := C.String("amqp.uri")
	if err != nil {
		t.Error(err)
	}
	expected := "amqp://guest:guest@192.168.99.100:5672/"
	if actual != expected {
		t.Errorf("The amqp.uri was %s instead of %s", actual, expected)
	}
}

func TestDBConfig(t *testing.T) {
	err := configurator()
	if err != nil {
		t.Error(err)
	}
	actual, err := C.String("db.uri")
	if err != nil {
		t.Error(err)
	}
	expected := "postgres://de:notprod@192.168.99.100:5432/metadata?sslmode=disable"
	if actual != expected {
		t.Errorf("The db.uri was %s instead of %s", actual, expected)
	}
}

func TestESBase(t *testing.T) {
	err := configurator()
	if err != nil {
		t.Error(err)
	}
	actual, err := C.String("elasticsearch.base")
	if err != nil {
		t.Error(err)
	}
	expected := "http://localhost:9200"
	if actual != expected {
		t.Errorf("The elasticsearch.base was %s instead of %s", actual, expected)
	}
}

func TestESIndex(t *testing.T) {
	err := configurator()
	if err != nil {
		t.Error(err)
	}
	actual, err := C.String("elasticsearch.index")
	if err != nil {
		t.Error(err)
	}
	expected := "data"
	if actual != expected {
		t.Errorf("The elasticsearch.index was %s instead of %s", actual, expected)
	}
}
