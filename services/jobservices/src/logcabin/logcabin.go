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
	Trace	*log.Logger
	Info	*log.Logger
	Warning	*log.Logger
	Error	*log.Logger
)

// Log Level Constants
const (
	trace_lvl = "TRACE"
	info_lvl = "INFO"
	warn_lvl = "WARN"
	err_lvl = "ERR"
)

func Init(service, artifact string) {
	Trace = log.New(&lincoln{service, artifact, trace_lvl}, "", log.Lshortfile)
	Info = log.New(&lincoln{service, artifact, info_lvl}, "", log.Lshortfile)
	Warning = log.New(&lincoln{service, artifact, warn_lvl}, "", log.Lshortfile)
	Error = log.New(&lincoln{service, artifact, err_lvl}, "", log.Lshortfile)
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
type lincoln struct {
	service string
	artifact string
	level string
}

// NewLogMessage returns a pointer to a new instance of LogMessage.
func (l *lincoln) newLogMessage(message string) *logMessage {
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

func (l *lincoln) Write(buf []byte) (n int, err error) {
	m := l.newLogMessage(string(buf[:]))
	j, err := json.Marshal(m)
	if err != nil {
		return 0, err
	}
	j = append(j, []byte("\n")...)
	return os.Stdout.Write(j)
}

