(ns terrain.services.filesystem.root
  (:use [clojure-commons.validators])
  (:require [clojure-commons.json :as json]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [terrain.clients.data-info.raw :as data-raw]
            [terrain.services.filesystem.common-paths :as paths]))

(defn- format-roots
  [roots user]
  (letfn [(format-subdir [root] (assoc root :hasSubDirs true))
          (update-subdirs [root-list] (map format-subdir root-list))]
    (update-in roots [:roots] update-subdirs)))

(defn do-root-listing
  [{user :user}]
  (-> (data-raw/list-roots user)
      :body
      (json/string->json true)
      (format-roots user)))

(with-pre-hook! #'do-root-listing
  (fn [params]
    (paths/log-call "do-root-listing" params)
    (validate-map params {:user string?})))

(with-post-hook! #'do-root-listing (paths/log-func "do-root-listing"))
