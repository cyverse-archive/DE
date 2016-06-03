package main

import (
	"configurate"
	"fmt"
	"io/ioutil"
	"model"
	"os"
	"path"
	"testing"

	"github.com/olebedev/config"
)

func JSONData() ([]byte, error) {
	f, err := os.Open("../test/test_submission.json")
	if err != nil {
		return nil, err
	}
	c, err := ioutil.ReadAll(f)
	if err != nil {
		return nil, err
	}
	return c, err
}

var (
	s   *model.Job
	cfg *config.Config
)

func _inittests(t *testing.T, memoize bool) *model.Job {
	var err error
	if s == nil || !memoize {
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
		cfg.Set("condor.log_path", "../test/tmp")
		cfg.Set("condor.porklock_tag", "test")
		cfg.Set("condor.filter_files", "foo,bar,baz,blippy")
		cfg.Set("condor.request_disk", "0")
		cfg.Set("condor.path_env_var", "/path/to/path")
		cfg.Set("condor.condor_config", "/condor/config")
		data, err := JSONData()
		if err != nil {
			t.Error(err)
		}
		s, err = model.NewFromData(cfg, data)
		if err != nil {
			t.Error(err)
		}
		PATH := fmt.Sprintf("../test/:%s", os.Getenv("PATH"))
		err = os.Setenv("PATH", PATH)
		if err != nil {
			t.Error(err)
		}
	}
	return s
}

func inittests(t *testing.T) *model.Job {
	return _inittests(t, true)
}

func TestGenerateCondorSubmit(t *testing.T) {
	s := inittests(t)
	cl := New(cfg)
	actual, err := cl.GenerateCondorSubmit(s)
	if err != nil {
		t.Error(err)
	}
	expected := `universe = vanilla
executable = /usr/local/bin/road-runner
rank = mips
arguments = --config config --job job
output = script-output.log
error = script-error.log
log = condor.log
request_disk = 0
+IpcUuid = "07b04ce2-7757-4b21-9e15-0b4c2f44be26"
+IpcJobId = "generated_script"
+IpcUsername = "test_this_is_a_test"
concurrency_limits = _00000000000000000000000000000000
+IpcExe = "wc_wrapper.sh"
+IpcExePath = "/usr/local3/bin/wc_tool-1.00"
should_transfer_files = YES
transfer_input_files = irods-config,iplant.cmd,config,job
transfer_output_files = logs/de-transfer-trigger.log,logs/logs-stdout-output,logs/logs-stderr-output
when_to_transfer_output = ON_EXIT_OR_EVICT
notification = NEVER
queue
`
	if actual != expected {
		t.Errorf("GenerateCondorSubmit() returned:\n\n%s\n\ninstead of:\n\n%s", actual, expected)
	}
	s.Group = "foo"
	actual, err = cl.GenerateCondorSubmit(s)
	if err != nil {
		t.Error(err)
	}
	expected = `universe = vanilla
executable = /usr/local/bin/road-runner
rank = mips
arguments = --config config --job job
output = script-output.log
error = script-error.log
log = condor.log
accounting_group = foo
accounting_group_user = test_this_is_a_test
request_disk = 0
+IpcUuid = "07b04ce2-7757-4b21-9e15-0b4c2f44be26"
+IpcJobId = "generated_script"
+IpcUsername = "test_this_is_a_test"
concurrency_limits = _00000000000000000000000000000000
+IpcExe = "wc_wrapper.sh"
+IpcExePath = "/usr/local3/bin/wc_tool-1.00"
should_transfer_files = YES
transfer_input_files = irods-config,iplant.cmd,config,job
transfer_output_files = logs/de-transfer-trigger.log,logs/logs-stdout-output,logs/logs-stderr-output
when_to_transfer_output = ON_EXIT_OR_EVICT
notification = NEVER
queue
`
	if actual != expected {
		t.Errorf("GenerateCondorSubmit() returned:\n\n%s\n\ninstead of:\n\n%s", actual, expected)
	}
	_inittests(t, false)
}

