package queries

import "database/sql"

// IsUser returns whether the provided user exists in the database
func IsUser(db *sql.DB, username string) (bool, error) {
	var (
		count int64
		query = `SELECT COUNT(*) FROM ( SELECT DISTINCT id FROM users WHERE username = $1 ) AS check_user`
	)
	if err := db.QueryRow(query, username).Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

// UserID returns the user ID string for the given username
func UserID(db *sql.DB, username string) (string, error) {
	var (
		userID string
		query  = `SELECT id FROM users WHERE username = $1`
	)
	if err := db.QueryRow(query, username).Scan(&userID); err != nil {
		return "", err
	}
	return userID, nil
}
