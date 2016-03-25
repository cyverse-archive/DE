(defproject org.iplantc/clj-icat-direct "5.2.6.0"
  :description "A Clojure library for accessing the iRODS ICAT database directly."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD Standard License"
            :url "http://www.iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.4.2"
                  :exclusions [c3p0]]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]])
