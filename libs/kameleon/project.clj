(defproject org.iplantc/kameleon "5.2.5.0"
  :description "Library for interacting with backend relational databases."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-time "0.11.0"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.4.2"
                  :exclusions [c3p0]]
                 [me.raynes/fs "1.4.6"]
                 [postgresql "9.3-1102.jdbc41"]
                 [slingshot "0.12.2"]]
  :plugins [[lein-marginalia "0.7.1"]
            [test2junit "1.1.3"]]
  :manifest {"db-version" "2.4.0:20160106.01"})
