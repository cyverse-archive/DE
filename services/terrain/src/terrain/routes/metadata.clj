(ns terrain.routes.metadata
  (:use [compojure.core]
        [terrain.services.file-listing]
        [terrain.services.metadata.apps]
        [terrain.util])
  (:require [clojure.tools.logging :as log]
            [terrain.clients.apps.raw :as apps]
            [terrain.util.config :as config]
            [terrain.util.service :as service]))

(defn app-category-routes
  []
  (optional-routes
    [config/app-routes-enabled]

    (GET "/apps/categories" [:as {params :params}]
         (service/success-response (apps/get-app-categories params)))

    (GET "/apps/categories/:category-id" [category-id :as {params :params}]
         (service/success-response (apps/apps-in-category category-id params)))))

(defn admin-category-routes
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
           (config/app-routes-enabled))]

    (GET "/apps/categories" [:as {params :params}]
         (service/success-response (apps/get-admin-app-categories params)))

    (POST "/apps/categories" [:as {:keys [body]}]
          (service/success-response (apps/add-category body)))

    (POST "/apps/categories/shredder" [:as {:keys [body]}]
          (service/success-response (apps/delete-categories body)))

    (DELETE "/apps/categories/:category-id" [category-id]
            (service/success-response (apps/delete-category category-id)))

    (PATCH "/apps/categories/:category-id" [category-id :as {:keys [body]}]
           (service/success-response (apps/update-category category-id body)))))

(defn admin-apps-routes
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
           (config/app-routes-enabled))]

    (POST "/apps" [:as {:keys [body]}]
          (service/success-response (apps/categorize-apps body)))

    (POST "/apps/shredder" [:as {:keys [body]}]
          (service/success-response (apps/permanently-delete-apps body)))

    (DELETE "/apps/:app-id" [app-id]
            (service/success-response (apps/admin-delete-app app-id)))

    (PATCH "/apps/:app-id" [app-id :as {:keys [body]}]
           (service/success-response (apps/admin-update-app app-id body)))

    (POST "/apps/:app-id/documentation" [app-id :as {:keys [body]}]
          (service/success-response (apps/admin-add-app-docs app-id body)))

    (PATCH "/apps/:app-id/documentation" [app-id :as {:keys [body]}]
           (service/success-response (apps/admin-edit-app-docs app-id body)))))

(defn apps-routes
  []
  (optional-routes
    [config/app-routes-enabled]

    (GET "/apps" [:as {params :params}]
         (service/success-response (apps/search-apps params)))

    (POST "/apps" [:as {:keys [body]}]
          (service/success-response (apps/create-app body)))

    (POST "/apps/arg-preview" [:as {:keys [body]}]
          (service/success-response (apps/preview-args body)))

    (GET "/apps/ids" []
         (service/success-response (apps/list-app-ids)))

    (GET "/apps/elements" [:as {:keys [params]}]
         (service/success-response (apps/get-all-workflow-elements params)))

    (GET "/apps/elements/:element-type" [element-type :as {:keys [params]}]
         (service/success-response (apps/get-workflow-elements element-type params)))

    (POST "/apps/pipelines" [:as {:keys [body]}]
          (service/success-response (apps/add-pipeline body)))

    (PUT "/apps/pipelines/:app-id" [app-id :as {:keys [body]}]
         (service/success-response (apps/update-pipeline app-id body)))

    (POST "/apps/pipelines/:app-id/copy" [app-id]
          (service/success-response (apps/copy-pipeline app-id)))

    (GET "/apps/pipelines/:app-id/ui" [app-id]
         (service/success-response (apps/edit-pipeline app-id)))

    (POST "/apps/shredder" [:as {:keys [body]}]
          (service/success-response (apps/delete-apps body)))

    (POST "/apps/permission-lister" [:as {:keys [body]}]
          (service/success-response (apps/list-permissions body)))

    (POST "/apps/sharing" [:as {:keys [body]}]
          (service/success-response (apps/share body)))

    (POST "/apps/unsharing" [:as {:keys [body]}]
          (service/success-response (apps/unshare body)))

    (GET "/apps/:app-id" [app-id]
         (service/success-response (apps/get-app app-id)))

    (DELETE "/apps/:app-id" [app-id]
            (service/success-response (apps/delete-app app-id)))

    (PATCH "/apps/:app-id" [app-id :as {:keys [body]}]
           (service/success-response (apps/relabel-app app-id body)))

    (PUT "/apps/:app-id" [app-id :as {:keys [body]}]
         (service/success-response (apps/update-app app-id body)))

    (POST "/apps/:app-id/copy" [app-id]
          (service/success-response (apps/copy-app app-id)))

    (GET "/apps/:app-id/details" [app-id]
         (service/success-response (apps/get-app-details app-id)))

    (GET "/apps/:app-id/documentation" [app-id]
         (service/success-response (apps/get-app-docs app-id)))

    (POST "/apps/:app-id/documentation" [app-id :as {:keys [body]}]
          (service/success-response (apps/add-app-docs app-id body)))

    (PATCH "/apps/:app-id/documentation" [app-id :as {:keys [body]}]
           (service/success-response (apps/edit-app-docs app-id body)))

    (DELETE "/apps/:app-id/favorite" [app-id]
            (service/success-response (apps/remove-favorite-app app-id)))

    (PUT "/apps/:app-id/favorite" [app-id]
         (service/success-response (apps/add-favorite-app app-id)))

    (GET "/apps/:app-id/is-publishable" [app-id]
         (service/success-response (apps/app-publishable? app-id)))

    (POST "/apps/:app-id/publish" [app-id :as {:keys [body]}]
          (service/success-response (apps/make-app-public app-id body)))

    (DELETE "/apps/:app-id/rating" [app-id]
            (service/success-response (apps/delete-rating app-id)))

    (POST "/apps/:app-id/rating" [app-id :as {body :body}]
          (service/success-response (apps/rate-app app-id body)))

    (GET "/apps/:app-id/tasks" [app-id]
         (service/success-response (apps/list-app-tasks app-id)))

    (GET "/apps/:app-id/tools" [app-id]
         (service/success-response (apps/get-tools-in-app app-id)))

    (GET "/apps/:app-id/ui" [app-id]
         (service/success-response (apps/get-app-ui app-id)))))

