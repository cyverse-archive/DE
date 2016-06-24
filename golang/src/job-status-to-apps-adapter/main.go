// job-status-to-apps-adapter
//
// This service periodically queries the DE database's job-status-updates table
// for new entries and propagates them up through the apps services's API, which
// eventually triggers job notifications in the UI.
//
// This service works by first querying for all jobs that have unpropagated
// statuses, iterating through each job and propagating all unpropagated
// status in the correct order. It records each attempt and will not re-attempt
// a propagation if the number of retries exceeds the configured maximum number
// of retries (which defaults to 3).
//
package main

import (
	"bytes"
	"configurate"
	"database/sql"
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"logcabin"
	"messaging"
	"net/http"
	"os"
	"time"
	"version"

	_ "github.com/lib/pq"
	"github.com/olebedev/config"
)

// JobStatusUpdate contains the data POSTed to the apps service.
type JobStatusUpdate struct {
	Status         string `json:"status"`
	CompletionDate string `json:"completion_date,omitempty"`
	UUID           string `json:"uuid"`
}

// JobStatusUpdateWrapper wraps a JobStatusUpdate
type JobStatusUpdateWrapper struct {
	State JobStatusUpdate `json:"state"`
}

// DBJobStatusUpdate represents a row from the job_status_updates table
type DBJobStatusUpdate struct {
	ID                     string
	ExternalID             string
	Message                string
	Status                 string
	SentFrom               string
	SentFromHostname       string
	SentOn                 int64
	Propagated             bool
	PropagationAttempts    int64
	LastPropagationAttempt sql.NullInt64
	CreatedDate            time.Time
}

// Unpropagated returns a []string of the UUIDs for jobs that have steps that
// haven't been propagated yet.
func Unpropagated(d *sql.DB) ([]string, error) {
	queryStr := `
	select distinct external_id
	  from job_status_updates
	 where propagated = 'false'`
	rows, err := d.Query(queryStr)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var retval []string
	for rows.Next() {
		var extID string
		err = rows.Scan(&extID)
		if err != nil {
			return nil, err
		}
		retval = append(retval, extID)
	}
	err = rows.Err()
	return retval, err
}

// Propagator looks for job status updates in the database and pushes them to
// the apps service if they haven't been successfully pushed there yet.
type Propagator struct {
	db       *sql.DB
	tx       *sql.Tx
	rollback bool
	appsURI  string
}

// NewPropagator returns a *Propagator that has been initialized with a new
// transaction.
func NewPropagator(d *sql.DB, appsURI string) (*Propagator, error) {
	t, err := d.Begin()
	if err != nil {
		return nil, err
	}
	return &Propagator{
		db:      d,
		tx:      t,
		appsURI: appsURI,
	}, nil
}

// Finished commits the transaction held by the *Propagator.
func (p *Propagator) Finished() error {
	if p.rollback {
		return p.tx.Rollback()
	}
	return p.tx.Commit()
}

// Propagate pushes the update to the apps service.
func (p *Propagator) Propagate(status *DBJobStatusUpdate) error {
	jsu := JobStatusUpdate{
		Status: status.Status,
		UUID:   status.ExternalID,
	}

	if jsu.Status == string(messaging.SucceededState) || jsu.Status == string(messaging.FailedState) {
		jsu.CompletionDate = fmt.Sprintf("%d", time.Now().UnixNano()/int64(time.Millisecond))
	}

	jsuw := JobStatusUpdateWrapper{
		State: jsu,
	}

	logcabin.Info.Printf("Job status in the propagate function for job %s is: %#v", jsu.UUID, jsuw)
	msg, err := json.Marshal(jsuw)
	if err != nil {
		logcabin.Error.Print(err)
		return err
	}

	buf := bytes.NewBuffer(msg)
	if err != nil {
		logcabin.Error.Print(err)
		return err
	}

	logcabin.Info.Printf("Message to propagate: %s", string(msg))

	logcabin.Info.Printf("Sending job status to %s in the propagate function for job %s", p.appsURI, jsu.UUID)
	resp, err := http.Post(p.appsURI, "application/json", buf)
	if err != nil {
		logcabin.Error.Printf("Error sending job status to %s in the propagate function for job %s: %#v", p.appsURI, jsu.UUID, err)
		return err
	}
	defer resp.Body.Close()

	logcabin.Info.Printf("Response from %s in the propagate function for job %s is: %s", p.appsURI, jsu.UUID, resp.Status)
	if resp.StatusCode < 200 || resp.StatusCode > 299 {
		return errors.New("bad response")
	}

	return nil
}

