(ns sharkbait.folders
  (:require [sharkbait.consts :as consts])
  (:import [edu.internet2.middleware.grouper Stem StemFinder]
           [edu.internet2.middleware.grouper.misc SaveMode]))

(def valid-privs #{:stem :create :attr-read :attr-update})

(defn folder-names
  "Formats the default folder names for the environment."
  [env]
  (into {} (map (fn [[k v]] (vector k (format v env))) consts/folder-format-strings)))

(defn find-folder
  "Finds a folder in Grouper, creating it and any parent folders if necessary."
  [session name]
  (or (StemFinder/findByName session name false)
      (Stem/saveStem session nil nil name nil nil SaveMode/INSERT true)))

(defn grant-privs
  "Grants privileges to a folder. The third parameter, privs, should be a set of keywords indicating which privileges
   to add. The set of valid keywords is stored in the variable, valid-privs."
  [folder subject privs & [{:keys [revoke-unselected?] :or {revoke-unselected? false}}]]
  (let [privs (set privs)]
     (.grantPrivs folder subject
                 (contains? privs :stem)
                 (contains? privs :create)
                 (contains? privs :attr-read)
                 (contains? privs :attr-update)
                 (boolean revoke-unselected?))))
