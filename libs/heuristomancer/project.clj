(defproject org.iplantc/heuristomancer "5.2.6.0"
  :description "Clojure library for attempting to guess file types."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD Standard License"
            :url "http://www.iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :profiles {:dev {:resource-paths ["test-data"]}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.2"]
                 [org.clojure/tools.logging "0.3.1"]
                 [instaparse "1.4.1"]]
  :plugins [[org.iplantc/lein-iplant-cmdtar "5.2.6.0"]
            [test2junit "1.1.3"]]
  :aot [heuristomancer.core]
  :main heuristomancer.core)
