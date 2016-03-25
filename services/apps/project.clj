(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/apps "5.2.6.0"
  :description "Framework for hosting DiscoveryEnvironment metadata services."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "apps-standalone.jar"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.google.guava/guava "18.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [medley "0.7.0"]
                 [metosin/compojure-api "0.24.2"]
                 [org.iplantc/authy "5.2.6.0"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/kameleon "5.2.6.0"]
                 [org.iplantc/mescal "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/common-cfg "5.2.6.0"]
                 [org.iplantc/common-swagger-api "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]
                 [me.raynes/fs "1.4.6"]
                 [mvxcvi/clj-pgp "0.8.0"]]
  :eastwood {:exclude-namespaces [apps.protocols :test-paths]
             :linters [:wrong-arity :wrong-ns-form :wrong-pre-post :wrong-tag :misplaced-docstrings]}
  :plugins [[lein-ring "0.9.6"]
            [lein-swank "1.4.4"]
            [test2junit "1.1.3"]
            [jonase/eastwood "0.2.3"]]
  :profiles {:dev {:resource-paths ["conf/test"]}}
  ;; compojure-api route macros should not be AOT compiled:
  ;; https://github.com/metosin/compojure-api/issues/135#issuecomment-121388539
  ;; https://github.com/metosin/compojure-api/issues/102
  :aot [#"apps.(?!routes).*"]
  :main apps.core
  :ring {:handler apps.routes.api/app
         :init apps.core/load-config-from-file
         :port 31323}
  :uberjar-exclusions [#"(?i)META-INF/[^/]*[.](SF|DSA|RSA)"]
  :jvm-opts ["-Dlogback.configurationFile=/etc/iplant/de/logging/apps-logging.xml"])
