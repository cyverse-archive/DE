package db

import (
	"database/sql/driver"
	"testing"
)

func extractValue(t *testing.T, sa *StringArray) driver.Value {
	val, err := sa.Value()
	if err != nil {
		t.Fatalf("unexpected error returned: %s", err)
	}
	return val
}

func TestStringArray(t *testing.T) {
	val := extractValue(t, &StringArray{"foo", "bar", "baz"})
	if val.(string) != "{\"foo\",\"bar\",\"baz\"}" {
		t.Errorf("unexpected value returned: %s", val.(string))
	}
}

func TestStringArrayMetachars(t *testing.T) {
	val := extractValue(t, &StringArray{"foo", "bar", "baz\"s", "quux\\s"})
	if val.(string) != "{\"foo\",\"bar\",\"baz\\\"s\",\"quux\\\\s\"}" {
		t.Errorf("unexpected value returned: %s", val.(string))
	}
}
