(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/kifshare "5.2.6.0"
  :description "CyVerse Quickshare for iRODS"
  :url "https://github.com/cyverse/DE"

  :license {:name "BSD"
            :url "http://cyverse.org/sites/default/files/iPLANT-LICENSE.txt"}

  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "kifshare-standalone.jar"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [medley "0.5.5"]
                 [org.iplantc/clj-jargon "5.2.6.0"
                   :exclusions [[org.slf4j/slf4j-log4j12]
                                [log4j]]]
                 [org.iplantc/service-logging "5.2.6.0"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [me.raynes/fs "1.4.6"]
                 [cheshire "5.5.0"
                   :exclusions [[com.fasterxml.jackson.dataformat/jackson-dataformat-cbor]
                                [com.fasterxml.jackson.dataformat/jackson-dataformat-smile]
                                [com.fasterxml.jackson.core/jackson-annotations]
                                [com.fasterxml.jackson.core/jackson-databind]
                                [com.fasterxml.jackson.core/jackson-core]]]
                 [slingshot "0.12.2"]
                 [compojure "1.3.4"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [com.cemerick/url "0.1.1"]]

  :ring {:init kifshare.config/init
         :handler kifshare.core/app}

  :profiles {:dev     {:resource-paths ["build" "conf"]
                       :dependencies [[midje "1.6.3"]]
                       :plugins [[lein-midje "2.0.1"]]}
             :uberjar {:aot :all}}

  :plugins [[lein-ring "0.7.5"]]

  :main ^:skip-aot kifshare.core)
