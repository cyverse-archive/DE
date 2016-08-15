(ns data-info.services.tickets
  (:use [clojure-commons.error-codes]
        [clj-jargon.init :only [with-jargon]]
        [clj-jargon.tickets]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [clojure-commons.file-utils :as ft]
            [clostache.parser :as stache]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [data-info.util.logging :as dul]
            [data-info.util.config :as cfg]
            [data-info.util.irods :as irods]
            [data-info.util.validators :as validators])
  (:import [java.util UUID]))

(defn- ticket-uuids?
  [cm user new-uuids]
  (try+
    (validators/all-tickets-nonexistant cm user new-uuids)
    true
    (catch error? e false)))

(defn- gen-uuids
  [cm user num-uuids]
  (let [new-uuids (doall (repeatedly num-uuids #(string/upper-case (str (UUID/randomUUID)))))]
    (if (ticket-uuids? cm user new-uuids)
      new-uuids
      (recur cm user num-uuids)) ))

(defn- render-ticket-tmpl
  [cm ticket-map tmpl]
  (stache/render tmpl {:url       (cfg/kifshare-external-url)
                       :ticket-id (:ticket-id ticket-map)
                       :filename  (ft/basename (:path ticket-map))}))

(defn- url-join
  [url path]
  (let [url  (ft/rm-last-slash url)
        path (string/replace-first path "/" "")]
    (str url "/" path)))

(defn- returnable-ticket-map
  [cm ticket-id]
  (let [tm (ticket-map cm (:username cm) ticket-id)]
    {:download-url      (render-ticket-tmpl cm tm (cfg/kifshare-download-template))
     :download-page-url (url-join (cfg/kifshare-external-url) (:ticket-id tm))
     :path              (:path tm)
     :ticket-id         (:ticket-id tm)}))

(defn- add-tickets
  [user paths public?]
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      (let [new-uuids (gen-uuids cm user (count paths))]
        (validators/user-exists cm user)
        (validators/all-paths-exist cm paths)
        (validators/all-paths-writeable cm user paths)
        (doseq [[path uuid] (map list paths new-uuids)]
          (log/warn "[add-tickets] adding ticket for " path "as" uuid)
          (create-ticket cm (:username cm) path uuid)
          (when public?
            (log/warn "[add-tickets] making ticket" uuid "public")
            (publicize-ticket cm uuid)))
        {:user    user
         :tickets (mapv (partial returnable-ticket-map cm) new-uuids)}))))

(defn- remove-tickets
  [user ticket-ids]
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      (validators/user-exists cm user)
      (validators/all-tickets-exist cm user ticket-ids)
      (let [all-paths (mapv #(.getIrodsAbsolutePath (ticket-by-id cm (:username cm) %)) ticket-ids)]
        (validators/all-paths-writeable cm user all-paths)
        (doseq [ticket-id ticket-ids]
          (delete-ticket cm (:username cm) ticket-id))
        {:user user :tickets ticket-ids}))))

(defn- tickets-for-path
  [cm path]
  (map :ticket-id (ticket-ids-for-path cm (:username cm) path)))

(defn- returnable-tickets-for-path
  [cm path]
  (map (partial returnable-ticket-map cm) (tickets-for-path cm path)))

(defn- list-tickets-for-paths
  [user paths]
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      (validators/user-exists cm user)
      (validators/all-paths-exist cm paths)
      (validators/all-paths-readable cm user paths)
      {:tickets
       (apply merge (mapv #(hash-map %1 (returnable-tickets-for-path cm %1)) paths))})))

(defn do-add-tickets
  [{public? :public user :user} {paths :paths}]
  (add-tickets user paths public?))

(with-pre-hook! #'do-add-tickets
  (fn [params body]
    (dul/log-call "do-add-tickets" params body)
    (validators/validate-num-paths (:paths body))))

(with-post-hook! #'do-add-tickets (dul/log-func "do-add-tickets"))

(defn do-remove-tickets
  [{user :user} {tickets :tickets}]
  (remove-tickets user tickets))

(with-pre-hook! #'do-remove-tickets
  (fn [params body]
    (dul/log-call "do-remove-tickets" params body)))

(with-post-hook! #'do-remove-tickets (dul/log-func "do-remove-tickets"))

(defn do-list-tickets
  [{user :user} {paths :paths}]
  (list-tickets-for-paths user paths))

(with-pre-hook! #'do-list-tickets
  (fn [params body]
    (dul/log-call "do-list-tickets" params body)
    (validators/validate-num-paths (:paths body))))

(with-post-hook! #'do-list-tickets (dul/log-func "do-list-tickets"))
