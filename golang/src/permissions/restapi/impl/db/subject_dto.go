package db

import (
	"permissions/models"
)

type SubjectDto struct {
	ID          string
	SubjectID   string
	SubjectType string
}

func NewSubjectDto(
	id *models.InternalSubjectID,
	subjectId *models.ExternalSubjectID,
	subjectType *models.SubjectType,
) *SubjectDto {
	var subjectDto SubjectDto

	if id != nil {
		subjectDto.ID = string(*id)
	}
	if subjectId != nil {
		subjectDto.SubjectID = string(*subjectId)
	}
	if subjectType != nil {
		subjectDto.SubjectType = string(*subjectType)
	}

	return &subjectDto
}

func (s *SubjectDto) ToSubjectOut() *models.SubjectOut {
	var subjectOut models.SubjectOut

	subjectOut.ID = models.InternalSubjectID(s.ID)
	subjectOut.SubjectID = models.ExternalSubjectID(s.SubjectID)
	subjectOut.SubjectType = models.SubjectType(s.SubjectType)

	return &subjectOut
}
