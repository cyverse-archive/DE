// Package logcabin provides common logging functionality that can be used to
// emit messages in the JSON format that we use for logstash/kibana.
package logcabin

import (
	"encoding/json"
	"log"
	"os"
	"time"
)

var (
	Trace   *log.Logger
	Info    *log.Logger
	Warning *log.Logger
	Error   *log.Logger

	TraceLincoln   *Lincoln
	InfoLincoln    *Lincoln
	WarningLincoln *Lincoln
	ErrorLincoln   *Lincoln

	Service  string
	Artifact string
)

// Log Level Constants
const (
	trace_lvl = "TRACE"
	info_lvl  = "INFO"
	warn_lvl  = "WARN"
	err_lvl   = "ERR"
)

func init() {
	Init("jobservices", "default")
}

func Init(service, artifact string) {
	Service = service
	Artifact = artifact

	TraceLincoln = &Lincoln{service, artifact, trace_lvl}
	InfoLincoln = &Lincoln{service, artifact, info_lvl}
	WarningLincoln = &Lincoln{service, artifact, warn_lvl}
	ErrorLincoln = &Lincoln{service, artifact, err_lvl}

	Trace = log.New(TraceLincoln, "", log.Lshortfile)
	Info = log.New(InfoLincoln, "", log.Lshortfile)
	Warning = log.New(WarningLincoln, "", log.Lshortfile)
	Error = log.New(ErrorLincoln, "", log.Lshortfile)
}

// LogMessage represents a message that will be logged in JSON format.
type logMessage struct {
	Service  string `json:"service"`
	Artifact string `json:"art-id"`
	Group    string `json:"group-id"`
	Level    string `json:"level"`
	Time     int64  `json:"timeMillis"`
	Message  string `json:"message"`
}

// Lincoln is a logger for jex-events.
type Lincoln struct {
	service  string
	artifact string
	level    string
}

// NewLogMessage returns a pointer to a new instance of LogMessage.
func (l *Lincoln) newLogMessage(message string) *logMessage {
	lm := &logMessage{
		Service:  l.service,
		Artifact: l.artifact,
		Group:    "org.iplantc",
		Level:    l.level,
		Time:     time.Now().UnixNano() / int64(time.Millisecond),
		Message:  message,
	}
	return lm
}

func (l *Lincoln) Write(buf []byte) (n int, err error) {
	m := l.newLogMessage(string(buf[:]))
	j, err := json.Marshal(m)
	if err != nil {
		return 0, err
	}
	j = append(j, []byte("\n")...)
	return os.Stdout.Write(j)
}