(defn analysis-routes
  []
  (optional-routes
   [config/app-routes-enabled]

   (GET "/analyses" [:as {:keys [params]}]
        (service/success-response (apps/list-jobs params)))

   (POST "/analyses" [:as {:keys [body]}]
         (service/success-response (apps/submit-job body)))

   (POST "/analyses/permission-lister" [:as {:keys [body]}]
         (service/success-response (apps/list-job-permissions body)))

   (POST "/analyses/sharing" [:as {:keys [body]}]
         (service/success-response (apps/share-jobs body)))

   (POST "/analyses/unsharing" [:as {:keys [body]}]
         (service/success-response (apps/unshare-jobs body)))

   (PATCH "/analyses/:analysis-id" [analysis-id :as {body :body}]
          (service/success-response (apps/update-job analysis-id body)))

   (DELETE "/analyses/:analysis-id" [analysis-id]
           (service/success-response (apps/delete-job analysis-id)))

   (POST "/analyses/shredder" [:as {:keys [body]}]
         (service/success-response (apps/delete-jobs body)))

   (GET "/analyses/:analysis-id/parameters" [analysis-id]
        (service/success-response (apps/get-job-params analysis-id)))

   (GET "/analyses/:analysis-id/relaunch-info" [analysis-id]
        (service/success-response (apps/get-job-relaunch-info analysis-id)))

   (GET "/analyses/:analysis-id/steps" [analysis-id]
        (service/success-response (apps/list-job-steps analysis-id)))

   (POST "/analyses/:analysis-id/stop" [analysis-id]
         (service/success-response (apps/stop-job analysis-id)))))

(defn admin-reference-genomes-routes
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
           (config/app-routes-enabled))]

    (POST "/reference-genomes" [:as req]
          (add-reference-genome req))

    (PUT "/reference-genomes" [:as req]
         (replace-reference-genomes req))

    (DELETE "/reference-genomes/:reference-genome-id" [reference-genome-id]
            (delete-reference-genomes reference-genome-id))

    (PATCH "/reference-genomes/:reference-genome-id" [reference-genome-id :as req]
           (update-reference-genome req reference-genome-id))))

(defn reference-genomes-routes
  []
  (optional-routes
    [config/app-routes-enabled]

    (GET "/reference-genomes" [:as {params :params}]
         (service/success-response (apps/list-reference-genomes params)))

    (GET "/reference-genomes/:reference-genome-id" [reference-genome-id]
         (service/success-response (apps/get-reference-genome reference-genome-id)))))

(defn admin-tool-routes
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
        (config/app-routes-enabled))]

    (POST "/tools" [:as {:keys [body]}]
          (import-tools body))

    (DELETE "/tools/:tool-id" [tool-id]
            (apps/delete-tool tool-id))

    (PATCH "/tools/:tool-id" [tool-id :as {:keys [params body]}]
           (apps/update-tool tool-id params body))

    (GET "/tool-requests" [:as {params :params}]
         (admin-list-tool-requests params))

    (GET "/tool-requests/:request-id" [request-id]
         (get-tool-request request-id))

    (POST "/tool-requests/:request-id/status" [request-id :as req]
          (update-tool-request req request-id))))

(defn tool-routes
  []
  (optional-routes
    [config/app-routes-enabled]

    (GET "/tools" [:as {:keys [params]}]
         (service/success-response (apps/search-tools params)))

    (GET "/tools/:tool-id" [tool-id]
         (service/success-response (apps/get-tool tool-id)))

    (GET "/tool-requests" []
         (list-tool-requests))

    (POST "/tool-requests" [:as req]
          (submit-tool-request req))

    (GET "/tool-requests/status-codes" [:as {params :params}]
         (list-tool-request-status-codes params))))

(defn secured-metadata-routes
  []
  (optional-routes
   [config/app-routes-enabled]

   (GET "/bootstrap" [:as req]
        (bootstrap req))

   (GET "/logout" [:as {params :params}]
        (logout params))

   (GET "/default-output-dir" []
        (get-default-output-dir))

   (POST "/default-output-dir" [:as {body :body}]
         (reset-default-output-dir body))

   (PUT "/feedback" [:as {body :body}]
        (provide-user-feedback body))))
