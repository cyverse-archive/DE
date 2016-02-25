(ns apps.service.apps.de.validation
  (:use [clojure-commons.exception-util :only [forbidden exists]]
        [slingshot.slingshot :only [try+ throw+]]
        [korma.core :exclude [update]]
        [kameleon.core]
        [kameleon.entities]
        [kameleon.queries :only [parameter-types-for-tool-type]]
        [apps.persistence.app-metadata :only [get-app list-duplicate-apps]])
  (:require [apps.service.apps.de.permissions :as perms]
            [clojure.string :as string]))

(defn- get-tool-type-from-database
  "Gets the tool type for the deployed component with the given identifier from
   the database."
  [component-id]
  (first (select tools
                 (fields :tool_types.id :tool_types.name)
                 (join tool_types)
                 (where {:id component-id}))))

(defn- get-deployed-component-from-database
  "Gets the deployed component for the deployed component with the given identifer
   from the database."
  [component-id]
  (first (select tools
                 (where {:id component-id}))))

;; FIXME
(defn- get-tool-type-from-registry
  "Gets the tool type for the deployed component with the given identifier from
   the given registry."
  [registry component-id]
  (when-not (nil? registry)
    (let [components (throw+ "Reimplement: (.getRegisteredObjects registry DeployedComponent)")
          component  (first (filter #(= component-id (.getId %)) components))
          tool-type  (when-not (nil? component) (.getToolType component))]
      (when-not (nil? tool-type)
        {:id   (.getId tool-type)
         :name (.getName tool-type)}))))

(defn- get-tool-type
  "Gets the tool type name for the deployed component with the given identifier."
  [registry component-id]
  (or (get-tool-type-from-registry registry component-id)
      (get-tool-type-from-database component-id)))

(defn- get-valid-ptype-names
  "Gets the valid property type names for a given tool type."
  [{tool-type-id :id}]
  (map :name (parameter-types-for-tool-type tool-type-id)))

;; FIXME
(defn validate-template-property-types
  "Validates the property types in a template that is being imported."
  [template registry]
  (when-let [tool-type (get-tool-type registry (.getComponent template))]
    (let [valid-ptypes (into #{} (get-valid-ptype-names tool-type))
          properties   (mapcat #(.getProperties %) (.getPropertyGroups template))]
      (dorun (map #(throw+ {:type          ::UnsupportedPropertyTypeException
                            :property-type %
                            :name          (:name tool-type)})
                  (filter #(nil? (valid-ptypes %))
                          (map #(.getPropertyTypeName %) properties)))))))

;; FIXME
(defn validate-template-deployed-component
  "Validates a deployed component that is associated with a template."
  [template]
  (let [component-id (.getComponent template)]
   (when (string/blank? component-id)
     (throw+ {:type        ::MissingDeployedComponentException
              :template-id (.getId template)}))
   (when (nil? (get-deployed-component-from-database component-id))
     (throw+ {:type ::UnknownDeployedComponentException :component-id component-id}))))

(defn- task-ids-for-app
  "Get the list of task IDs associated with an app."
  [app-id]
  (map :task_id
       (select [:app_steps :step]
               (fields :step.task_id)
               (where {:step.app_id app-id}))))

(defn- private-apps-for
  "Finds private single-step apps for a list of task IDs."
  [task-ids]
  (select [:app_listing :a]
          (fields :a.id :a.name)
          (join [:app_steps :step]
                {:a.id :step.app_id})
          (where {:step.task_id [in task-ids]
                  :a.step_count 1
                  :a.is_public  false})))

(defn- list-unrunnable-tasks
  "Determines which of a collection of task IDs are not runnable."
  [task-ids]
  (map :id
       (select [:tasks :t]
               (fields :t.id)
               (where {:t.id              [in task-ids]
                       :t.tool_id         nil
                       :t.external_app_id nil}))))

(defn app-publishable?
  "Determines whether or not an app can be published. An app is publishable if none of the
   templates in the app are associated with any single-step apps that are not public. Returns
   a flag indicating whether or not the app is publishable along with the reason the app isn't
   publishable if it's not."
  [{username :shortUsername} app-id & {:keys [permissions-checked]}]
  (perms/check-app-permissions username "own" [app-id])
  (let [app              (get-app app-id)
        task-ids         (task-ids-for-app app-id)
        unrunnable-tasks (list-unrunnable-tasks task-ids)
        private-apps     (private-apps-for task-ids)]
    (cond (:is_public app)       [false "app is already public"]
          (empty? task-ids)      [false "no app ID provided"]
          (seq unrunnable-tasks) [false "contains unrunnable tasks" unrunnable-tasks]
          (= 1 (count task-ids)) [true]
          (seq private-apps)     [false "contains private apps" private-apps]
          :else                  [true])))

(defn- verify-app-not-public
  "Verifies that an app has not been made public."
  [app]
  (when (:is_public app)
    (throw+ {:type  :clojure-commons.exception/not-writeable
             :error (str "Workflow, " (:id app) ", is public and may not be edited")})))

(defn verify-app-permission
  "Verifies that the user has sufficient privileges for an app."
  [{user :shortUsername} {app-id :id} level]
  (perms/check-app-permissions user level [app-id]))

(defn verify-app-editable
  "Verifies that the app is allowed to be edited by the current user."
  [user app]
  (verify-app-permission user app "write")
  (verify-app-not-public app))

(def ^:private duplicate-app-selected-categories-msg
  "An app with the same name already exists in one of the selected categories.")

(def ^:private duplicate-app-existing-categories-msg
  "An app with the same name already exists in one of the same categories.")

(defn validate-app-name
  "Verifies that an app with the same name doesn't already exist in any of the same app categories. The beta
   category is treated as an exception because it's intended to be a staging area for new apps."
  ([app-name app-id beta-app-category-id]
     (validate-app-name app-name app-id beta-app-category-id nil))
  ([app-name app-id beta-app-category-id category-ids]
     (when (seq (list-duplicate-apps app-name app-id beta-app-category-id category-ids))
       (if (seq category-ids)
         (exists duplicate-app-selected-categories-msg :app_name app-name :category_ids category-ids)
         (exists duplicate-app-existing-categories-msg :app_name app-name :app_id app-id))))
  ([app-name app-id beta-app-category-id category-ids path]
     (when (seq (list-duplicate-apps app-name app-id beta-app-category-id category-ids))
       (if (seq category-ids)
         (exists duplicate-app-selected-categories-msg :app_name app-name :category_ids category-ids :path path)
         (exists duplicate-app-existing-categories-msg :app_name app-name :app_id app-id :path path)))))
