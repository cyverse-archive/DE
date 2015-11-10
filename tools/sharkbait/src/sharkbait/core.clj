(ns sharkbait.core
  (:require [clojure.string :as string])
  (:import [edu.internet2.middleware.grouper GrouperSession Stem StemFinder SubjectFinder]
           [edu.internet2.middleware.grouper.misc SaveMode]))

(def ^:private de-username        "de_grouper")
(def ^:private de-folder          "iplant:de")
(def ^:private de-users-folder    "iplant:de:users")
(def ^:private de-apps-folder     "iplant:de:apps")
(def ^:private de-analyses-folder "iplant:de:analyses")

(def ^:private default-folder-names
  [de-users-folder
   de-apps-folder
   de-analyses-folder])

(def ^:private find-subject
  "Finds a subject with the given ID. This function is memoized because it appears that searching
   for a subject more than once can cause null pointer exceptions."
  (memoize (fn [subject-id] (SubjectFinder/findById subject-id true))))

(defn- create-grouper-session
  "Creates a Grouper session using the root Grouper account."
  ([]
     (GrouperSession/start (SubjectFinder/findRootSubject)))
  ([subject-id]
     (GrouperSession/start (find-subject subject-id))))

(defn- find-folder
  "Finds a folder in Grouper, creating it and any parent folders if necessary."
  [session name]
  (or (StemFinder/findByName session name false)
      (Stem/saveStem session nil nil name nil nil SaveMode/INSERT true)))

(defn- perform-root-actions
  "Performs the actions that require superuser privileges."
  []
  (let [session (create-grouper-session)]
    (try
      (-> (find-folder session de-folder)
          (.grantPrivs (find-subject de-username) true false false false false))
      (finally (GrouperSession/stopQuietly session)))))

;; TODO: implement me
(defn- create-app-permission-def
  [session])

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  []
  (let [session (create-grouper-session de-username)]
    (try
      (dorun (map (partial find-folder session) default-folder-names))
      (create-app-permisison-def session)
      (finally (GrouperSession/stopQuietly session)))))

(defn -main
  [& args]
  (perform-root-actions)
  (perform-de-user-actions))
