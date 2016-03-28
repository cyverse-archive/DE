(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/info-typer "5.2.6.0"
  :description "An AMQP based info type detection service for iRODS"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "info-typer-standalone.jar"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.novemberain/langohr "3.5.1"]
                 [me.raynes/fs "1.4.6"]
                 [org.iplantc/clj-jargon "5.2.6.0"
                   :exclusions [[org.slf4j/slf4j-log4j12]
                                [log4j]]]
                 [org.iplantc/clojure-commons "5.2.6.0" :exclusions [commons-logging]]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/heuristomancer "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]]
  :main ^:skip-aot info-typer.core
  :profiles {:dev     {:resource-paths ["conf/test"]}
             :uberjar {:aot :all}}
  :plugins [[test2junit "1.1.3"]]
  :uberjar-exclusions [#"LICENSE" #"NOTICE"]
  :jvm-opts ["-Dlogback.configurationFile=/etc/iplant/de/logging/info-typer-logging.xml"])