// JobUpdates returns a list of JobUpdate's that are sorted by their SentOn
// field.
func (p *Propagator) JobUpdates(extID string) ([]DBJobStatusUpdate, error) {
	queryStr := `
	select id,
				 external_id,
				 message,
				 status,
				 sent_from,
				 sent_from_hostname,
				 sent_on,
				 propagated,
				 propagation_attempts,
				 last_propagation_attempt,
				 created_date
	  from job_status_updates
	 where external_id = $1
	order by sent_on asc`
	rows, err := p.tx.Query(queryStr, extID)
	if err != nil {
		p.rollback = true
		return nil, err
	}
	defer rows.Close()
	var retval []DBJobStatusUpdate
	for rows.Next() {
		r := DBJobStatusUpdate{}
		err = rows.Scan(
			&r.ID,
			&r.ExternalID,
			&r.Message,
			&r.Status,
			&r.SentFrom,
			&r.SentFromHostname,
			&r.SentOn,
			&r.Propagated,
			&r.PropagationAttempts,
			&r.LastPropagationAttempt,
			&r.CreatedDate,
		)
		if err != nil {
			p.rollback = true
			return nil, err
		}
		retval = append(retval, r)
	}
	err = rows.Err()
	if err != nil {
		p.rollback = true
	}
	return retval, err
}

// MarkPropagated marks the job as propagated in the database as part of the
// transaction tracked by the *Propagator.
func (p *Propagator) MarkPropagated(id string) error {
	updateStr := `UPDATE ONLY job_status_updates SET propagated = 'true' where id = $1`
	_, err := p.tx.Exec(updateStr, id)
	return err
}

// LastPropagated returns the index in the list of []DBJobStatusUpdates that
// contains the last update that is marked as propagated in the database.
func LastPropagated(updates []DBJobStatusUpdate) int {
	lastID := 0
	for idx, update := range updates {
		if update.Propagated {
			lastID = idx
		}
	}
	return lastID
}

// StorePropagationAttempts stores an incremented value for the update's
// propagation_attempts field.
func (p *Propagator) StorePropagationAttempts(update *DBJobStatusUpdate) error {
	newVal := update.PropagationAttempts
	id := update.ID
	lastAttemptTime := time.Now().UnixNano() / int64(time.Millisecond)
	insertStr := `UPDATE ONLY job_status_updates
												SET propagation_attempts = $2,
														last_propagation_attempt = $3
											WHERE id = $1`
	_, err := p.tx.Exec(insertStr, id, newVal, lastAttemptTime)
	return err
}

// ScanAndPropagate is contains the core logic. Here's what it does:
// * Gets all job IDs with a status update that hasn't been propagated yet.
// * For each of those jobs, start a database transaction and get all of the
//   associcated status updates from the database.
// * Mark any unpropagated updates that appear __before__ a propagated update as
//   propagated.
// * Propagate any unpropagated steps that appear after the last propagated
//   update.
// * Commit the transaction.
func ScanAndPropagate(d *sql.DB, maxRetries int64, appsURI string) error {
	unpropped, err := Unpropagated(d)
	if err != nil {
		return err
	}

	for _, jobExtID := range unpropped {
		proper, err := NewPropagator(d, appsURI)
		if err != nil {
			return err
		}

		updates, err := proper.JobUpdates(jobExtID)
		if err != nil {
			return err
		}

		for _, subupdates := range updates {
			if !subupdates.Propagated && subupdates.PropagationAttempts < maxRetries {
				logcabin.Info.Printf("Propagating %#v", subupdates)
				if err = proper.Propagate(&subupdates); err != nil {
					logcabin.Error.Print(err)
					subupdates.PropagationAttempts = subupdates.PropagationAttempts + 1
					if err = proper.StorePropagationAttempts(&subupdates); err != nil {
						logcabin.Error.Print(err)
					}
					continue
				}
				logcabin.Info.Printf("Marking update %s as propagated", subupdates.ID)
				if err = proper.MarkPropagated(subupdates.ID); err != nil {
					logcabin.Error.Print(err)
					continue
				}
			}
		}

		if err = proper.Finished(); err != nil {
			logcabin.Error.Print(err)
		}
	}

	return nil
}

func main() {
	var (
		cfgPath     = flag.String("config", "", "Path to the config file. Required.")
		showVersion = flag.Bool("version", false, "Print the version information")
		dbURI       = flag.String("db", "", "The URI used to connect to the database")
		maxRetries  = flag.Int64("retries", 3, "The maximum number of propagation retries to make")
		err         error
		cfg         *config.Config
		db          *sql.DB
		appsURI     string
	)

	flag.Parse()

	logcabin.Init("job-status-to-apps-adapter", "job-status-to-apps-adapter")

	if *showVersion {
		version.AppVersion()
		os.Exit(0)
	}

	if *cfgPath == "" {
		fmt.Println("Error: --config must be set.")
		flag.PrintDefaults()
		os.Exit(-1)
	}

	cfg, err = configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Print(err)
		os.Exit(-1)
	}

	logcabin.Info.Println("Done reading config.")

	if *dbURI == "" {
		*dbURI, err = cfg.String("db.uri")
		if err != nil {
			logcabin.Error.Fatal(err)
		}
	} else {
		cfg.Set("db.uri", *dbURI)
	}

	appsURI, err = cfg.String("apps.callbacks_uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	logcabin.Info.Println("Connecting to the database...")
	db, err = sql.Open("postgres", *dbURI)
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	err = db.Ping()
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Println("Connected to the database")

	for {
		if err = ScanAndPropagate(db, *maxRetries, appsURI); err != nil {
			logcabin.Error.Fatal(err)
		}
		time.Sleep(5 * time.Second)
	}
}
