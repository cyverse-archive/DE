(defproject org.iplantc/service-logging "5.2.6.0"
  :description "Common Logging Utilities for Clojure Projects"
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]       ; exported
                 [cheshire "5.5.0"]
                 [slingshot "0.12.2"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [org.slf4j/jcl-over-slf4j "1.7.13"]
                 [net.logstash.logback/logstash-logback-encoder "4.5.1"]
                 [com.fasterxml.jackson.core/jackson-core "2.5.1"]
                 [com.fasterxml.jackson.core/jackson-databind "2.5.1"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.5.1"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor "2.5.1"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-smile "2.5.1"]])
