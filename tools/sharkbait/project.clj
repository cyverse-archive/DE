(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/sharkbait "5.2.4.0"
  :description "Utility for initializing Grouper."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE/"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "sharkbait-standalone.jar"
  :dependencies [[edu.internet2.middleware.grouper/grouper "2.2.1"]
                 [net.sf.ehcache/ehcache "2.10.1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.hibernate/hibernate-core "3.6.10.Final"]
                 [org.hibernate/hibernate-ehcache "3.6.10.Final"]
                 [org.iplantc/common-cli "5.2.4.0"]]
  :main sharkbait.core
  :profiles {:uberjar {:aot :all}})
