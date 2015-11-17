(ns sharkbait.sessions
  (:require [sharkbait.subjects :as subjects])
  (:import [edu.internet2.middleware.grouper GrouperSession]))

(defn create-grouper-session
  "Creates a Grouper session. If no subject ID is specified then a root session is created."
  ([]
     (GrouperSession/start (subjects/find-root-subject)))
  ([subject-id]
     (GrouperSession/start (subjects/find-subject subject-id))))

(defn stop-grouper-session
  "Stops a Grouper session."
  [session]
  (GrouperSession/stopQuietly session))
