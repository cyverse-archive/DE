package database

import (
	"database/sql"
	"fmt"

	"templeton/model"

	_ "github.com/lib/pq"
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
	       attribute,
	       value,
	       unit,
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

// GetAllObjects returns a function to iterate through individual objects' worth of AVURecords, and a function to clean up
// The function it returns will return nil if all records have been read.
func (d *Databaser) GetAllObjects() (func() ([]model.AVURecord, error), func(), error) {
	query := selectAVUsWhere("")

	rows, err := d.db.Query(query)
	endFunc := func() { rows.Close() }
	if err != nil {
		return nil, endFunc, err
	}

	lastRow := &model.AVURecord{TargetId: ""}
	moreRows := true

	return func() ([]model.AVURecord, error) {
		if !moreRows {
			return nil, nil
		}
		var retval []model.AVURecord
		if lastRow.TargetId != "" {
			retval = append(retval, *lastRow)
		}
		for moreRows {
			moreRows = rows.Next()
			if !moreRows {
				break
			}
			ar, err := avuRecordFromRow(rows)
			if err != nil {
				return nil, err
			}
			if lastRow.TargetId == "" || lastRow.TargetId == ar.TargetId {
				lastRow = ar
				retval = append(retval, *ar)
			} else {
				lastRow = ar
				break
			}
		}
		err = rows.Err()
		return retval, err
	}, endFunc, err
}
