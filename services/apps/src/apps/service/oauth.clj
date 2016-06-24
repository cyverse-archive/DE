(ns apps.service.oauth
  "Service implementations dealing with OAuth 2.0 authentication."
  (:use [apps.user :only [current-user]]
        [slingshot.slingshot :only [throw+]])
  (:require [apps.persistence.oauth :as op]
            [apps.util.config :as config]
            [apps.util.service :as service]
            [clojure-commons.exception-util :as cxu]
            [authy.core :as authy]))

(defn- build-authy-server-info
  "Builds the server info to pass to authy."
  [server-info token-callback]
  (assoc (dissoc server-info :api-name)
    :token-callback token-callback))

(def ^:private server-info-fn-for
  {:agave config/agave-oauth-settings})

(defn- get-server-info
  "Retrieves the server info for the given API name."
  [api-name]
  (if-let [server-info-fn (server-info-fn-for (keyword api-name))]
    (server-info-fn)
    (throw+ {:type  :clojure-commons.exception/bad-request-field
             :error (str "unknown API name: " api-name)})))

(defn get-access-token
  "Receives an OAuth authorization code and obtains an access token."
  [api-name {:keys [code state]}]
  (let [server-info    (get-server-info api-name)
        username       (:username current-user)
        state-info     (op/retrieve-authorization-request-state state username)
        token-callback (partial op/store-access-token api-name username)]
    (authy/get-access-token (build-authy-server-info server-info token-callback) code)
    {:state_info state-info}))

(defn- format-admin-token-info
  "Formats access token info for administrative endpoints."
  [token-info]
  (when token-info
    {:access_token  (:access-token token-info)
     :expires_at    (.. (:expires-at token-info) toInstant toEpochMilli)
     :refresh_token (:refresh-token token-info)
     :webapp        (:webapp token-info)}))

(defn- format-token-info
  "Formats access token info."
  [token-info]
  (when token-info
    (-> (format-admin-token-info token-info)
        (select-keys [:expires_at :webapp]))))

(defn get-token-info
  "Retrieves the user's token information, excluding the actual tokens, for an external API if it exists."
  [api-name {:keys [username]}]
  (or (format-token-info (op/get-access-token api-name username))
      (cxu/not-found "access token not found" :api_name api-name)))

(defn get-admin-token-info
  "Retrieves the user's token information for an external API if it exists."
  [api-name {:keys [username]}]
  (or (format-admin-token-info (op/get-access-token api-name username))
      (cxu/not-found "access token not found" :api_name api-name)))
