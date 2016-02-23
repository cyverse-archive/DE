package elasticsearch

import (
	"logcabin"

	"gopkg.in/olivere/elastic.v3"

	"templeton/database"
	"templeton/model"
)

var (
	logger = logcabin.New()
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

// PurgeIndex walks an index querying a database, deleting those which should not exist
func (e *Elasticer) PurgeIndex(d *database.Databaser) {
	indexer := e.NewBulkIndexer(1000)
	defer indexer.Flush()

	scanner, err := e.es.Scan(e.index).Type("metadata").Scroll("1m").Fields("_id").Do()
	if err != nil {
		logger.Fatal(err)
		return
	}

	for {
		docs, err := scanner.Next()
		if err == elastic.EOS {
			logger.Print("Finished all rows for purge.")
			break
		}
		if err != nil {
			logger.Print(err)
			break
		}

		if docs.TotalHits() > 0 {
			for _, hit := range docs.Hits.Hits {
				avus, err := d.GetObjectAVUs(hit.Id)
				if err != nil {
					logger.Printf("Error processing %s: %s", hit.Id, err)
					continue
				}
				if len(avus) == 0 {
					logger.Printf("Deleting %s", hit.Id)
					req := elastic.NewBulkDeleteRequest().Index(e.index).Type("metadata").Id(hit.Id)
					err = indexer.Add(req)
					if err != nil {
						logger.Printf("Error enqueuing delete of %s: %s", hit.Id, err)
					}
				}
			}
		}
	}
}

// IndexEverything creates a bulk indexer and takes a database, and iterates to index its contents
func (e *Elasticer) IndexEverything(d *database.Databaser) {
	indexer := e.NewBulkIndexer(1000)
	defer indexer.Flush()

	cursor, err := d.GetAllObjects()
	if err != nil {
		logger.Fatal(err)
	}
	defer cursor.Close()

	for {
		ids, err := cursor.Next()
		if err == database.EOS {
			logger.Print("Done all rows, finishing.")
			break
		}
		if err != nil {
			logger.Print(err)
			break
		}

		formatted, err := model.AVUsToIndexedObject(ids)
		if err != nil {
			logger.Print(err)
			break
		}
		logger.Printf("Indexing %s", formatted.ID)

		req := elastic.NewBulkIndexRequest().Index(e.index).Type("metadata").Id(formatted.ID).Doc(formatted)
		err = indexer.Add(req)
		if err != nil {
			logger.Print(err)
			break
		}
	}
}

func (e *Elasticer) Reindex(d *database.Databaser) {
	e.PurgeIndex(d)
	e.IndexEverything(d)
}
