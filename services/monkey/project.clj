(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/monkey "5.2.6.0"
  :description "A metadata database crawler. It synchronizes the tag documents in the search data
                index with the tag information inthe metadata database.  🐒"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :aot [monkey.index monkey.tags monkey.core]
  :main monkey.core
  :uberjar-name "monkey-standalone.jar"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql "9.1-901-1.jdbc4"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [clojurewerkz/elastisch "2.0.0"]
                 [com.novemberain/langohr "3.5.1"]
                 [me.raynes/fs "1.4.6"]
                 [slingshot "0.10.3"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]]
  :plugins [[test2junit "1.1.3"]]
  :profiles {:dev {:resource-paths ["conf/test"]}})
