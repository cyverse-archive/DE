(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/clockwork "5.2.6.0"
  :description "Scheduled jobs for the iPlant Discovery Environment"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "clockwork-standalone.jar"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [cheshire "5.5.0"
                   :exclusions [[com.fasterxml.jackson.dataformat/jackson-dataformat-cbor]
                                [com.fasterxml.jackson.dataformat/jackson-dataformat-smile]
                                [com.fasterxml.jackson.core/jackson-annotations]
                                [com.fasterxml.jackson.core/jackson-databind]
                                [com.fasterxml.jackson.core/jackson-core]]]
                 [clj-http "0.6.5"]
                 [clj-time "0.4.5"]
                 [clojurewerkz/quartzite "1.0.1"
                   :exclusions [c3p0]]
                 [com.cemerick/url "0.0.7"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.3.0-RC5"
                  :exclusions [c3p0]]
                 [org.iplantc/clj-jargon "5.2.6.0"
                   :exclusions [[org.slf4j/slf4j-log4j12]
                                [log4j]]]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/kameleon "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]
                 [me.raynes/fs "1.4.6"]
                 [slingshot "0.10.3"]]
  :profiles {:dev     {:resource-paths ["resources/test"]}
             :uberjar {:aot :all}}
  :main ^:skip-aot clockwork.core
  :plugins [[test2junit "1.1.3"]]
  :uberjar-exclusions [#"BCKEY.SF"])
