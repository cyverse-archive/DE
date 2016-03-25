;; IMPORTANT NOTE: Both an RPM and a tarball are generated for this project.
;; Because the release number is not recorded anywhere in the tarball, minor
;; changes need to be recorded in the version number.  Please increment the
;; minor version number rather than the release number for minor changes.
(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/facepalm "5.2.6.0"
  :description "Command-line utility for DE database managment."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "facepalm-standalone.jar"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.0"]
                 [cheshire "5.5.0"]
                 [com.cemerick/pomegranate "0.3.0"]
                 [fleet "0.10.1"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [korma "0.4.0"
                  :exclusions [c3p0]]
                 [me.raynes/fs "1.4.6"]
                 [org.iplantc/clj-jargon "5.2.6.0"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/kameleon "5.2.6.0"]
                 [postgresql "9.1-901-1.jdbc4"]
                 [slingshot "0.10.3"]
                 [clj-http "2.0.0"]]
  :plugins [[lein-marginalia "0.7.1"]]
  :aot :all
  :main facepalm.core)
