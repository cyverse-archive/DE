(ns terrain.routes.filesystem
  (:use [compojure.core]
        [terrain.util])
  (:require [terrain.util.config :as config]
            [clojure.tools.logging :as log]
            [terrain.clients.data-info :as data]
            [terrain.services.filesystem.directory :as dir]
            [terrain.services.filesystem.manifest :as manifest]
            [terrain.services.filesystem.metadata :as meta]
            [terrain.services.filesystem.metadata-templates :as mt]
            [terrain.services.filesystem.root :as root]
            [terrain.services.filesystem.sharing :as sharing]
            [terrain.services.filesystem.stat :as stat]
            [terrain.services.filesystem.tickets :as ticket]
            [terrain.services.filesystem.updown :as ud]))

(defn secured-filesystem-routes
  "The routes for file IO endpoints."
  []
  (optional-routes
    [config/filesystem-routes-enabled]

    (GET "/filesystem/root" [:as req]
      (controller req root/do-root-listing :params))

    (POST "/filesystem/exists" [:as req]
      (controller req data/check-existence :params :body))

    (POST "/filesystem/stat" [:as req]
      (controller req stat/do-stat :params :body))

    (GET "/filesystem/display-download" [:as req]
      (controller req ud/do-special-download :params))

    (GET "/filesystem/directory" [:as req]
      (controller req dir/do-directory :params))

    (GET "/filesystem/paged-directory" [:as req]
      (controller req dir/do-paged-listing :params))

    (POST "/filesystem/directories" [:as req]
      (controller req data/create-dirs :params :body))

    (POST "/filesystem/directory/create" [:as req]
      (controller req data/create-dir :params :body))

    (POST "/filesystem/rename" [:as req]
      (controller req data/rename :params :body))

    (POST "/filesystem/delete" [:as req]
      (controller req data/delete-paths :params :body))

    (POST "/filesystem/delete-contents" [:as req]
      (controller req data/delete-contents :params :body))

    (POST "/filesystem/move" [:as req]
      (controller req data/move :params :body))

    (POST "/filesystem/move-contents" [:as req]
      (controller req data/move-contents :params :body))

    (GET "/filesystem/file/manifest" [:as req]
      (controller req manifest/do-manifest :params))

    (POST "/filesystem/user-permissions" [:as req]
      (controller req data/collect-permissions :params :body))

    (POST "/filesystem/restore" [:as req]
      (controller req data/restore-files :params :body))

    (POST "/filesystem/restore-all" [:as req]
      (controller req data/restore-files :params))

    (POST "/filesystem/tickets" [:as req]
      (controller req ticket/do-add-tickets :params :body))

    (POST "/filesystem/delete-tickets" [:as req]
      (controller req ticket/do-remove-tickets :params :body))

    (POST "/filesystem/list-tickets" [:as req]
      (controller req ticket/do-list-tickets :params :body))

    (DELETE "/filesystem/trash" [:as req]
      (controller req data/delete-trash :params))

    (POST "/filesystem/read-chunk" [:as req]
      (controller req data/read-chunk :params :body))

    (POST "/filesystem/read-csv-chunk" [:as req]
      (controller req data/read-tabular-chunk :params :body))

    (POST "/filesystem/anon-files" [:as req]
      (controller req data/share-with-anonymous :params :body))))

(defn secured-filesystem-metadata-routes
  "The routes for file metadata endpoints."
  []
  (optional-routes
   [#(and (config/filesystem-routes-enabled)
          (config/metadata-routes-enabled))]

    (POST "/filesystem/metadata/csv-parser" [:as {:keys [user-info params] :as req}]
      (meta/parse-metadata-csv-file user-info params))

    (GET "/filesystem/metadata/templates" [:as req]
      (controller req mt/do-metadata-template-list))

    (GET "/filesystem/metadata/template/:template-id" [template-id :as req]
      (controller req mt/do-metadata-template-view template-id))

    (GET "/filesystem/metadata/template/attr/:attr-id" [attr-id :as req]
      (controller req mt/do-metadata-attribute-view attr-id))

    (GET "/filesystem/:data-id/metadata" [data-id :as req]
      (controller req meta/do-metadata-get :params data-id))

    (POST "/filesystem/:data-id/metadata" [data-id :as req]
      (controller req meta/do-metadata-set data-id :params :body))

    (POST "/filesystem/:data-id/metadata/copy" [data-id force :as req]
      (controller req meta/do-metadata-copy :params data-id force :body))

    (POST "/filesystem/:data-id/metadata/save" [data-id :as req]
      (controller req meta/do-metadata-save data-id :params :body))))

(defn admin-filesystem-metadata-routes
  "The admin routes for file metadata endpoints."
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
           (config/filesystem-routes-enabled)
           (config/metadata-routes-enabled))]

    (GET "/filesystem/metadata/templates" [:as req]
      (controller req mt/do-metadata-template-admin-list))

    (POST "/filesystem/metadata/templates" [:as req]
      (controller req mt/do-metadata-template-add :body))

    (POST "/filesystem/metadata/templates/:template-id" [template-id :as req]
      (controller req mt/do-metadata-template-edit template-id :body))

    (DELETE "/filesystem/metadata/templates/:template-id" [template-id :as req]
      (controller req mt/do-metadata-template-delete template-id))))
