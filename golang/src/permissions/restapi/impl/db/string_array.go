package db

import (
	"bytes"
	"database/sql/driver"
	"regexp"
)

type StringArray []string

func escape(s string) string {
	return "\\" + s
}

func (a *StringArray) Value() (driver.Value, error) {
	re := regexp.MustCompile("[\\\\\"]")

	// Build the string representation of the array.
	buf := bytes.NewBufferString("{")
	for n, s := range []string(*a) {
		if n != 0 {
			buf.WriteString(",")
		}
		buf.WriteString("\"")
		buf.WriteString(re.ReplaceAllStringFunc(s, escape))
		buf.WriteString("\"")
	}
	buf.WriteString("}")

	return buf.String(), nil
}
