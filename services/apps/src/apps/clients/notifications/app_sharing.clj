(ns apps.clients.notifications.app-sharing
  (:require [clojure.string :as string]))

(def grouping-threshold 10)

(def notification-type "apps")
(def share-action "share")
(def unshare-action "unshare")

(def sharer-success-formats
  {:grouped   {:singular "%5$d app has been %1$sd with %3$s."
               :plural   "%5$d apps have been %1$sd with %3$s."}
   :ungrouped {:singular "The following app has been %1$sd with %3$s: %4$s"
               :plural   "The following apps have been %1$sd with %3$s: %4$s"}})

(def sharee-success-formats
  {:grouped   {:singular "%2$s has %1$sd %5$d app with you."
               :plural   "%2$s has %1$sd %5$d apps with you."}
   :ungrouped {:singular "%2$s has %1$sd the following app with you: %4$s"
               :plural   "%2$s has %1$sd the following apps with you: %4$s"}})

(def failure-formats
  {:grouped   {:singular "%5$d app could not be %1$sd with %3$s."
               :plural   "%5$d apps could not be %1$sd with %3$s."}
   :ungrouped {:singular "The following app could not be %1$sd with %3$s: %4$s"
               :plural   "The following apps could not be %1$sd with %3$s: %4$s"}})

(defn- format-numbered-string
  [formats action sharer sharee response-desc response-count]
  (let [fmt (formats (if (= response-count 1) :singular :plural))]
    (format fmt action sharer sharee response-desc response-count)))

(defn- format-subject
  [formats action sharer sharee response-desc response-count]
  (format-numbered-string (:grouped formats) action sharer sharee response-desc response-count))

(defn- format-message
  [formats action sharer sharee response-desc response-count]
  (let [formats (formats (if (< response-count grouping-threshold) :ungrouped :grouped))]
    (format-numbered-string formats action sharer sharee response-desc response-count)))

(defn- format-payload
  [action responses]
  {:action action
   :apps   (map :app_name responses)})

(defn- format-notification
  [recipient formats action sharer sharee responses]
  (when (seq responses)
    (let [response-desc  (string/join ", " (map :app_name responses))
          response-count (count responses)]
      {:type    notification-type
       :user    recipient
       :subject (format-subject formats action sharer sharee response-desc response-count)
       :message (format-message formats action sharer sharee response-desc response-count)
       :payload (format-payload action responses)})))

(defn format-sharing-notifications
  "Formats sharing notifications for apps."
  [sharer sharee responses]
  (let [responses (group-by :success responses)]
    (remove nil?
            [(format-notification sharer sharer-success-formats share-action sharer sharee (responses true))
             (format-notification sharee sharee-success-formats share-action sharer sharee (responses true))
             (format-notification sharer failure-formats share-action sharer sharee (responses false))])))

(defn format-unsharing-notifications
  "Formats unsharing notifications for apps."
  [sharer sharee responses]
  (let [responses (group-by :success responses)]
    (remove nil?
            [(format-notification sharer sharer-success-formats unshare-action sharer sharee (responses true))
             (format-notification sharer failure-formats unshare-action sharer sharee (responses false))])))