func TestCreateSubmissionDirectory(t *testing.T) {
	s := inittests(t)
	cl := New(cfg)
	dir, err := cl.CreateSubmissionDirectory(s)
	if err != nil {
		t.Error(err)
	}
	_, err = os.Stat(dir)
	if err != nil {
		t.Error(err)
	}
	logPath, err := cl.cfg.String("condor.log_path")
	if err != nil {
		t.Error(err)
	}
	parent := path.Join(logPath, s.Submitter)
	err = os.RemoveAll(parent)
	if err != nil {
		t.Error(err)
	}
	_inittests(t, false)
}

func TestCreateSubmissionFiles(t *testing.T) {
	s := inittests(t)
	cl := New(cfg)
	dir, err := cl.CreateSubmissionDirectory(s)
	if err != nil {
		t.Error(err)
	}
	cmd, sh, c, err := cl.CreateSubmissionFiles(dir, s)
	if err != nil {
		t.Error(err)
	}
	_, err = os.Stat(cmd)
	if err != nil {
		t.Error(err)
	}
	_, err = os.Stat(sh)
	if err != nil {
		t.Error(err)
	}
	_, err = os.Stat(c)
	if err != nil {
		t.Error(err)
	}
	irodsPath := path.Join(path.Dir(cmd), "irods-config")
	_, err = os.Stat(irodsPath)
	if err != nil {
		t.Error(err)
	}
	logPath, err := cl.cfg.String("condor.log_path")
	if err != nil {
		t.Error(err)
	}
	parent := path.Join(logPath, s.Submitter)
	err = os.RemoveAll(parent)
	if err != nil {
		t.Error(err)
	}
	_inittests(t, false)
}

func TestCondorSubmit(t *testing.T) {
	s := inittests(t)
	PATH := fmt.Sprintf(".:%s", os.Getenv("PATH"))
	err := os.Setenv("PATH", PATH)
	if err != nil {
		t.Error(err)
	}
	cl := New(cfg)
	dir, err := cl.CreateSubmissionDirectory(s)
	if err != nil {
		t.Error(err)
	}
	cmd, _, _, err := cl.CreateSubmissionFiles(dir, s)
	if err != nil {
		t.Error(err)
	}
	actual, err := cl.submit(cmd, s)
	if err != nil {
		t.Error(err)
	}
	expected := "10000"
	if actual != expected {
		t.Errorf("CondorSubmit() returned %s instead of %s", actual, expected)
	}
	logPath, err := cl.cfg.String("condor.log_path")
	if err != nil {
		t.Error(err)
	}
	parent := path.Join(logPath, s.Submitter)
	err = os.RemoveAll(parent)
	if err != nil {
		t.Error(err)
	}
}

func TestLaunch(t *testing.T) {
	inittests(t)
	cl := New(cfg)
	data, err := JSONData()
	if err != nil {
		t.Error(err)
	}
	j, err := model.NewFromData(cl.cfg, data)
	if err != nil {
		t.Error(err)
	}
	actual, err := cl.launch(j)
	if err != nil {
		t.Error(err)
	}
	expected := "10000"
	if actual != expected {
		t.Errorf("launch returned:\n%s\ninstead of:\n%s\n", actual, expected)
	}
	parent := path.Join(j.CondorLogPath, "test_this_is_a_test")
	err = os.RemoveAll(parent)
	if err != nil {
		t.Error(err)
	}
}

func TestStop(t *testing.T) {
	inittests(t)
	cl := New(cfg)
	//Start up a fake jex-events
	jr := &model.Job{
		CondorID:     "10000",
		Submitter:    "test_this_is_a_test",
		AppID:        "c7f05682-23c8-4182-b9a2-e09650a5f49b",
		InvocationID: "00000000-0000-0000-0000-000000000000",
	}
	actual, err := cl.stop(jr)
	if err != nil {
		t.Error(err)
	}
	if actual == "" {
		t.Errorf("stop returned an empty string")
	}
}
