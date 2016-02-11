(ns apps.clients.notifications.app-sharing
  (:use [clostache.parser :only [render]]
        [medley.core :only [remove-vals]])
  (:require [clojure.string :as string]))

(def grouping-threshold 10)

(def notification-type "apps")
(def share-action "share")
(def unshare-action "unshare")

(def sharer-success-formats
  {:grouped   {:singular "{{count}} app has been {{action}}d with {{sharee}}."
               :plural   "{{count}} apps have been {{action}}d with {{sharee}}."}
   :ungrouped {:singular "The following app has been {{action}}d with {{sharee}}: {{apps}}"
               :plural   "The following apps have been {{action}}d with {{sharee}}: {{apps}}"}})

(def sharee-success-formats
  {:grouped   {:singular "{{sharer}} has {{action}}d {{count}} app with you."
               :plural   "{{sharer}} has {{action}}d {{count}} apps with you."}
   :ungrouped {:singular "{{sharer}} has {{action}}d the following app with you: {{apps}}"
               :plural   "{{sharer}} has {{action}}d the following apps with you: {{apps}}"}})

(def failure-formats
  {:grouped   {:singular "{{count}} app could not be {{action}}d with {{sharee}}."
               :plural   "{{count}} apps could not be {{action}}d with {{sharee}}."}
   :ungrouped {:singular "The following app could not be {{action}}d with {{sharee}}: {{apps}}"
               :plural   "The following apps could not be {{action}}d with {{sharee}}: {{apps}}"}})

(defn- format-numbered-string
  [formats action sharer sharee response-desc response-count]
  (let [fmt (formats (if (= response-count 1) :singular :plural))]
    (render fmt {:action action
                 :sharer sharer
                 :sharee sharee
                 :apps   response-desc
                 :count  response-count})))

(defn- format-subject
  [formats action sharer sharee response-desc response-count]
  (format-numbered-string (:grouped formats) action sharer sharee response-desc response-count))

(defn- format-message
  [formats action sharer sharee response-desc response-count]
  (let [formats (formats (if (< response-count grouping-threshold) :ungrouped :grouped))]
    (format-numbered-string formats action sharer sharee response-desc response-count)))

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
       :subject (format-subject formats action sharer sharee response-desc response-count)
       :message (format-message formats action sharer sharee response-desc response-count)
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
