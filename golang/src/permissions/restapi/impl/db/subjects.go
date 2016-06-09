package db

import (
	"database/sql"
	"fmt"
	"permissions/models"
)

func rowsToSubjectList(rows *sql.Rows) ([]*models.SubjectOut, error) {

	// Get the list of subjects.
	subjects := make([]*models.SubjectOut, 0)
	for rows.Next() {
		var subjectDto SubjectDto
		if err := rows.Scan(&subjectDto.ID, &subjectDto.SubjectID, &subjectDto.SubjectType); err != nil {
			return nil, err
		}
		subjects = append(subjects, subjectDto.ToSubjectOut())
	}

	return subjects, nil
}

func rowsToSubject(rows *sql.Rows, duplicateErr error) (*models.SubjectOut, error) {

	// Get the list of subjects.
	subjects, err := rowsToSubjectList(rows)
	if err != nil {
		return nil, err
	}

	// Check for duplicates. This shouldn't happen unless there's a bug in the query.
	if len(subjects) > 1 {
		return nil, duplicateErr
	}

	// Return the result.
	if len(subjects) < 1 {
		return nil, nil
	}
	return subjects[0], nil
}

func AddSubject(
	tx *sql.Tx,
	subjectId models.ExternalSubjectID,
	subjectType models.SubjectType,
) (*models.SubjectOut, error) {

	// Update the database.
	query := `INSERT INTO subjects (subject_id, subject_type) VALUES ($1, $2)
            RETURNING id, subject_id, subject_type`
	row := tx.QueryRow(query, string(subjectId), string(subjectType))

	// Return the subject information.
	var subjectDto SubjectDto
	if err := row.Scan(&subjectDto.ID, &subjectDto.SubjectID, &subjectDto.SubjectType); err != nil {
		return nil, err
	}
	return subjectDto.ToSubjectOut(), nil
}

func UpdateSubject(
	tx *sql.Tx,
	id models.InternalSubjectID,
	subjectId models.ExternalSubjectID,
	subjectType models.SubjectType,
) (*models.SubjectOut, error) {

	// Update the database.
	query := `UPDATE subjects SET subject_id = $1, subject_type = $2
            WHERE id = $3
            RETURNING id, subject_id, subject_type`
	row := tx.QueryRow(query, string(subjectId), string(subjectType), string(id))

	// Return the subject information.
	var subjectDto SubjectDto
	if err := row.Scan(&subjectDto.ID, &subjectDto.SubjectID, &subjectDto.SubjectType); err != nil {
		return nil, err
	}
	return subjectDto.ToSubjectOut(), nil
}

func SubjectIdExists(tx *sql.Tx, subjectId models.ExternalSubjectID) (bool, error) {

	// Query the database.
	query := "SELECT count(*) FROM subjects WHERE subject_id = $1"
	row := tx.QueryRow(query, string(subjectId))

	// Get the result.
	var count uint32
	if err := row.Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

func SubjectExists(tx *sql.Tx, id models.InternalSubjectID) (bool, error) {

	// Query the database.
	query := "SELECT count(*) FROM subjects WHERE id = $1"
	row := tx.QueryRow(query, string(id))

	// Get the result.
	var count uint32
	if err := row.Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

func DuplicateSubjectExists(
	tx *sql.Tx,
	id models.InternalSubjectID,
	subjectId models.ExternalSubjectID,
) (bool, error) {

	// Query the database.
	query := "SELECT count(*) FROM subjects WHERE id != $1 and subject_id = $2"
	row := tx.QueryRow(query, string(id), string(subjectId))

	// Get the result.
	var count uint32
	if err := row.Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

func ListSubjects(tx *sql.Tx, subjectType, subjectId *string) ([]*models.SubjectOut, error) {

	// Query the database.
	var rows *sql.Rows
	var err error
	if subjectType != nil && subjectId != nil {
		query := `SELECT id, subject_id, subject_type FROM subjects
		          WHERE subject_type = $1 AND subject_id = $2
		          ORDER BY subject_type, subject_id`
		rows, err = tx.Query(query, *subjectType, *subjectId)
	} else if subjectType != nil {
		query := `SELECT id, subject_id, subject_type FROM subjects
		          WHERE subject_type = $1
		          ORDER BY subject_type, subject_id`
		rows, err = tx.Query(query, *subjectType)
	} else if subjectId != nil {
		query := `SELECT id, subject_id, subject_type FROM subjects
		          WHERE subject_id = $1
		          ORDER BY subject_type, subject_id`
		rows, err = tx.Query(query, *subjectId)
	} else {
		query := `SELECT id, subject_id, subject_type FROM subjects
		          ORDER BY subject_type, subject_id`
		rows, err = tx.Query(query)
	}
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the list of subjects.
	return rowsToSubjectList(rows)
}

func DeleteSubject(tx *sql.Tx, id models.InternalSubjectID) error {

	// Update the database.
	stmt := "DELETE FROM subjects WHERE id = $1"
	result, err := tx.Exec(stmt, string(id))
	if err != nil {
		return err
	}

	// Verify that a row was deleted.
	count, err := result.RowsAffected()
	if err != nil {
		return err
	}
	if count == 0 {
		return fmt.Errorf("no subjects deleted for id %s", id)
	}
	if count > 1 {
		return fmt.Errorf("multiple subjects deleted for id %s", id)
	}

	return nil
}

func GetSubject(
	tx *sql.Tx,
	subjectId models.ExternalSubjectID,
	subjectType models.SubjectType,
) (*models.SubjectOut, error) {

	// Get the subject information from the database.
	query := `SELECT id, subject_id, subject_type FROM subjects
            WHERE subject_id = $1 AND subject_type = $2`
	rows, err := tx.Query(query, string(subjectId), string(subjectType))
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the subject.
	duplicateErr := fmt.Errorf(
		"found multiple subjects with ID, %s, and type, %s", string(subjectId), string(subjectType),
	)
	return rowsToSubject(rows, duplicateErr)
}

func GetSubjectByExternalId(tx *sql.Tx, subjectId models.ExternalSubjectID) (*models.SubjectOut, error) {

	// Get the subject information from the database.
	query := "SELECT id, subject_id, subject_type FROM subjects WHERE subject_id = $1"
	rows, err := tx.Query(query, string(subjectId))
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the subject.
	duplicateErr := fmt.Errorf("found multiple subjects with ID, %s", string(subjectId))
	return rowsToSubject(rows, duplicateErr)
}
