(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.iplantc/terrain "5.2.6.0-SNAPSHOT"
  :description "Framework for hosting DiscoveryEnvironment metadata services."
  :url "https://github.com/cyverse/DE"
  :license {:name "BSD Standard License"
            :url "http://www.iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "terrain-standalone.jar"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [clj-time "0.11.0"]
                 [clojurewerkz/elastisch "2.2.0"]
                 [com.cemerick/url "0.1.1" :exclusions [com.cemerick/clojurescript.test]]
                 [commons-net "3.4"]                               ; provides org.apache.commons.net
                 [compojure "1.4.0"]
                 [metosin/compojure-api "0.24.2"]  ; should be held to the same version as the one 
                                                   ; used by org.iplantc/clojure-commons
                 [de.ubercode.clostache/clostache "1.4.0" :exclusions [org.clojure/core.incubator]]
                 [dire "0.5.3"]
                 [me.raynes/fs "1.4.6"]
                 [medley "0.7.0"]
                 [org.apache.tika/tika-core "1.11"]      ; provides org.apache.tika
                 [org.nexml.model/nexml "1.5-SNAPSHOT"]  ; provides org.nexml.model
                 [org.biojava.thirdparty/forester "1.005" ]
                 [slingshot "0.12.2"]
                 [org.iplantc/clj-cas "5.2.6.0"]
                 [org.iplantc/clj-icat-direct "5.2.6.0"]
                 [org.iplantc/clj-jargon "5.2.6.0"]
                 [org.iplantc/clojure-commons "5.2.6.0"]
                 [org.iplantc/common-cfg "5.2.6.0"]
                 [org.iplantc/common-cli "5.2.6.0"]
                 [org.iplantc/kameleon "5.2.6.0"]
                 [org.iplantc/heuristomancer "5.2.6.0"]
                 [org.iplantc/service-logging "5.2.6.0"]]
  :plugins [[lein-ring "0.9.2" :exclusions [org.clojure/clojure]]
            [swank-clojure "1.4.2" :exclusions [org.clojure/clojure]]
            [test2junit "1.1.3"]]
  :profiles {:dev     {:resource-paths ["conf/test"]}
             :uberjar {:aot :all}}
  :main ^:skip-aot terrain.core
  :ring {:handler terrain.core/app
         :init terrain.core/lein-ring-init
         :port 31325
         :auto-reload? false}
  :uberjar-exclusions [#".*[.]SF" #"LICENSE" #"NOTICE"]
  :repositories [["biojava"
                  {:url "http://www.biojava.org/download/maven"}]
                 ["sonatype-releases"
                  {:url "https://oss.sonatype.org/content/repositories/releases/"}]
                 ["local"
                  {:url "https://everdene.iplantcollaborative.org/maven/repository"
                   :checksum :ignore}]]
  :jvm-opts ["-Dlogback.configurationFile=/etc/iplant/de/logging/terrain-logging.xml"])
