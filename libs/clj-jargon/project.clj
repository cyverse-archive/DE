(defproject org.iplantc/clj-jargon "5.2.7.0"
  :description "Clojure API on top of iRODS's jargon-core."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [org.irods.jargon/jargon-core "4.0.2.4-RELEASE"
                  :exclusions [[org.jglobus/JGlobus-Core]
                               [org.slf4j/slf4j-api]
                               [org.slf4j/slf4j-log4j12]]]
                 [org.irods.jargon/jargon-data-utils "4.0.2.4-RELEASE"
                  :exclusions [[org.slf4j/slf4j-api]
                               [org.slf4j/slf4j-log4j12]]]
                 [org.irods.jargon/jargon-ticket "4.0.2.4-RELEASE"
                  :exclusions [[org.slf4j/slf4j-api]
                               [org.slf4j/slf4j-log4j12]]]
                 [slingshot "0.12.2"]
                 [org.iplantc/clojure-commons "5.2.7.0"]]
  :repositories [["dice.repository"
                  {:url "https://raw.github.com/DICE-UNC/DICE-Maven/master/releases"}]
                 ["renci-snapshot.repository"
                  {:url "http://ci-dev.renci.org/nexus/content/repositories/renci-snapshot/"}]
                 ["iplant.repository"
                  {:url "https://everdene.iplantcollaborative.org/archiva/repository/internal/"}]])
