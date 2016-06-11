(ns apps.service.apps.de.sharing
  (:use [clostache.parser :only [render]]
        [slingshot.slingshot :only [try+]])
  (:require [apps.clients.permissions :as perms-client]
            [apps.persistence.app-metadata :as amp]
            [apps.service.apps.de.listings :as listings]
            [apps.service.apps.de.permissions :as perms]))

(def app-sharing-formats
  {:not-found    "app ID {{app-id}} does not exist"
   :load-failure "unable to load permissions for {{app-id}}: {{detail}}"
   :not-allowed  "insufficient privileges for app ID {{app-id}}"})

(defn- app-sharing-msg
  ([reason-code app-id]
     (app-sharing-msg reason-code app-id nil))
  ([reason-code app-id detail]
     (render (app-sharing-formats reason-code)
             {:app-id app-id
              :detail (or detail "unexpected error")})))

(defn share-app-with-user
  [{username :shortUsername :as user} sharee app-id level success-fn failure-fn]
  (try+
    (if-not (amp/app-exists? app-id)
      (failure-fn nil nil (app-sharing-msg :not-found app-id))
      (let [sharer-category (listings/get-category-id-for-app user app-id)
            sharee-category listings/shared-with-me-id]
        (if-not (perms/has-app-permission username app-id "own")
          (failure-fn sharer-category sharee-category (app-sharing-msg :not-allowed app-id))
          (if-let [failure-reason (perms-client/share-app app-id "user" sharee level)]
            (failure-fn sharer-category sharee-category failure-reason)
            (success-fn sharer-category sharee-category)))))
    (catch [:type :apps.service.apps.de.permissions/permission-load-failure] {:keys [reason]}
      (failure-fn nil nil (app-sharing-msg :load-failure app-id reason)))))

(defn unshare-app-with-user
  [{username :shortUsername :as user} sharee app-id success-fn failure-fn]
  (try+
   (if-not (amp/app-exists? app-id)
     (failure-fn nil (app-sharing-msg :not-found app-id))
     (let [sharer-category (listings/get-category-id-for-app user app-id)]
       (if-not (perms/has-app-permission username app-id "own")
         (failure-fn sharer-category (app-sharing-msg :not-allowed app-id))
         (if-let [failure-reason (perms-client/unshare-app app-id "user" sharee)]
           (failure-fn sharer-category failure-reason)
           (success-fn sharer-category)))))
   (catch [:type :apps.service.apps.de.permissions/permission-load-failure] {:keys [reason]}
       (failure-fn nil (app-sharing-msg :load-failure app-id reason)))))
