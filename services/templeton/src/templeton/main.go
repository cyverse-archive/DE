package main

import (
	"flag"
	"logcabin"
	"messaging"

	"configurate"
	"fmt"
	"os"

	"templeton/database"
	"templeton/elasticsearch"

	"github.com/streadway/amqp"
)

var (
	version            = flag.Bool("version", false, "Print version information")
	mode               = flag.String("mode", "", "One of 'periodic', 'incremental', or 'full'. Required except for --version.")
	cfgPath            = flag.String("config", "", "Path to the configuration file. Required except for --version.")
	amqpURI            string
	elasticsearchBase  string
	elasticsearchIndex string
	dbURI              string
	gitref             string
	appver             string
	builtby            string
)

func init() {
	flag.Parse()
	logcabin.Init("templeton", "templeton")
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

func checkMode() {
	validModes := []string{"periodic", "incremental", "full"}
	foundMode := false

	for _, v := range validModes {
		if v == *mode {
			foundMode = true
		}
	}

	if !foundMode {
		fmt.Printf("Invalid mode: %s\n", *mode)
		flag.PrintDefaults()
		os.Exit(-1)
	}
}

func initConfig(cfgPath string) {
	err := configurate.Init(cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
}

func loadElasticsearchConfig() {
	var err error
	elasticsearchBase, err = configurate.C.String("elasticsearch.base")
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	elasticsearchIndex, err = configurate.C.String("elasticsearch.index")
	if err != nil {
		logcabin.Error.Fatal(err)
	}
}

func loadAMQPConfig() {
	var err error
	amqpURI, err = configurate.C.String("amqp.uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}
}

func loadDBConfig() {
	var err error
	dbURI, err = configurate.C.String("db.uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}
}

func doFullMode(es *elasticsearch.Elasticer, d *database.Databaser) {
	logcabin.Info.Println("Full indexing mode selected.")

	es.Reindex(d)
}

func doPeriodicMode(es *elasticsearch.Elasticer, d *database.Databaser, client *messaging.Client) {
	logcabin.Info.Println("Periodic indexing mode selected.")

	go client.Listen()

	// Accept and handle messages sent out with the index.all and index.templates routing keys
	client.AddConsumer(messaging.ReindexExchange, "direct", "templeton.reindexAll", messaging.ReindexAllKey, func(del amqp.Delivery) {
		es.Reindex(d)
		del.Ack(false)
	})
	client.AddConsumer(messaging.ReindexExchange, "direct", "templeton.reindexTemplates", messaging.ReindexTemplatesKey, func(del amqp.Delivery) {
		es.Reindex(d)
		del.Ack(false)
	})

	// spinner in order to keep the program running since client.Listen() is in a goroutine.
	spinner := make(chan int)
	for {
		select {
		case <-spinner:
			fmt.Println("Exiting")
			break
		}
	}
}

func main() {
	if *version {
		AppVersion()
		os.Exit(0)
	}

	checkMode()

	if *cfgPath == "" {
		fmt.Println("--config is required")
		flag.PrintDefaults()
		os.Exit(-1)
	}

	initConfig(*cfgPath)
	loadElasticsearchConfig()
	es, err := elasticsearch.NewElasticer(elasticsearchBase, elasticsearchIndex)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer es.Close()

	loadDBConfig()
	d, err := database.NewDatabaser(dbURI)
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	if *mode == "full" {
		doFullMode(es, d)
		return
	}

	loadAMQPConfig()

	client, err := messaging.NewClient(amqpURI, true)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer client.Close()

	if *mode == "periodic" {
		doPeriodicMode(es, d, client)
	}

	if *mode == "incremental" {
		logcabin.Info.Println("Incremental indexing mode selected.")

		// TODO: AMQP listener triggering incremental updates
	}
}
