(ns facepalm.c250-2016021001
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.5.0:20160210.01")

(defn- add-reference-genome-uniqueness-constraints
  "Adds uniqueness constraints to the name and path columns of the genome_reference table."
  []
  (println "\t* Adding uniqueness constraints to the genome_reference table")
  (load-sql-file "constraints/46_genome_ref.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-reference-genome-uniqueness-constraints))
