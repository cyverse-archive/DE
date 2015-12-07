(ns terrain.services.user-sessions
  (:use [terrain.util.service]
        [terrain.clients.user-sessions]
        [terrain.auth.user-attributes])
  (:require [clojure.tools.logging :as log]))

(defn user-session
  ([]
     (let [user (:username current-user)]
       (log/debug "Getting user session for" user)
       (success-response (get-session user))))
  ([session]
     (let [user (:username current-user)]
       (log/debug "Setting user session for" user)
       (success-response (set-session user session)))))

(defn remove-session
  []
  (let [user (:username current-user)]
    (log/debug "Deleting user session for" user)
    (success-response (delete-session user))))
