(ns terrain.clients.user-prefs
  (:use [terrain.util.config]
        [clojure-commons.error-codes]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clj-http.client :as http]
            [cemerick.url :refer [url]]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]))

(defn- user-prefs-url
  [user]
  (str (url (prefs-base) user)))

(defn get-prefs
  [username]
  (let [resp (http/get (user-prefs-url username) {:throw-exceptions false})]
    (cond
     (= (:status resp) 404)
     (throw+ {:error_code ERR_NOT_A_USER :user username})

     (= (:status resp) 400)
     (throw+ {:error_code ERR_BAD_REQUEST :user username})

     (= (:status resp) 500)
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Error thrown by user-preferences service."})
     
     (not (<= 200 (:status resp) 299))
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Unknown error thrown by the user-preferences service"})

     :else
     (json/parse-string (:body resp) true))))

(defn get-prefs-safe
  "Same as get-prefs, but does not throw exceptions.
   Instead, caught exceptions are logged and nil is returned."
  [username]
  (try+
   (get-prefs username)
   (catch Object e
     (log/error e)
     nil)))

(defn set-prefs
  [username prefs-map]
  (let [json-prefs  (json/encode prefs-map)
        req-options {:body json-prefs
                     :content-type "application/json"
                     :throw-exceptions false}
        resp        (http/post (user-prefs-url username) req-options)]
    (cond
     (= (:status resp) 404)
     (throw+ {:error_code ERR_NOT_A_USER :user username})

     (= (:status resp) 400)
     (throw+ {:error_code ERR_BAD_REQUEST :user username})

     (= (:status resp) 415)
     (throw+ {:error_code ERR_BAD_REQUEST :content-type "application/json"})

     (= (:status resp) 500)
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Error thrown by user-preferences service"})

     (not (<= 200 (:status resp) 299))
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Unknown error thrown by the user-preferences service"})

     :else
     (json/parse-string (:body resp) true))))

(defn delete-prefs
  [username]
  (let [resp (http/delete (user-prefs-url username))]
    (cond
     (= (:status resp) 404)
     (throw+ {:error_code ERR_NOT_A_USER :user username})

     (= (:status resp) 400)
     (throw+ {:error_code ERR_BAD_REQUEST :user username})

     (= (:status resp) 500)
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Error thrown by user-preferences service"})

     (not (<= 200 (:status resp) 299))
     (throw+ {:error_code ERR_UNCHECKED_EXCEPTION :msg "Unknown error thrown by the user-preferences service"}))))
