package permission_lookup

import (
	"permissions/clients/grouper"
)

func groupIdsForSubject(grouperClient grouper.Grouper, subjectType, subjectId string) ([]string, error) {
	groupIds := make([]string, 0)

	// Simply return an empty slice if the subject is a group.
	if subjectType == "group" {
		return groupIds, nil
	}

	// Look up the groups.
	groups, err := grouperClient.GroupsForSubject(subjectId)
	if err != nil {
		return nil, err
	}

	// Extract the identifiers from the list of groups.
	for _, group := range groups {
		groupIds = append(groupIds, group.ID)
	}

	return groupIds, nil
}
