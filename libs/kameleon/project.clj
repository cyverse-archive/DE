(defproject org.iplantc/kameleon "5.2.8.0"
  :description "Library for interacting with backend relational databases."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-time "0.11.0"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.4.2"
                  :exclusions [c3p0]]
                 [me.raynes/fs "1.4.6"]
                 [postgresql "9.3-1102.jdbc41"]
                 [slingshot "0.12.2"]]
  :plugins [[lein-marginalia "0.7.1"]
            [test2junit "1.1.3"]
            [jonase/eastwood "0.2.3"]]
  :eastwood {:exclude-namespaces [apps.protocols :test-paths]
             :linters [:wrong-arity :wrong-ns-form :wrong-pre-post :wrong-tag :misplaced-docstrings]}
  :manifest {"db-version" "2.8.0:20160725.01"})
