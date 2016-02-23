package main

import (
	"flag"
	"logcabin"

	"configurate"
	"fmt"
	"os"

	"templeton/database"
	"templeton/elasticsearch"
)

var (
	logger             = logcabin.New()
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
		logger.Fatal(err)
	}
}

func loadElasticsearchConfig() {
	var err error
	elasticsearchBase, err = configurate.C.String("elasticsearch.base")
	if err != nil {
		logger.Fatal(err)
	}
	elasticsearchIndex, err = configurate.C.String("elasticsearch.index")
	if err != nil {
		logger.Fatal(err)
	}
}

func loadAMQPConfig() {
	var err error
	amqpURI, err = configurate.C.String("amqp.uri")
	if err != nil {
		logger.Fatal(err)
	}
}

func loadDBConfig() {
	var err error
	dbURI, err = configurate.C.String("db.uri")
	if err != nil {
		logger.Fatal(err)
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
		logger.Fatal(err)
	}
	defer es.Close()

	loadDBConfig()
	d, err := database.NewDatabaser(dbURI)
	if err != nil {
		logger.Fatal(err)
	}

	if *mode == "full" {
		logger.Println("Full indexing mode selected.")

		es.Reindex(d)
		return
	}

	loadAMQPConfig()

	if *mode == "periodic" {
		logger.Println("Periodic indexing mode selected.")

		// TODO: AMQP listener triggering same steps as full mode
		return
	}

	if *mode == "incremental" {
		logger.Println("Incremental indexing mode selected.")

		// TODO: AMQP listener triggering incremental updates
		return
	}
}
