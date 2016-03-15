(ns apps.clients.notifications.app-sharing
  (:use [apps.clients.notifications.common-sharing]
        [medley.core :only [remove-vals]])
  (:require [clojure.string :as string]))

(def notification-type "apps")
(def singular "app")
(def plural "apps")

(defn- format-app
  [category-keyword response]
  (remove-vals nil? (assoc (select-keys response [:app_id :app_name])
                      :category_id (str (category-keyword response)))))

(defn- format-payload
  [category-keyword action responses]
  {:action action
   :apps   (map (partial format-app category-keyword) responses)})

(defn- format-notification
  [category-keyword recipient formats action sharer sharee responses]
  (when (seq responses)
    (let [response-desc  (string/join ", " (map :app_name responses))
          response-count (count responses)]
      {:type    notification-type
       :user    recipient
       :subject (format-subject formats singular plural action sharer sharee response-desc response-count)
       :message (format-message formats singular plural action sharer sharee response-desc response-count)
       :payload (format-payload category-keyword action responses)})))

(defn- format-sharer-notification
  [formats action sharer sharee responses]
  (format-notification :sharer_category sharer formats action sharer sharee responses))

(defn- format-sharee-notification
  [formats action sharer sharee responses]
  (format-notification :sharee_category sharee formats action sharer sharee responses))

(defn format-sharing-notifications
  "Formats sharing notifications for apps."
  [sharer sharee responses]
  (let [responses (group-by :success responses)]
    (remove nil?
            [(format-sharer-notification sharer-success-formats share-action sharer sharee (responses true))
             (format-sharee-notification sharee-success-formats share-action sharer sharee (responses true))
             (format-sharer-notification failure-formats share-action sharer sharee (responses false))])))

(defn format-unsharing-notifications
  "Formats unsharing notifications for apps."
  [sharer sharee responses]
  (let [responses (group-by :success responses)]
    (remove nil?
            [(format-sharer-notification sharer-success-formats unshare-action sharer sharee (responses true))
             (format-sharer-notification failure-formats unshare-action sharer sharee (responses false))])))
