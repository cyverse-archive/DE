package db

import (
	"permissions/models"
)

type SubjectDto struct {
	ID          string
	SubjectID   string
	SubjectType string
}

func (s *SubjectDto) ToSubjectOut() *models.SubjectOut {
	var subjectOut models.SubjectOut

	subjectOut.ID = models.InternalSubjectID(s.ID)
	subjectOut.SubjectID = models.ExternalSubjectID(s.SubjectID)
	subjectOut.SubjectType = models.SubjectType(s.SubjectType)

	return &subjectOut
}
