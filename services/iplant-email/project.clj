(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/iplant-email "5.2.6.0"
  :description "iPlant Email Service"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "iplant-email-standalone.jar"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]
                 [cheshire "5.5.0"
                   :exclusions [[com.fasterxml.jackson.dataformat/jackson-dataformat-cbor]
                                [com.fasterxml.jackson.dataformat/jackson-dataformat-smile]
                                [com.fasterxml.jackson.core/jackson-annotations]
                                [com.fasterxml.jackson.core/jackson-databind]
                                [com.fasterxml.jackson.core/jackson-core]]]
                 [javax.mail/mail "1.4"]
                 [org.bituf/clj-stringtemplate "0.2"]
                 [compojure "1.0.1"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [me.raynes/fs "1.4.6"]]
  :plugins [[test2junit "1.1.3"]]
  :aot [iplant-email.core]
  :main iplant-email.core)
