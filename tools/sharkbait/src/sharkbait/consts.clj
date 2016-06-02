(ns sharkbait.consts)

;; Various folders used by the DE.
(def folder-format-strings
  {:de          "iplant:de:%s"
   :de-users    "iplant:de:%s:users"
   :de-apps     "iplant:de:%s:apps"
   :de-analyses "iplant:de:%s:analyses"})

;; Roles used by the DE.
(def de-users-role-name "de-users")

;; Permission definitions used by the DE.
(def app-permission-def-name      "app-permission-def")
(def analysis-permission-def-name "analysis-permission-def")

;; Common permission resources.
(def public-apps-resource-name "public-apps")
