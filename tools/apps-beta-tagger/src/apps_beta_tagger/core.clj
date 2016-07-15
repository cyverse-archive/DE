(ns apps-beta-tagger.core
  (:gen-class)
  (:use [korma.db])
  (:require [apps-beta-tagger.db.de :as db-de]
            [apps-beta-tagger.db.metadata :as db-metadata]
            [common-cli.core :as cli]))

(def tool-info
  {:desc "Utility for adding beta metadata AVUS to apps in the DE's 'Beta' apps category."
   :app-name "apps-beta-tagger"
   :group-id "org.iplantc"
   :art-id "apps-beta-tagger"})

(def cli-options
  [["-?" "--help" "Show help." :default false]
   ["-h" "--de-host DE-HOST" "The DE database hostname." :default "localhost"]
   ["-p" "--de-port DE-PORT" "The DE database port number." :default 5432 :parse-fn #(Integer/parseInt %)]
   ["-d" "--de-database DE-DATABASE" "The DE database name." :default "de"]
   ["-U" "--de-user DE-USER" "The DE database username." :default "de"]
   ["-H" "--meta-host METADATA-HOST" "The Metadata database hostname." :default "localhost"]
   ["-P" "--meta-port METADATA-PORT" "The Metadata database port number." :default 5432 :parse-fn #(Integer/parseInt %)]
   ["-D" "--meta-database METADATA-DATABASE" "The Metadata database name." :default "metadata"]
   ["-u" "--meta-user METADATA-USER" "The Metadata database username." :default "de"]
   ["-v" "--version" "Show the this tool's version." :default false]])

(defn -main
  "Add/update beta metadata AVUS for apps in the DE's 'Beta' apps category."
  [& args]
  (let [{:keys [options]} (cli/handle-args tool-info args (constantly cli-options))]
    (db-de/define-database options)
    (db-metadata/define-database options)
    (db-metadata/tag-beta-apps (db-de/get-beta-app-ids))))
