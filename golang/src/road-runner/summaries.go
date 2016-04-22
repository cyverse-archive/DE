package main

import (
	"encoding/csv"
	"io"
	"model"
	"os"
	"path"
)

func writeCSV(fileWriter io.Writer, records [][]string) (err error) {
	writer := csv.NewWriter(fileWriter)
	for _, record := range records {
		if err = writer.Write(record); err != nil {
			return err
		}
	}
	writer.Flush()
	return writer.Error()
}

func writeJobSummary(outputDir string, job *model.Job) error {
	outputPath := path.Join(outputDir, "JobSummary.csv")
	fileWriter, err := os.Create(outputPath)
	if err != nil {
		return err
	}
	defer fileWriter.Close()
	records := [][]string{
		{"Job ID", job.InvocationID},
		{"Job Name", job.Name},
		{"Application ID", job.AppID},
		{"Application Name", job.AppName},
		{"Submitted By", job.Submitter},
	}
	return writeCSV(fileWriter, records)
}

func stepToRecord(step *model.Step) [][]string {
	var retval [][]string
	params := step.Config.Parameters()
	for _, p := range params {
		retval = append(retval, []string{
			step.Executable(),
			p.Name,
			p.Value,
		})
	}
	return retval
}

func writeJobParameters(outputDir string, job *model.Job) error {
	outputPath := path.Join(outputDir, "JobParameters.csv")
	fileWriter, err := os.Create(outputPath)
	if err != nil {
		return err
	}
	defer fileWriter.Close()
	records := [][]string{
		{"Executable", "Argument Option", "Argument Value"},
	}
	for _, s := range job.Steps {
		stepRecords := stepToRecord(&s)
		for _, sr := range stepRecords {
			records = append(records, sr)
		}
	}
	return writeCSV(fileWriter, records)
}
