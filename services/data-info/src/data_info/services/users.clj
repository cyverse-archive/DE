(ns data-info.services.users
  (:use [clj-jargon.init :only [with-jargon]])
  (:require [clojure.tools.logging :as log]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [data-info.services.permissions :as perms]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as dul]
            [data-info.util.validators :as validators]))

(defn- list-perm
  [cm user abspath]
  {:path abspath
   :user-permissions (perms/filtered-user-perms cm user abspath)})

(defn- list-perms
  [user abspaths]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (validators/all-paths-exist cm abspaths)
    (validators/user-owns-paths cm user abspaths)
    (mapv (partial list-perm cm user) abspaths)))

(defn do-user-permissions
  [{user :user} {paths :paths}]
  {:paths (list-perms user paths)})

(with-pre-hook! #'do-user-permissions
  (fn [params body]
    (dul/log-call "do-user-permissions" params body)
    (validators/validate-num-paths (:paths body))))

(with-post-hook! #'do-user-permissions (dul/log-func "do-user-permissions"))
