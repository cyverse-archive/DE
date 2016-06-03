(ns terrain.auth.user-attributes
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure-commons.response :as resp]
            [clojure-commons.exception :as cx]
            [terrain.util.config :as cfg]
            [terrain.util.jwt :as jwt]))


(def
  ^{:doc "The authenticated user or nil if the service is unsecured."
    :dynamic true}
  current-user nil)

;; TODO: fix common name retrieval when we add it as an attribute.
(defn user-from-attributes
  "Creates a map of values from user attributes obtained during the authentication process."
  [{:keys [user-attributes]}]
  (log/trace user-attributes)
  (let [first-name (get user-attributes "firstName")
        last-name  (get user-attributes "lastName")]
    {:username      (str (get user-attributes "uid") "@" (cfg/uid-domain)),
     :password      (get user-attributes "password"),
     :email         (get user-attributes "email"),
     :shortUsername (get user-attributes "uid")
     :firstName     first-name
     :lastName      last-name
     :commonName    (str first-name " " last-name)
     :principal     (get user-attributes "principal")}))

(defn user-from-de-jwt-claims
  "Creates a map of values from JWT claims stored in the request by the DE."
  [{:keys [jwt-claims]}]
  (jwt/terrain-user-from-jwt-claims jwt-claims))

(defn user-from-wso2-jwt-claims
  "Creates a map of values from JWT claims stored int he request by WSO2."
  [{:keys [jwt-claims]}]
  (jwt/terrain-user-from-jwt-claims jwt-claims jwt/user-from-wso2-assertion))

(defn fake-user-from-attributes
  "Creates a real map of fake values for a user base on environment variables."
  [& _]
  {:username      (System/getenv "IPLANT_CAS_USER")
   :password      (System/getenv "IPLANT_CAS_PASS")
   :email         (System/getenv "IPLANT_CAS_EMAIL")
   :shortUsername (System/getenv "IPLANT_CAS_SHORT")
   :firstName     (System/getenv "IPLANT_CAS_FIRST")
   :lastName      (System/getenv "IPLANT_CAS_LAST")
   :commonName    (System/getenv "IPLANT_CAS_COMMON")})

(defn- user-info-from-current-user
  "Converts the current-user to the user info structure expected in the request."
  [user]
  {:user       (:shortUsername user)
   :email      (:email user)
   :first-name (:firstName user)
   :last-name  (:lastName user)})

(defn wrap-current-user
  "Generates a Ring handler function that stores user information in current-user."
  [handler user-info-fn]
  (fn [request]
    (binding [current-user (user-info-fn request)]
      (handler (assoc request :user-info (user-info-from-current-user current-user))))))

(defn- find-auth-handler
  "Finds an authentication handler for a request."
  [request phs]
  (->> (remove (fn [[token-fn _]] (nil? (token-fn request))) phs)
       (first)
       (second)))

(defn- wrap-auth-selection
  "Generates a ring handler function that selects the authentication method based on predicates."
  [phs]
  (fn [request]
    (log/log 'AccessLogger :trace nil "entering terrain.auth.user-attributes/wrap-auth-selection")
    (if-let [auth-handler (find-auth-handler request phs)]
      (auth-handler request)
      (resp/unauthorized "No authentication information found in request."))))

(defn- get-fake-auth
  "Returns a non-nil value if we're using fake authentication."
  [_]
  (System/getenv "IPLANT_CAS_FAKE"))

(defn- get-de-jwt-assertion
  "Extracts a JWT assertion from the request header used by the DE, returning nil if none is
   found."
  [request]
  (get (:headers request) "x-iplant-de-jwt"))

(defn- get-wso2-jwt-assertion
  "Extracts a JWT assertion from the request header used by WSO2, returning nil if none is
   found."
  [request]
  (when-let [header-name (cfg/wso2-jwt-header)]
    (get (:headers request) (string/lower-case header-name))))

(defn- wrap-fake-auth
  [handler]
  (wrap-current-user handler fake-user-from-attributes))

(defn- wrap-de-jwt-auth
  [handler]
  (-> (wrap-current-user handler user-from-de-jwt-claims)
      (jwt/validate-jwt-assertion get-de-jwt-assertion)))

(defn- wrap-wso2-jwt-auth
  [handler]
  (-> (wrap-current-user handler user-from-wso2-jwt-claims)
      (jwt/validate-jwt-assertion get-wso2-jwt-assertion jwt/user-from-wso2-assertion)))

(defn authenticate-current-user
  "Authenticates the user and binds current-user to a map that is built from the user attributes retrieved
   during the authentication process."
  [handler]
  (wrap-auth-selection [[get-fake-auth          (wrap-fake-auth handler)]
                        [get-de-jwt-assertion   (wrap-de-jwt-auth handler)]
                        [get-wso2-jwt-assertion (wrap-wso2-jwt-auth handler)]]))

(defn validate-current-user
  "Verifies that the user belongs to one of the groups that are permitted to access the resource."
  [handler]
  (wrap-auth-selection
   [[get-fake-auth          handler]
    [get-de-jwt-assertion   (jwt/validate-group-membership handler cfg/allowed-groups)]
    [get-wso2-jwt-assertion (constantly (resp/forbidden "Admin not supported for WSO2."))]]))

(defn fake-store-current-user
  "Fake storage of a user"
  [handler & _]
  (fn [req]
    (log/info "Storing current user from IPLANT_CAS_* env vars.")
    (binding [current-user (fake-user-from-attributes req)]
      (handler req))))

(defmacro with-user
  "Performs a task with the given user information bound to current-user. This macro is used
   for debugging in the REPL."
  [[user] & body]
  `(binding [current-user (user-from-attributes {:user-attributes ~user})]
     (do ~@body)))
