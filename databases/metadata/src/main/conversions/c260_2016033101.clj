(ns facepalm.c260-2016033101
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.6.0:20160331.01")

(defn- add-ontology-tables
  []
  (println "\t* Adding ontologies table...")
  (load-sql-file "tables/ontologies.sql")

  (println "\t* Adding ontology_classes table...")
  (load-sql-file "tables/ontology_classes.sql")

  (println "\t* Adding ontology_hierarchies table...")
  (load-sql-file "tables/ontology_hierarchies.sql")

  (println "\t* Adding ontologies constraints...")
  (load-sql-file "constraints/15_ontologies.sql")

  (println "\t* Adding ontology_classes constraints...")
  (load-sql-file "constraints/16_ontology_classes.sql")

  (println "\t* Adding ontology_hierarchies constraints...")
  (load-sql-file "constraints/17_ontology_hierarchies.sql")

  (println "\t* Adding ontology_hierarchy functions...")
  (load-sql-file "functions/04_ontology_hierarchy.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-ontology-tables))
