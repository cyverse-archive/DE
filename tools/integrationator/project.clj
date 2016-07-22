(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject integrationator "0.1.0"
  :description "Adds user IDs to the integration_data table in the DE apps database."
  :url "https://github.com/cyverse/DE"
  :license {:name "BSD"
            :url "http://www.cyverse.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "integrationator-standalone.jar"
  :dependencies [[com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.4.0"
                  :exclusions [c3p0]]
                 [org.clojars.pntblnk/clj-ldap "0.0.12"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.iplantc/kameleon "5.2.8.0"]]
  :aot :all
  :main integrationator.core)
