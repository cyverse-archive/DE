(ns apps.service.apps.test-utils
  (:use [apps.user :only [user-from-attributes]]))

(defn create-user [i]
  (let [username (str "testde" i)]
    {:user       username
     :first-name username
     :last-name  username
     :email      (str username "@mail.org")}))

(defn create-user-map []
  (->> (take 10 (iterate inc 1))
       (mapv (comp (juxt (comp keyword :user) identity) create-user))
       (into {})))

(def users (create-user-map))

(defn get-user [k]
  (user-from-attributes (users k)))
