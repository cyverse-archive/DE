package elasticsearch

import (
	"fmt"
	"logcabin"

	"gopkg.in/olivere/elastic.v3"

	"templeton/database"
	"templeton/model"
)

var (
	// knownTypes is a mapping which stores known types to index
	knownTypes = map[string]bool{
		"file":   true,
		"folder": true,
	}
)

// Elasticer is a type used to interact with Elasticsearch
type Elasticer struct {
	es      *elastic.Client
	baseURL string
	index   string
}

// NewElasticer returns a pointer to an Elasticer instance that has already tested its connection
// by making a WaitForStatus call to the configured Elasticsearch cluster
func NewElasticer(elasticsearchBase string, elasticsearchIndex string) (*Elasticer, error) {
	c, err := elastic.NewClient(elastic.SetURL(elasticsearchBase))

	if err != nil {
		return nil, err
	}

	return &Elasticer{es: c, baseURL: elasticsearchBase, index: elasticsearchIndex}, nil
}

func (e *Elasticer) Close() {
	e.es.Stop()
}

type BulkIndexer struct {
	es          *elastic.Client
	bulkSize    int
	bulkService *elastic.BulkService
}

func (e *Elasticer) NewBulkIndexer(bulkSize int) *BulkIndexer {
	return &BulkIndexer{bulkSize: bulkSize, es: e.es, bulkService: e.es.Bulk()}
}

func (b *BulkIndexer) Add(r elastic.BulkableRequest) error {
	b.bulkService.Add(r)
	if b.bulkService.NumberOfActions() >= b.bulkSize {
		err := b.Flush()
		if err != nil {
			return err
		}
	}
	return nil
}

func (b *BulkIndexer) Flush() error {
	_, err := b.bulkService.Do()
	if err != nil {
		return err
	}

	b.bulkService = b.es.Bulk()

	return nil
}

func (e *Elasticer) PurgeType(d *database.Databaser, indexer *BulkIndexer, t string) error {
	scanner, err := e.es.Scan(e.index).Type(t).Scroll("1m").Fields("_id").Do()
	if err != nil {
		return err
	}

	for {
		docs, err := scanner.Next()
		if err == elastic.EOS {
			logcabin.Info.Printf("Finished all rows for purge of %s.", t)
			break
		}
		if err != nil {
			return err
		}

		if docs.TotalHits() > 0 {
			for _, hit := range docs.Hits.Hits {
				avus, err := d.GetObjectAVUs(hit.Id)
				if err != nil {
					logcabin.Error.Printf("Error processing %s/%s: %s", t, hit.Id, err)
					continue
				}
				if len(avus) == 0 {
					logcabin.Info.Printf("Deleting %s/%s", t, hit.Id)
					req := elastic.NewBulkDeleteRequest().Index(e.index).Type(t).Routing(hit.Id).Id(hit.Id)
					err = indexer.Add(req)
					if err != nil {
						logcabin.Error.Printf("Error enqueuing delete of %s/%s: %s", t, hit.Id, err)
					}
				}
			}
		}
	}
	return nil
}

// PurgeIndex walks an index querying a database, deleting those which should not exist
func (e *Elasticer) PurgeIndex(d *database.Databaser) {
	indexer := e.NewBulkIndexer(1000)
	defer indexer.Flush()

	err := e.PurgeType(d, indexer, "file_metadata")
	if err != nil {
		logcabin.Error.Fatal(err)
		return
	}

	err = e.PurgeType(d, indexer, "folder_metadata")
	if err != nil {
		logcabin.Error.Fatal(err)
		return
	}
}

// IndexEverything creates a bulk indexer and takes a database, and iterates to index its contents
func (e *Elasticer) IndexEverything(d *database.Databaser) {
	indexer := e.NewBulkIndexer(1000)
	defer indexer.Flush()

	cursor, err := d.GetAllObjects()
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer cursor.Close()

	for {
		avus, err := cursor.Next()
		if err == database.EOS {
			logcabin.Info.Print("Done all rows, finishing.")
			break
		}
		if err != nil {
			logcabin.Error.Print(err)
			break
		}

		formatted, err := model.AVUsToIndexedObject(avus)
		if err != nil {
			logcabin.Error.Print(err)
			break
		}

		if knownTypes[avus[0].TargetType] {
			indexed_type := fmt.Sprintf("%s_metadata", avus[0].TargetType)
			logcabin.Info.Printf("Indexing %s/%s", indexed_type, formatted.ID)

			req := elastic.NewBulkIndexRequest().Index(e.index).Type(indexed_type).Parent(formatted.ID).Id(formatted.ID).Doc(formatted)
			err = indexer.Add(req)
			if err != nil {
				logcabin.Error.Print(err)
				break
			}
		}
	}
}

func (e *Elasticer) Reindex(d *database.Databaser) {
	e.PurgeIndex(d)
	e.IndexEverything(d)
}

func (e *Elasticer) DeleteOne(id string) {
	logcabin.Info.Printf("Deleting metadata for %s", id)
	_, fileErr := e.es.Delete().Index(e.index).Type("file_metadata").Parent(id).Id(id).Do()
	_, folderErr := e.es.Delete().Index(e.index).Type("folder_metadata").Parent(id).Id(id).Do()
	if fileErr != nil && folderErr != nil {
		logcabin.Error.Printf("Error deleting file metadata for %s: %s", id, fileErr)
		logcabin.Error.Printf("Error deleting folder metadata for %s: %s", id, folderErr)
	}
	return
}

// IndexOne takes a database and one ID and reindexes that one entity. It should not die or throw errors.
func (e *Elasticer) IndexOne(d *database.Databaser, id string) {
	avus, err := d.GetObjectAVUs(id)
	if err != nil {
		logcabin.Error.Print(err)
		return
	}

	formatted, err := model.AVUsToIndexedObject(avus)
	if err == model.NoAVUs {
		e.DeleteOne(id)
		return
	}
	if err != nil {
		logcabin.Error.Print(err)
		return
	}

	if knownTypes[avus[0].TargetType] {
		indexed_type := fmt.Sprintf("%s_metadata", avus[0].TargetType)
		logcabin.Info.Printf("Indexing %s/%s", indexed_type, formatted.ID)
		_, err = e.es.Index().Index(e.index).Type(indexed_type).Parent(formatted.ID).Id(formatted.ID).BodyJson(formatted).Do()
		if err != nil {
			logcabin.Error.Print(err)
		}
	}
	return
}
