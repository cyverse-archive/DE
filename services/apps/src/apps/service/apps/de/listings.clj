(ns apps.service.apps.de.listings
  (:use [slingshot.slingshot :only [try+ throw+]]
        [korma.core :exclude [update]]
        [kameleon.core]
        [kameleon.entities]
        [kameleon.app-groups]
        [kameleon.app-listing]
        [kameleon.uuids :only [uuidify]]
        [apps.persistence.app-documentation :only [get-documentation]]
        [apps.tools :only [get-tools-by-id]]
        [apps.util.assertions :only [assert-not-nil]]
        [apps.util.config]
        [apps.util.conversions :only [to-long remove-nil-vals]]
        [apps.workspace])
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [apps.persistence.app-metadata :refer [get-app get-app-tools] :as amp]
            [apps.service.apps.de.permissions :as perms]
            [cemerick.url :as curl]))

(def my-public-apps-id (uuidify "00000000-0000-0000-0000-000000000000"))
(def shared-with-me-id (uuidify "EEEEEEEE-EEEE-EEEE-EEEE-EEEEEEEEEEEE"))
(def trash-category-id (uuidify "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"))

(def default-sort-params
  {:sort-field :lower_case_name
   :sort-dir   :ASC})

(defn- fix-sort-params
  [params]
  (let [params (merge default-sort-params params)]
    (if (= (keyword (:sort-field params)) :name)
      (assoc params :sort-field (:sort-field default-sort-params))
      params)))

