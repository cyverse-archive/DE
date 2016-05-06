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

	Trace_Lincoln   *Lincoln
	Info_Lincoln    *Lincoln
	Warning_Lincoln *Lincoln
	Error_Lincoln   *Lincoln

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

	Trace_Lincoln = &Lincoln{service, artifact, trace_lvl}
	Info_Lincoln = &Lincoln{service, artifact, info_lvl}
	Warning_Lincoln = &Lincoln{service, artifact, warn_lvl}
	Error_Lincoln = &Lincoln{service, artifact, err_lvl}

	Trace = log.New(Trace_Lincoln, "", log.Lshortfile)
	Info = log.New(Info_Lincoln, "", log.Lshortfile)
	Warning = log.New(Warning_Lincoln, "", log.Lshortfile)
	Error = log.New(Error_Lincoln, "", log.Lshortfile)
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
