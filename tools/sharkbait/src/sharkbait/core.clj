(ns sharkbait.core
  (:import [edu.internet2.middleware.grouper GrouperSession StemFinder SubjectFinder]))

(defn- create-grouper-session
  "Creates a Grouper session using the root Grouper account."
  []
  (GrouperSession/start (SubjectFinder/findRootSubject)))

(defn -main
  [& args]
  (let [session (create-grouper-session)]
    (try
      (println (StemFinder/findByName session, "tmp:iplant", true))
      (finally (GrouperSession/stopQuietly session)))))