(defn- add-subgroups
  [group groups]
  (let [subgroups (filter #(= (:id group) (:parent_id %)) groups)
        subgroups (map #(add-subgroups % groups) subgroups)
        result    (if (empty? subgroups) group (assoc group :categories subgroups))
        result    (dissoc result :parent_id :workspace_id :description)]
    result))

(defn format-trash-category
  "Formats the virtual group for the admin's deleted and orphaned apps category."
  [_ _ params]
  {:id         trash-category-id
   :name       "Trash"
   :is_public  true
   :app_count  (count-deleted-and-orphaned-apps)})

(defn list-trashed-apps
  "Lists the public, deleted apps and orphaned apps."
  [_ _ params]
  (list-deleted-and-orphaned-apps params))

(defn- format-my-public-apps-group
  "Formats the virtual group for the user's public apps."
  [user _ params]
  {:id        my-public-apps-id
   :name      "My public apps"
   :is_public false
   :app_count (count-public-apps-by-user (:email user) params)})

(defn list-my-public-apps
  "Lists the public apps belonging to the user with the given workspace."
  [user workspace params]
  (list-public-apps-by-user
   workspace
   (workspace-favorites-app-category-index)
   (:email user)
   params))

(defn format-shared-with-me-category
  "Formats the virtual group for apps that have been shared with the user."
  [_ workspace params]
  {:id        shared-with-me-id
   :name      "Shared with me"
   :is_public false
   :app_count (count-shared-apps workspace (workspace-favorites-app-category-index) params)})

(defn list-apps-shared-with-me
  [_ workspace params]
  (list-shared-apps workspace (workspace-favorites-app-category-index) params))

(def ^:private virtual-group-fns
  {my-public-apps-id {:format-group   format-my-public-apps-group
                      :format-listing list-my-public-apps}
   trash-category-id {:format-group   format-trash-category
                      :format-listing list-trashed-apps}
   shared-with-me-id {:format-group   format-shared-with-me-category
                      :format-listing list-apps-shared-with-me}})

(def ^:private virtual-group-ids (set (keys virtual-group-fns)))

(defn- format-private-virtual-groups
  "Formats any virtual groups that should appear in a user's workspace."
  [user workspace params]
  (remove :is_public
    (map (fn [[_ {f :format-group}]] (f user workspace params)) virtual-group-fns)))

(defn- add-private-virtual-groups
  [user group workspace params]
  (let [virtual-groups (format-private-virtual-groups user workspace params)
        actual-count   (count-apps-in-group-for-user
                        (:id group)
                        (:email user)
                        params)]
    (-> group
        (update-in [:categories] concat virtual-groups)
        (assoc :app_count actual-count))))

(defn- format-app-group-hierarchy
  "Formats the app group hierarchy rooted at the app group with the given
   identifier."
  [user user-workspace params {root-id :root_category_id workspace-id :id}]
  (let [groups (get-app-group-hierarchy root-id params)
        root   (first (filter #(= root-id (:id %)) groups))
        result (add-subgroups root groups)]
    (if (= (:id user-workspace) workspace-id)
      (add-private-virtual-groups user result user-workspace params)
      result)))

(defn- get-workspace-app-groups
  "Retrieves the list of the current user's workspace app groups."
  [user params]
  (let [workspace (get-workspace (:username user))]
    [(format-app-group-hierarchy user workspace params workspace)]))

(defn- get-visible-app-groups-for-workspace
  "Retrieves the list of app groups that are visible from a workspace."
  [user-workspace user params]
  (let [workspaces (get-visible-workspaces (:id user-workspace))]
    (map (partial format-app-group-hierarchy user user-workspace params) workspaces)))

(defn- get-visible-app-groups
  "Retrieves the list of app groups that are visible to a user."
  [user {:keys [admin] :as params}]
  (-> (when-not admin (get-optional-workspace (:username user)))
      (get-visible-app-groups-for-workspace user params)))

(defn get-app-groups
  "Retrieves the list of app groups that are visible to all users, the current user's app groups, or
   both, depending on the :public param."
  [user {:keys [public] :as params}]
  (let [perms  (iplant-groups/load-app-permissions (:shortUsername user))
        params (assoc params :app-ids (set (keys perms)))]
    (if (contains? params :public)
      (if-not public
        (get-workspace-app-groups user params)
        (get-visible-app-groups-for-workspace nil user params))
      (get-visible-app-groups user params))))

(defn get-admin-app-groups
  "Retrieves the list of app groups that are accessible to administrators. This includes all public
   app groups along with the trash group."
  [user params]
  (let [params (assoc params :admin true :public true)]
    (conj (vec (get-app-groups user params))
          (format-trash-category nil nil params))))

(defn- validate-app-pipeline-eligibility
  "Validates an App for pipeline eligibility, throwing a slingshot stone ."
  [app]
  (let [app_id (:id app)
        step_count (:step_count app)
        overall_job_type (:overall_job_type app)]
    (if (< step_count 1)
      (throw+ {:reason
               (str "Analysis, "
                    app_id
                    ", has too few steps for a pipeline.")}))
    (if (> step_count 1)
      (throw+ {:reason
               (str "Analysis, "
                    app_id
                    ", has too many steps for a pipeline.")}))
    (if-not (= overall_job_type "executable")
      (throw+ {:reason
               (str "Job type, "
                    overall_job_type
                    ", can't currently be included in a pipeline.")}))))

(defn- format-app-pipeline-eligibility
  "Validates an App for pipeline eligibility, reformatting its :overall_job_type value, and
   replacing it with a :pipeline_eligibility map"
  [app]
  (let [pipeline_eligibility (try+
                              (validate-app-pipeline-eligibility app)
                              {:is_valid true
                               :reason ""}
                              (catch map? {:keys [reason]}
                                {:is_valid false
                                 :reason reason}))
        app (dissoc app :overall_job_type)]
    (assoc app :pipeline_eligibility pipeline_eligibility)))

(defn- format-app-ratings
  "Formats an App's :average_rating, :user_rating, and :comment_id values into a
   :rating map."
  [{:keys [average_rating total_ratings user_rating comment_id] :as app}]
  (-> app
    (dissoc :average_rating :total_ratings :user_rating :comment_id)
    (assoc :rating (remove-nil-vals
                     {:average average_rating
                      :total total_ratings
                      :user user_rating
                      :comment_id comment_id}))))

(defn- app-can-run?
  [{tool-count :tool_count external-app-count :external_app_count task-count :task_count}]
  (= (+ tool-count external-app-count) task-count))

(defn- format-app-permissions
  "Formats the permission setting in the app. There are some cases where admins can view apps
  for which they don't have permissions. For example, when viewing orphaned and deleted apps.
  For the time being we'll deal with that by defaulting the permission level to the empty string,
  indicating that the user has no explicit permissions on the app."
  [app perms]
  (assoc app :permission (or (iplant-groups/get-permission-level perms (:id app)) "")))

(defn- format-app-listing
  "Formats certain app fields into types more suitable for the client."
  [perms app]
  (-> (assoc app :can_run (app-can-run? app))
      (dissoc :tool_count :task_count :external_app_count :lower_case_name)
      (format-app-ratings)
      (format-app-pipeline-eligibility)
      (format-app-permissions perms)
      (assoc :can_favor true :can_rate true :app_type "DE")
      (remove-nil-vals)))

(defn- list-apps-in-virtual-group
  "Formats a listing for a virtual group."
  [user workspace group-id perms params]
  (when-let [format-fns (virtual-group-fns group-id)]
    (-> ((:format-group format-fns) user workspace params)
        (assoc :apps (->> ((:format-listing format-fns) user workspace params)
                          (map (partial format-app-listing perms)))))))

(defn- count-apps-in-group
  "Counts the number of apps in an app group, including virtual app groups that may be included."
  [user {root-group-id :root_category_id} {:keys [id] :as app-group} params]
  (if (= root-group-id id)
    (count-apps-in-group-for-user id (:email user) params)
    (count-apps-in-group-for-user id params)))

(defn- get-apps-in-group
  "Gets the apps in an app group, including virtual app groups that may be included."
  [user {root-group-id :root_category_id :as workspace} {:keys [id]} params]
  (let [faves-index (workspace-favorites-app-category-index)]
    (if (= root-group-id id)
      (get-apps-in-group-for-user id workspace faves-index params (:email user))
      (get-apps-in-group-for-user id workspace faves-index params))))

(defn- list-apps-in-real-group
  "This service lists all of the apps in a real app group and all of its descendents."
  [user workspace category-id perms params]
  (let [app_group      (->> (get-app-category category-id)
                            (assert-not-nil ["category_id" category-id])
                            remove-nil-vals)
        total          (count-apps-in-group user workspace app_group params)
        apps_in_group  (get-apps-in-group user workspace app_group params)
        apps_in_group  (map (partial format-app-listing perms) apps_in_group)]
    (assoc app_group
      :app_count total
      :apps apps_in_group)))

(defn list-apps-in-group
  "This service lists all of the apps in an app group and all of its
   descendents."
  [user app-group-id params]
  (let [workspace (get-optional-workspace (:username user))
        perms     (iplant-groups/load-app-permissions (:shortUsername user))
        params    (fix-sort-params (assoc params :app-ids (set (keys perms))))]
    (or (list-apps-in-virtual-group user workspace app-group-id perms params)
        (list-apps-in-real-group user workspace app-group-id perms params))))

(defn has-category
  "Determines whether or not a category with the given ID exists."
  [category-id]
  (or (virtual-group-ids category-id)
      (seq (select :app_categories (where {:id category-id})))))

(defn search-apps
  "This service searches for apps in the user's workspace and all public app
   groups, based on a search term."
  [user params]
  (let [search_term (curl/url-decode (:search params))
        workspace (get-workspace (:username user))
        perms (iplant-groups/load-app-permissions (:shortUsername user))
        params (fix-sort-params (assoc params :app-ids (set (keys perms))))
        total (count-search-apps-for-user search_term (:id workspace) params)
        search_results (search-apps-for-user
                        search_term
                        workspace
                        (workspace-favorites-app-category-index)
                        params)
        search_results (map (partial format-app-listing perms) search_results)]
    {:app_count total
     :apps search_results}))

(defn- load-app-details
  "Retrieves the details for a single app."
  [app-id]
  (assert-not-nil [:app-id app-id]
    (first (select apps
                   (with app_references)
                   (with integration_data)
                   (where {:id app-id})))))

(defn- format-wiki-url
  "CORE-6510: Remove the wiki_url from app details responses if the App has documentation saved."
  [{:keys [id wiki_url] :as app}]
  (assoc app :wiki_url (if (get-documentation id) nil wiki_url)))

(defn- format-app-details
  "Formats information for the get-app-details service."
  [details tools]
  (let [app-id (:id details)]
    (-> details
      (select-keys [:id :integration_date :edited_date :deleted :disabled :wiki_url
                    :integrator_name :integrator_email])
      (assoc :name                 (:name details "")
             :description          (:description details "")
             :references           (map :reference_text (:app_references details))
             :tools                (map remove-nil-vals tools)
             :categories           (get-groups-for-app app-id)
             :suggested_categories (get-suggested-groups-for-app app-id))
      format-wiki-url)))

(defn get-app-details
  "This service obtains the high-level details of an app."
  [{username :shortUsername} app-id]
  (perms/check-app-permissions username "read" [app-id])
  (let [details (load-app-details app-id)
        tools   (get-app-tools app-id)]
    (when (empty? tools)
      (throw  (IllegalArgumentException. (str "no tools associated with app, " app-id))))
    (->> (format-app-details details tools)
         (remove-nil-vals))))

(defn load-app-ids
  "Loads the identifiers for all apps that refer to valid tools from the database."
  []
  (map :id
       (select [:apps :app]
               (modifier "distinct")
               (fields :app.id)
               (join [:app_steps :step]
                     {:app.id :step.app_id})
               (where (not [(sqlfn :exists (subselect [:tasks :t]
                                                      (join [:tools :dc]
                                                            {:t.tool_id :dc.id})
                                                      (where {:t.id :step.task_id
                                                              :t.tool_id nil})))]))
               (order :id :ASC))))

(defn list-app-ids
  "This service obtains the identifiers of all apps that refer to valid tools."
  []
  {:app_ids (load-app-ids)})

(defn- with-task-params
  "Includes a list of related file parameters in the query's result set,
   with fields required by the client."
  [query task-param-entity]
  (with query task-param-entity
              (join :parameter_values {:parameter_values.parameter_id :id})
              (fields :id
                      :name
                      :label
                      :description
                      :required
                      :parameter_values.value
                      [:data_format :format])))

(defn- get-tasks
  "Fetches a list of tasks for the given IDs with their inputs and outputs."
  [task-ids]
  (select tasks
    (fields :id
            :name
            :description)
    (with-task-params inputs)
    (with-task-params outputs)
    (where (in :id task-ids))))

(defn- format-task-file-param
  [file-parameter]
  (dissoc file-parameter :value))

(defn- format-task-output
  [{value :value :as output}]
  (-> output
    (assoc :label value)
    format-task-file-param))

(defn- format-task
  [task]
  (-> task
    (update-in [:inputs] (partial map (comp remove-nil-vals format-task-file-param)))
    (update-in [:outputs] (partial map (comp remove-nil-vals format-task-output)))))

(defn get-tasks-with-file-params
  "Fetches a formatted list of tasks for the given IDs with their inputs and outputs."
  [task-ids]
  (map format-task (get-tasks task-ids)))

(defn- format-app-task-listing
  [{app-id :id :as app}]
  (let [task-ids (map :task_id (select :app_steps (fields :task_id) (where {:app_id app-id})))
        tasks    (get-tasks-with-file-params task-ids)]
    (-> app
        (select-keys [:id :name :description])
        (assoc :tasks tasks))))

(defn get-app-task-listing
  "A service used to list the file parameters in an app."
  [{username :shortUsername} app-id]
  (perms/check-app-permissions username "read" [app-id])
  (let [app (get-app app-id)]
    (format-app-task-listing app)))

(defn get-app-tool-listing
  "A service to list the tools used by an app."
  [{username :shortUsername} app-id]
  (perms/check-app-permissions username "read" [app-id])
  (let [app (get-app app-id)
        tasks (:tasks (first (select apps
                               (with tasks (fields :tool_id))
                               (where {:apps.id app-id}))))
        tool-ids (map :tool_id tasks)]
    {:tools (get-tools-by-id tool-ids)}))

(defn get-category-id-for-app
  "Determines the category that an app is in. If the category can't be found for the app then
   the app is assumed to be in the 'Shared with me' category. This means that the ID of the
   'Shared with me' category will be returned if the user does not have access to the app. For
   this reason, it is important to verify that the user does, in fact, have access to the app
   when calling this function."
  [{:keys [username]} app-id]
  (or (amp/get-category-id-for-app username app-id (workspace-favorites-app-category-index))
      shared-with-me-id))

(defn get-app-input-ids
  "Gets the list of parameter IDs corresponding to input files."
  [app-id]
  (->> (amp/get-app-parameters app-id)
       (filter (comp amp/param-ds-input-types :type))
       (mapv #(str (:step_id %) "_" (:id %)))))
