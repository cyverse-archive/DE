package database

import (
	"database/sql"
	"errors"
	"fmt"

	"logcabin"

	"templeton/model"

	_ "github.com/lib/pq"
)

var (
	EOS = errors.New("EOS")
)

// Databaser is a type used to interact with the database.
type Databaser struct {
	db         *sql.DB
	ConnString string
}

// NewDatabaser returns a pointer to a Databaser instance that has already
// connected to the database by calling Ping().
func NewDatabaser(connString string) (*Databaser, error) {
	db, err := sql.Open("postgres", connString)
	if err != nil {
		return nil, err
	}
	err = db.Ping()
	if err != nil {
		return nil, err
	}
	databaser := &Databaser{
		db:         db,
		ConnString: connString,
	}
	return databaser, nil
}

// avuRecordFromRow converts a sql.Rows from a result set to a AVU record
// It would be great if they'd provided an interface for *this* Scan method
// (sql.Scanner is for the other one) but we'll just have to live with being
// unable to use QueryRow for this
func avuRecordFromRow(row *sql.Rows) (*model.AVURecord, error) {
	ar := &model.AVURecord{}

	err := row.Scan(
		&ar.ID,
		&ar.Attribute,
		&ar.Value,
		&ar.Unit,
		&ar.TargetId,
		&ar.TargetType,
		&ar.CreatedBy,
		&ar.ModifiedBy,
		&ar.CreatedOn,
		&ar.ModifiedOn,
	)

	return ar, err
}

const _selectAVU = `
	SELECT cast(id as varchar),
	       coalesce(attribute, ''),
	       coalesce(value, ''),
	       coalesce(unit, ''),
	       cast(target_id as varchar),
	       cast(target_type as varchar),
	       created_by,
	       modified_by,
	       created_on,
	       modified_on
	  FROM avus
`

// selectAVUsWhere generates a SELECT FROM avus with a given WHERE clause (or no WHERE, given an empty string)
func selectAVUsWhere(where string) string {
	if where != "" {
		return fmt.Sprintf("%s WHERE %s ORDER BY target_id", _selectAVU, where)
	}
	return fmt.Sprintf("%s ORDER BY target_id", _selectAVU)
}

// GetAVU returns a model.AVURecord from the database
func (d *Databaser) GetAVU(uuid string) (*model.AVURecord, error) {
	query := selectAVUsWhere("id = cast($1 as uuid)")
	rows, err := d.db.Query(query, uuid)
	defer rows.Close()
	if err != nil {
		return nil, err
	}
	if !rows.Next() {
		err := rows.Err()
		if err == nil {
			err = sql.ErrNoRows
		}
		return nil, err
	}
	ar, err := avuRecordFromRow(rows)
	if err != nil {
		return nil, err
	}
	if rows.Next() {
		return ar, fmt.Errorf("AVU Query for %s returned more than one row", uuid)
	}
	return ar, nil
}

// GetObjectAVUs returns a slice of model.AVURecord structs by UUID
func (d *Databaser) GetObjectAVUs(uuid string) ([]model.AVURecord, error) {
	query := selectAVUsWhere("target_id = cast($1 as uuid)")

	rows, err := d.db.Query(query, uuid)
	defer rows.Close()
	if err != nil {
		return nil, err
	}
	var retval []model.AVURecord
	for rows.Next() {
		ar, err := avuRecordFromRow(rows)
		if err != nil {
			return nil, err
		}
		retval = append(retval, *ar)
	}
	err = rows.Err()
	return retval, err
}

type objectCursor struct {
	rows     *sql.Rows
	lastRow  *model.AVURecord
	moreRows bool
	anyRows  bool
}

func newObjectCursor(rows *sql.Rows) *objectCursor {
	return &objectCursor{
		rows:     rows,
		lastRow:  &model.AVURecord{TargetId: ""},
		moreRows: true,
		anyRows:  false}
}

func (o *objectCursor) Next() ([]model.AVURecord, error) {
	if !o.moreRows {
		return nil, EOS
	}

	var retval []model.AVURecord

	if o.lastRow.TargetId != "" {
		retval = append(retval, *o.lastRow)
	}

	for o.moreRows {
		o.moreRows = o.rows.Next()
		if !o.moreRows {
			break
		}
		o.anyRows = true

		ar, err := avuRecordFromRow(o.rows)
		if err != nil {
			return nil, err
		}

		if o.lastRow.TargetId == "" || o.lastRow.TargetId == ar.TargetId {
			o.lastRow = ar
			retval = append(retval, *ar)
		} else {
			o.lastRow = ar
			break
		}
	}
	err := o.rows.Err()
	if err == nil && !o.anyRows {
		logcabin.Info.Print("No metadata was found in the configured database.")
		return nil, EOS
	}
	return retval, err
}

func (o *objectCursor) Close() {
	o.rows.Close()
}

// GetAllObjects returns a function to iterate through individual objects' worth of AVURecords, and a function to clean up
// The function it returns will return nil if all records have been read.
func (d *Databaser) GetAllObjects() (*objectCursor, error) {
	query := selectAVUsWhere("")

	rows, err := d.db.Query(query)
	if err != nil {
		return nil, err
	}

	return newObjectCursor(rows), nil
}
