(ns mescal.agave-de-v2.app-listings
  (:require [mescal.agave-de-v2.constants :as c]
            [mescal.util :as util]))

(defn hpc-app-group
  []
  {:id           c/hpc-group-id
   :is_public    true
   :name         c/hpc-group-name
   :app_count    -1})

(defn get-app-name
  [app]
  (str (or (:label app) (:name app)) " " (:version app)))

(defn- format-app-listing
  [statuses jobs-enabled? listing]
  (let [mod-time (util/to-utc (:lastModified listing))
        system   (:executionSystem listing)]
    {:id                   (:id listing)
     :name                 (get-app-name listing)
     :description          (:shortDescription listing)
     :integration_date     mod-time
     :edited_date          mod-time
     :app_type             c/hpc-app-type
     :can_favor            false
     :can_rate             false
     :can_run              true
     :deleted              false
     :disabled             (not (and jobs-enabled? (= "UP" (statuses system))))
     :integrator_email     c/unknown-value
     :integrator_name      c/unknown-value
     :is_favorite          false
     :is_public            (:isPublic listing)
     :pipeline_eligibility {:is_valid true :reason ""}
     :rating               {:average 0.0 :total 0}
     :step_count           1
     :permission           "read"
     :wiki_url             ""}))

(defn- format-app-listing-response
  [listing statuses jobs-enabled?]
  (assoc (hpc-app-group)
    :apps      (map (partial format-app-listing statuses jobs-enabled?) listing)
    :app_count (count listing)))

(defn list-apps
  ([agave statuses jobs-enabled?]
     (format-app-listing-response (.listApps agave) statuses jobs-enabled?))
  ([agave statuses jobs-enabled? app-ids]
     (format-app-listing-response (.listApps agave app-ids) statuses jobs-enabled?)))

(defn list-apps-with-ontology
  [agave statuses jobs-enabled? term]
  (format-app-listing-response (.listAppsWithOntology agave term) statuses jobs-enabled?))
