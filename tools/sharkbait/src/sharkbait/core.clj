(ns sharkbait.core
  (:gen-class)
  (:require [clojure.string :as string])
  (:import [edu.internet2.middleware.grouper GrouperSession Stem StemFinder SubjectFinder]
           [edu.internet2.middleware.grouper.attr AttributeDefSave AttributeDefType]
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

(defn- add-implied-action
  "Creates a relationship between two actions so that one action implies another."
  [action-delegate implying-action-name implied-action-name]
  (let [implying-action (.findAction action-delegate implying-action-name true)
        implied-action  (.findAction action-delegate implied-action-name true)]
    (-> (.getAttributeAssignActionSetDelegate implying-action)
        (.addToAttributeAssignActionSet implied-action))))

(defn- set-permission-actions
  "Sets the allowed actions for a permission definition."
  [permission-def]
  (let [delegate (.getAttributeDefActionDelegate permission-def)]
    (.configureActionList delegate ["read" "write" "own"])
    (add-implied-action delegate "own" "write")
    (add-implied-action delegate "write" "read"))
  (.store permission-def))

(defn- create-permission-def
  "Creates a permission definition."
  [session permission-name folder]
  (-> (AttributeDefSave. session)
      (.assignAttributeDefType AttributeDefType/perm)
      (.assignName (str folder ":" permission-name))
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.assignToGroup true)
      (.assignToEffMembership true)
      (.save)
      (set-permission-actions)))

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  []
  (let [session (create-grouper-session de-username)]
    (try
      (dorun (map (partial find-folder session) default-folder-names))
      (create-permission-def session "appPermissionDef" de-apps-folder)
      (create-permission-def session "analysisPermissionDef" de-analyses-folder)
      (finally (GrouperSession/stopQuietly session)))))

(defn -main
  [& args]
  (perform-root-actions)
  (perform-de-user-actions))
