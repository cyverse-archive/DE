(ns sharkbait.consts)

;; The username used by the DE.
(def de-username "de_grouper")

;; Various folders used by the DE.
(def de-folder          "iplant:de")
(def de-users-folder    "iplant:de:users")
(def de-apps-folder     "iplant:de:apps")
(def de-analyses-folder "iplant:de:analyses")

;; Roles used by the DE.
(def de-users-role-name "de-users")

;; Permission definitions used by the DE.
(def app-permission-def-name      "app-permission-def")
(def analysis-permission-def-name "analysis-permission-def")

;; Common permission resources.
(def public-apps-resource-name "public-apps")

;; Some full names for convenience
(def full-de-users-role-name (str de-users-folder ":" de-users-role-name))
