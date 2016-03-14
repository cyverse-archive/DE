(ns apps.clients.notifications.common-sharing
  (:use [clostache.parser :only [render]]))

(def grouping-threshold 10)

(def share-action "share")
(def unshare-action "unshare")

(def sharer-success-formats
  {:grouped
   {:singular "{{count}} {{singular}} has been {{action}}d with {{sharee}}."
    :plural   "{{count}} {{plural}} have been {{action}}d with {{sharee}}."}
   :ungrouped
   {:singular "The following {{singular}} has been {{action}}d with {{sharee}}: {{items}}"
    :plural   "The following {{plural}} have been {{action}}d with {{sharee}}: {{items}}"}})

(def sharee-success-formats
  {:grouped
   {:singular "{{sharer}} has {{action}}d {{count}} {{singular}} with you."
    :plural   "{{sharer}} has {{action}}d {{count}} {{plural}} with you."}
   :ungrouped
   {:singular "{{sharer}} has {{action}}d the following {{singular}} with you: {{items}}"
    :plural   "{{sharer}} has {{action}}d the following {{plural}} with you: {{items}}"}})

(def failure-formats
  {:grouped
   {:singular "{{count}} {{singular}} could not be {{action}}d with {{sharee}}."
    :plural   "{{count}} {{plural}} could not be {{action}}d with {{sharee}}."}
   :ungrouped
   {:singular "The following {{singular}} could not be {{action}}d with {{sharee}}: {{items}}"
    :plural   "The following {{plural}} could not be {{action}}d with {{sharee}}: {{items}}"}})

(defn- format-numbered-string
  [formats singular plural action sharer sharee response-desc response-count]
  (let [fmt (formats (if (= response-count 1) :singular :plural))]
    (render fmt {:singular singular
                 :plural   plural
                 :action   action
                 :sharer   sharer
                 :sharee   sharee
                 :items    response-desc
                 :count    response-count})))

(defn format-subject
  [formats singular plural action sharer sharee response-desc response-count]
  (format-numbered-string (:grouped formats) singular plural action sharer sharee response-desc response-count))

(defn format-message
  [formats singular plural action sharer sharee response-desc response-count]
  (let [formats (formats (if (< response-count grouping-threshold) :ungrouped :grouped))]
    (format-numbered-string formats singular plural action sharer sharee response-desc response-count)))
