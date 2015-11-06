(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/sharkbait "5.0.0"
  :description "Utility for initializing Grouper."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE/"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "facepalm-standalone.jar"
  :dependencies [[edu.internet2.middleware.grouper/grouper "2.2.1"]
                 [net.sf.ehcache/ehcache "2.10.1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.hibernate/hibernate-core "3.6.10.Final"]
                 [org.hibernate/hibernate-ehcache "3.6.10.Final"]]
  :main sharkbait.core)
