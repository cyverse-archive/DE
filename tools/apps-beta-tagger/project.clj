(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/apps-beta-tagger "5.2.8.0"
  :description "Utility to add the beta metadata AVU to apps in the Beta category."
  :url "https://github.com/cyverse/DE"
  :license {:name "BSD Standard License"
            :url "http://www.cyverse.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "apps-beta-tagger-standalone.jar"
  :dependencies [[korma "0.4.2"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.iplantc/common-cli "5.2.8.0"]
                 [org.iplantc/kameleon "5.2.8.0"]]
  :aot :all
  :main apps-beta-tagger.core)
