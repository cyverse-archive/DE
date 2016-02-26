(ns mescal.test
  (:require [authy.core :as authy]
            [cemerick.url :as curl]
            [mescal.core :as mc]
            [mescal.de :as md]))

(defn- get-agave-base-url []
  (System/getenv "AGAVE_BASE_URL"))

(defn- get-agave-storage-system []
  (System/getenv "AGAVE_STORAGE_SYSTEM"))

(defn- get-api-key []
  (System/getenv "AGAVE_API_KEY"))

(defn- get-api-secret []
  (System/getenv "AGAVE_API_SECRET"))

(defn- prompt-for-username []
  (print "username: ")
  (flush)
  (read-line))

(defn- prompt-for-password []
  (print "password: ")
  (flush)
  (.. System console readPassword))

(defn- get-username []
  (or (System/getenv "IPLANT_CAS_SHORT")
      (prompt-for-username)))

(defn- get-password []
  (or (System/getenv "IPLANT_CAS_PASS")
      (prompt-for-password)))

(defn- get-oauth-info [base-url api-key api-secret]
  {:api-name      "agave"
   :client-key    api-key
   :client-secret api-secret
   :token-uri     (str (curl/url base-url "oauth2" "token"))})

(defn- get-token [base-url api-key api-secret username password]
  (let [oauth-info (get-oauth-info base-url api-key api-secret)]
    (authy/get-access-token-for-credentials oauth-info username password)))

(defn get-test-agave-client
  ([]
     (get-test-agave-client {}))
  ([agave-params]
     (get-test-agave-client agave-params (get-username)))
  ([agave-params username]
     (get-test-agave-client agave-params username (get-password)))
  ([agave-params username password]
     (get-test-agave-client agave-params username password (get-api-key) (get-api-secret)))
  ([agave-params username password api-key api-secret]
     (let [base-url       (get-agave-base-url)
           storage-system (get-agave-storage-system)
           token-info     (get-token base-url api-key api-secret username password)
           agave-params   (flatten (seq agave-params))]
       (apply mc/agave-client-v2 base-url storage-system (constantly token-info) agave-params))))

(defn get-test-de-agave-client
  ([]
     (get-test-de-agave-client {}))
  ([agave-params]
     (get-test-de-agave-client agave-params true))
  ([agave-params jobs-enabled?]
     (get-test-de-agave-client agave-params jobs-enabled? (get-username)))
  ([agave-params jobs-enabled? username]
     (get-test-de-agave-client agave-params jobs-enabled? username (get-password)))
  ([agave-params jobs-enabled? username password]
     (get-test-de-agave-client agave-params jobs-enabled? username password (get-api-key) (get-api-secret)))
  ([agave-params jobs-enabled? username password api-key api-secret]
     (let [base-url       (get-agave-base-url)
           storage-system (get-agave-storage-system)
           token-info     (get-token base-url api-key api-secret username password)
           agave-params   (flatten (seq agave-params))]
       (apply md/de-agave-client-v2 base-url storage-system (constantly token-info) jobs-enabled? agave-params))))
