(defproject org.iplantc/clojure-commons "5.2.6.0"
  :description "Common Utilities for Clojure Projects"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :plugins [[test2junit "1.1.3"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [buddy/buddy-sign "0.7.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [metosin/compojure-api "0.24.2"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [clj-time "0.11.0"]
                 [com.cemerick/url "0.1.1"]
                 [commons-configuration "1.10"    ; provides org.apache.commons.configuration
                  :exclusions [commons-logging]]
                 [me.raynes/fs "1.4.6"]
                 [medley "0.7.0"]
                 [ring "1.4.0"]
                 [slingshot "0.12.2"]
                 [trptcolin/versioneer "0.2.0"]
                 [org.iplantc/service-logging "5.2.6.0"]]
  :profiles {:test {:resource-paths ["resources" "test-resources"]}})
