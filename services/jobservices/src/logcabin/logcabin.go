package logcabin

import (
	"encoding/json"
	"log"
	"os"
	"time"
)

// var logger *Lincoln

// LogMessage represents a message that will be logged in JSON format.
type LogMessage struct {
	Service  string `json:"service"`
	Artifact string `json:"art-id"`
	Group    string `json:"group-id"`
	Level    string `json:"level"`
	Time     int64  `json:"timeMillis"`
	Message  string `json:"message"`
}

// Lincoln is a logger for jex-events.
type Lincoln struct {
	*log.Logger
	service, artifact string
}

// New returns a pointer to a newly initialized Lincoln.
func New(service, artifact string) *Lincoln {
	logger := &Lincoln{log.New(os.Stderr, "", log.Lshortfile), service, artifact}
	log.SetOutput(logger)
	log.SetPrefix("")
	return logger
}

// NewLogMessage returns a pointer to a new instance of LogMessage.
func (l *Lincoln) NewLogMessage(message string) *LogMessage {
	lm := &LogMessage{
		Service:  l.service,
		Artifact: l.artifact,
		Group:    "org.iplantc",
		Level:    "INFO",
		Time:     time.Now().UnixNano() / int64(time.Millisecond),
		Message:  message,
	}
	return lm
}

func (l *Lincoln) Write(buf []byte) (n int, err error) {
	m := l.NewLogMessage(string(buf[:]))
	j, err := json.Marshal(m)
	if err != nil {
		return 0, err
	}
	j = append(j, []byte("\n")...)
	return os.Stdout.Write(j)
}
