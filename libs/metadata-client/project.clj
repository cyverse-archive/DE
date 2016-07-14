(defproject org.iplantc/metadata-client "5.2.8.0"
  :description "Client for the metadata service"
  :url "https://github.com/cyverse/DE"
  :license {:name "BSD"
            :url "http://cyverse.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.2.0"]
                 [com.cemerick/url "0.1.1" :exclusions [com.cemerick/clojurescript.test]]
                 [cheshire "5.5.0"]
                 [org.iplantc/kameleon "5.2.8.0"]])
