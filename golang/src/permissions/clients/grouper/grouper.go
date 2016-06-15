package grouper

import (
	"database/sql"

	"permissions/util"

	_ "github.com/lib/pq"
)

type GroupInfo struct {
	ID   string
	Name string
}

type Grouper interface {
	GroupsForSubject(string) ([]*GroupInfo, error)
}

// Note: the grouper client is intended to be a read-only client. Explicit transactions are not
// used here for that reason.
type GrouperClient struct {
	db     *sql.DB
	prefix string
}

func NewGrouperClient(dburi, prefix string) (*GrouperClient, error) {
	connector, err := util.NewDefaultConnector("1m")
	if err != nil {
		return nil, err
	}

	db, err := connector.Connect("postgres", dburi)
	if err != nil {
		return nil, err
	}

	return &GrouperClient{
		db:     db,
		prefix: prefix,
	}, nil
}

func (gc *GrouperClient) GroupsForSubject(subjectId string) ([]*GroupInfo, error) {

	// Query the database.
	query := `SELECT group_id, group_name FROM grouper_memberships_v
            WHERE subject_id = $1 AND group_name LIKE $2`
	rows, err := gc.db.Query(query, subjectId, gc.prefix+"%")
	if err != nil {
		return nil, err
	}

	// Extract the groups from the database.
	groups := make([]*GroupInfo, 0)
	for rows.Next() {
		var group GroupInfo
		if err := rows.Scan(&group.ID, &group.Name); err != nil {
			return nil, err
		}
		groups = append(groups, &group)
	}

	return groups, nil
}
