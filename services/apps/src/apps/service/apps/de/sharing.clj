(ns apps.service.apps.de.sharing
  (:use [clostache.parser :only [render]]
        [slingshot.slingshot :only [try+]])
  (:require [apps.clients.iplant-groups :as iplant-groups]
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

(defn- share-accessible-app
  [user sharee app-id level success-fn failure-fn perms]
  (let [sharer-category (listings/get-category-id-for-app user app-id)
        sharee-category listings/shared-with-me-id]
    (if (iplant-groups/lacks-permission-level perms "own" app-id)
      (failure-fn sharer-category sharee-category (app-sharing-msg :not-allowed app-id))
      (if-let [failure-reason (iplant-groups/share-app app-id sharee level)]
        (failure-fn sharer-category sharee-category failure-reason)
        (success-fn sharer-category sharee-category)))))

(defn- share-non-existent-app
  [app-id failure-fn]
  (when-not (amp/app-exists? app-id)
    (failure-fn nil nil (app-sharing-msg :not-found app-id))))

(defn- share-extant-app
  [user sharee app-id level success-fn failure-fn]
  (let [perms (perms/load-app-permissions (:shortUsername user) app-id)]
    (if (empty? (perms app-id))
      (failure-fn nil nil (app-sharing-msg :not-allowed app-id))
      (share-accessible-app user sharee app-id level success-fn failure-fn perms))))

(defn share-app-with-user
  [user sharee app-id level success-fn failure-fn]
  (try+
   (or (share-non-existent-app app-id failure-fn)
       (share-extant-app user sharee app-id level success-fn failure-fn))
   (catch [:type :apps.service.apps.de.permissions/permission-load-failure] {:keys [reason]}
     (failure-fn nil nil (app-sharing-msg :load-failure app-id reason)))))

(defn- unshare-accessible-app
  [user sharee app-id success-fn failure-fn perms]
  (let [sharer-category (listings/get-category-id-for-app user app-id)]
    (if (iplant-groups/lacks-permission-level perms "own" app-id)
      (failure-fn sharer-category (app-sharing-msg :not-allowed app-id))
      (if-let [failure-reason (iplant-groups/unshare-app app-id sharee)]
        (failure-fn sharer-category failure-reason)
        (success-fn sharer-category)))))

(defn- unshare-non-existent-app
  [app-id failure-fn]
  (when-not (amp/app-exists? app-id)
    (failure-fn nil (app-sharing-msg :not-found app-id))))

(defn- unshare-extant-app
  [user sharee app-id success-fn failure-fn]
  (let [perms (perms/load-app-permissions (:shortUsername user) app-id)]
    (if (empty? (perms app-id))
      (failure-fn nil (app-sharing-msg :not-allowed app-id))
      (unshare-accessible-app user sharee app-id success-fn failure-fn perms))))

(defn unshare-app-with-user
  [user sharee app-id success-fn failure-fn]
  (try+
   (or (unshare-non-existent-app app-id failure-fn)
       (unshare-extant-app user sharee app-id success-fn failure-fn))
   (catch [:type :apps.service.apps.de.permissions/permission-load-failure] {:keys [reason]}
       (failure-fn nil (app-sharing-msg :load-failure app-id reason)))))
