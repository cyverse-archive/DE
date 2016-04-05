// job-status-recorder
//
// This service listens for job updates sent via AMQP on the "jobs" exchange
// with a key of "jobs.updates". Each update is recorded in the DE database's
// job_status_updates table.
//
package main

import (
	"configurate"
	"database/sql"
	"encoding/json"
	"flag"
	"fmt"
	"logcabin"
	"messaging"
	"net"
	"os"
	"strconv"

	_ "github.com/lib/pq"
	"github.com/streadway/amqp"
)

var (
	version    = flag.Bool("version", false, "Print the version information")
	cfgPath    = flag.String("config", "", "The path to the config file")
	dbURI      = flag.String("db", "", "The URI used to connect to the database")
	amqpURI    = flag.String("amqp", "", "The URI used to connect to the amqp broker")
	gitref     string
	appver     string
	builtby    string
	amqpClient *messaging.Client
	db         *sql.DB
)

func init() {
	flag.Parse()
	logcabin.Init("job-status-recorder", "job-status-recorder")
}

// AppVersion prints version information to stdout
func AppVersion() {
	if appver != "" {
		fmt.Printf("App-Version: %s\n", appver)
	}
	if gitref != "" {
		fmt.Printf("Git-Ref: %s\n", gitref)
	}

	if builtby != "" {
		fmt.Printf("Built-By: %s\n", builtby)
	}
}

func insert(state, invID, msg, host, ip string, sentOn int64) (sql.Result, error) {
	insertStr := `
		INSERT INTO job_status_updates (
			external_id,
			message,
			status,
			sent_from,
			sent_from_hostname,
			sent_on
		) VALUES (
			$1,
			$2,
			$3,
			$4,
			$5,
			$6
		) RETURNING id`
	return db.Exec(insertStr, invID, msg, state, ip, host, sentOn)
}

func msg(delivery amqp.Delivery) {
	if err := delivery.Ack(false); err != nil {
		logcabin.Error.Print(err)
	}
	logcabin.Info.Println("Message received")
	update := &messaging.UpdateMessage{}
	err := json.Unmarshal(delivery.Body, update)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	if update.State == "" {
		logcabin.Warning.Println("State was unset, dropping update")
		return
	}
	logcabin.Info.Printf("State is %s\n", update.State)
	if update.Job.InvocationID == "" {
		logcabin.Warning.Println("InvocationID was unset, dropping update")
	}
	logcabin.Info.Printf("InvocationID is %s\n", update.Job.InvocationID)
	if update.Message == "" {
		logcabin.Warning.Println("Message set to empty string, setting to UNKNOWN")
		update.Message = "UNKNOWN"
	}
	logcabin.Info.Printf("Message is: %s", update.Message)
	var sentFromAddr string
	if update.Sender == "" {
		logcabin.Warning.Println("Unknown sender, setting from address to 0.0.0.0")
		update.Sender = "0.0.0.0"
	}
	parsedIP := net.ParseIP(update.Sender)
	if parsedIP != nil {
		sentFromAddr = update.Sender
	} else {
		ips, err := net.LookupIP(update.Sender)
		if err != nil {
			logcabin.Error.Print(err)
		} else {
			if len(ips) > 0 {
				sentFromAddr = ips[0].String()
			}
		}
	}
	logcabin.Info.Printf("Sent from: %s", sentFromAddr)
	logcabin.Info.Printf("Sent On, unparsed: %s", update.SentOn)
	sentOn, err := strconv.ParseInt(update.SentOn, 10, 64)
	if err != nil {
		logcabin.Error.Printf("Error parsing SentOn field, setting field to 0: %s", err)
		sentOn = 0
	}
	logcabin.Info.Printf("Sent On: %d", sentOn)
	result, err := insert(
		string(update.State),
		update.Job.InvocationID,
		update.Message,
		update.Sender,
		sentFromAddr,
		sentOn,
	)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	rowCount, err := result.RowsAffected()
	if err != nil {
		logcabin.Error.Print(err)
		return
	}
	logcabin.Info.Printf("Inserted %d rows\n", rowCount)
}

func main() {
	var err error

	if *version {
		AppVersion()
		os.Exit(0)
	}

	if *dbURI == "" || *amqpURI == "" {
		if *cfgPath == "" {
			logcabin.Error.Fatal("--config must be set.")
		}
		err := configurate.Init(*cfgPath)
		if err != nil {
			logcabin.Error.Fatal(err)
		}
		if *dbURI == "" {
			*dbURI, err = configurate.C.String("db.uri")
			if err != nil {
				logcabin.Error.Fatal(err)
			}
		}
		if *amqpURI == "" {
			*amqpURI, err = configurate.C.String("amqp.uri")
			if err != nil {
				logcabin.Error.Fatal(err)
			}
		}
	}
	logcabin.Info.Printf("AMQP broker setting is %s\n", *amqpURI)
	amqpClient, err := messaging.NewClient(*amqpURI, false)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer amqpClient.Close()
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
	go amqpClient.Listen()
	amqpClient.AddConsumer(messaging.JobsExchange, "topic", "job_status_recorder", messaging.UpdatesKey, msg)
	spinner := make(chan int)
	<-spinner
}
