(defproject org.iplantc/permissions-client "5.2.7.0"
  :description "A Clojure client library for the CyVerse permissions service."
  :url "https://github.com/cyverse/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[cheshire "5.6.1"]
                 [clj-http "2.2.0"]
                 [com.cemerick/url "0.1.1"]
                 [org.clojure/clojure "1.7.0"]]
  :profiles {:test {:dependencies [[clj-http-fake "1.0.2"]]}})
