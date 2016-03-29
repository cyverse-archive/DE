package model

import (
	"fmt"
	"time"
)

var (
	NoAVUs = fmt.Errorf("templeton/model: No AVUs provided to AVUsToIndexedObject")
)

// AVURecord is a type that contains info from the avus table
type AVURecord struct {
	ID         string
	Attribute  string
	Value      string
	Unit       string
	TargetId   string
	TargetType string
	CreatedBy  string
	ModifiedBy string
	CreatedOn  time.Time
	ModifiedOn time.Time
}

// IndexedAVU is a type that contains a single AVU as represented in ES
type IndexedAVU struct {
	Attribute string `json:"attribute"`
	Value     string `json:"value"`
	Unit      string `json:"unit"`
}

// IndexedObject is a type that contains info as it is sent to and recieved from ES
type IndexedObject struct {
	ID       string       `json:"id"`
	Metadata []IndexedAVU `json:"metadata"`
}

// avuRecordToIndexedAVU turns a AVURecord into a *IndexedAVU
func avuRecordToIndexedAVU(avu AVURecord) (*IndexedAVU, error) {
	ia := &IndexedAVU{Attribute: avu.Attribute, Value: avu.Value, Unit: avu.Unit}
	return ia, nil
}

// AVUsToIndexedObject takes []AVURecord and creates a *IndexedObject
func AVUsToIndexedObject(avus []AVURecord) (*IndexedObject, error) {
	if len(avus) == 0 {
		return nil, NoAVUs
	}
	var ias []IndexedAVU
	for _, avu := range avus {
		ia, err := avuRecordToIndexedAVU(avu)
		if err != nil {
			return nil, err
		}
		ias = append(ias, *ia)
	}
	retval := &IndexedObject{ID: avus[0].TargetId, Metadata: ias}
	return retval, nil
}

type UpdateMessage struct {
	ID     string `json:"entity"`
	Author string `json:"author"`
}
