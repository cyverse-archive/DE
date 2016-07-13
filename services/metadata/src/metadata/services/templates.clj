(ns metadata.services.templates
  (:use [clojure-commons.core :only [remove-nil-values]]
        [korma.db :only [transaction]])
  (:require [clojure-commons.assertions :as ca]
            [clojure.string :as string]
            [me.raynes.fs.compression :as cmp]
            [sanitize-filename.core :as sf]
            [metadata.persistence.templates :as tp]
            [metadata.util.csv :as csv]))

(defn list-templates
  []
  {:metadata_templates (mapv remove-nil-values (tp/list-templates))})

(defn view-template
  [template-id]
  (-> (tp/view-template template-id)
      (ca/assert-found "metadata template" template-id)
      (remove-nil-values)))

(defn- format-template-csv
  [template-data]
  (csv/csv-string [
    (->> template-data
      :attributes
      (map :name)
      (cons "file name or path"))]))

(defn view-template-csv
  [template-id]
  (format-template-csv (view-template template-id)))

(defn- format-template-guide
  [template-data]
  (csv/csv-string
    (->> template-data
      :attributes
      (map (juxt :name :description :required :type #(if (:values %) (string/join ", " (map :value (:values %))) "")))
      (cons ["attribute name", "attribute description",
             "required (If you cannot provide,  enter 'not collected', 'not applicable' or 'missing'.)",
             "value type definition", "enum value options (you must enter one of these values)"]))))

(defn view-template-guide
  [template-id]
  (format-template-guide (view-template template-id)))

(defn view-template-zip
  [template-id]
  (let [template-data (view-template template-id)
        template-name (sf/sanitize (:name template-data))]
    (cmp/make-zip-stream [(str template-name "/blank.csv") (format-template-csv template-data)]
                         [(str template-name "/guide.csv") (format-template-guide template-data)])))

;; This function alias relies on view-template's error checking to throw an exception if a template
;; with the given ID doesn't exist.
(def validate-template-exists view-template)

(defn view-attribute
  [attr-id]
  (-> (tp/view-attribute attr-id)
      (ca/assert-found "metadata attribute" attr-id)
      (remove-nil-values)))

(defn admin-list-templates
  []
  {:metadata_templates (mapv remove-nil-values (tp/list-templates false))})

(defn add-template
  [{:keys [user]} template]
  (transaction (view-template (tp/add-template user template))))

(defn update-template
  [{:keys [user]} template-id template]
  (transaction (view-template (tp/update-template user template-id template))))

(defn delete-template
  [{:keys [user]} template-id]
  (transaction (tp/delete-template user template-id)))
