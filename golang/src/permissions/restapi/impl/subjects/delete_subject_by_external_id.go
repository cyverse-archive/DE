package subjects

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/subjects"
)

func deleteSubjectByExternalIdInternalServerError(reason string) middleware.Responder {
	return subjects.NewDeleteSubjectByExternalIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func deleteSubjectByExternalIdNotFound(reason string) middleware.Responder {
	return subjects.NewDeleteSubjectByExternalIDNotFound().WithPayload(&models.ErrorOut{&reason})
}

func deleteSubjectByExternalIdOk() middleware.Responder {
	return subjects.NewDeleteSubjectByExternalIDOK()
}

func BuildDeleteSubjectByExternalIdHandler(
	db *sql.DB,
) func(subjects.DeleteSubjectByExternalIDParams) middleware.Responder {

	// Return the handler function.
	return func(params subjects.DeleteSubjectByExternalIDParams) middleware.Responder {
		subjectId := models.ExternalSubjectID(params.SubjectID)
		subjectType := models.SubjectType(params.SubjectType)

		// Start a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return deleteSubjectByExternalIdInternalServerError(err.Error())
		}

		// Look up the subject.
		subject, err := permsdb.GetSubject(tx, subjectId, subjectType)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteSubjectByExternalIdInternalServerError(err.Error())
		}
		if subject == nil {
			tx.Rollback()
			reason := fmt.Sprintf("subject not found: %s:%s", string(subjectType), string(subjectId))
			return deleteSubjectByExternalIdNotFound(reason)
		}

		// Delete the subject.
		if err := permsdb.DeleteSubject(tx, subject.ID); err != nil {
			tx.Rollback()
			return deleteSubjectByExternalIdInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteSubjectByExternalIdInternalServerError(err.Error())
		}

		return deleteSubjectByExternalIdOk()
	}
}
