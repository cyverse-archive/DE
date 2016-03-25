(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/dewey "5.2.6.0"
  :description "This is a RabbitMQ client responsible for keeping an elasticsearch index
                synchronized with an iRODS repository using messages produced by iRODS."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "dewey-standalone.jar"
  :main ^:skip-aot dewey.core
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [cheshire "5.5.0"
                   :exclusions [[com.fasterxml.jackson.dataformat/jackson-dataformat-cbor]
                                [com.fasterxml.jackson.dataformat/jackson-dataformat-smile]
                                [com.fasterxml.jackson.core/jackson-annotations]
                                [com.fasterxml.jackson.core/jackson-databind]
                                [com.fasterxml.jackson.core/jackson-core]]]
                 [clojurewerkz/elastisch "2.0.0"]
                 [com.novemberain/langohr "3.5.1"]
                 [liberator "0.11.1"]
                 [compojure "1.1.8"]
                 [ring "1.4.0"]
                 [slingshot "0.10.3"]
                 [org.iplantc/clj-jargon "5.2.6.0"
                   :exclusions [[org.slf4j/slf4j-log4j12]
                                [log4j]]]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]
                 [me.raynes/fs "1.4.6"]]
  :plugins [[lein-midje "3.1.1"]]
  :resource-paths []
  :profiles {:dev     {:dependencies   [[midje "1.6.3"]]
                       :resource-paths ["dev-resource"]}
             :uberjar {:aot :all}}
  :jvm-opts ["-Dlogback.configurationFile=/etc/iplant/de/logging/dewey-logging.xml"])
