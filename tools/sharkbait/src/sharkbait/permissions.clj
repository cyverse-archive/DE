(ns sharkbait.permissions
  (:import [edu.internet2.middleware.grouper.attr AttributeDefNameSave AttributeDefSave AttributeDefType]
           [edu.internet2.middleware.grouper.attr.assign AttributeAssign]
           [edu.internet2.middleware.grouper.attr.finder AttributeDefFinder]
           [edu.internet2.middleware.grouper.misc SaveMode]))

(def read  "read")
(def write "write")
(def own   "own")

(defn- add-implied-action
  "Creates a relationship between two actions so that one action implies another."
  [action-delegate implying-action-name implied-action-name]
  (let [implying-action (.findAction action-delegate implying-action-name true)
        implied-action  (.findAction action-delegate implied-action-name true)]
    (-> (.getAttributeAssignActionSetDelegate implying-action)
        (.addToAttributeAssignActionSet implied-action))))

(defn- set-permission-actions
  "Sets the allowed actions for a permission definition."
  [permission-def]
  (let [delegate (.getAttributeDefActionDelegate permission-def)]
    (.configureActionList delegate ["read" "write" "own"])
    (add-implied-action delegate "own" "write")
    (add-implied-action delegate "write" "read"))
  (.store permission-def))

(defn create-permission-def
  "Creates a permission definition."
  [session folder permission-def-name]
  (println (str "Creating permission def:" permission-def-name "..."))
  (-> (AttributeDefSave. session)
      (.assignAttributeDefType AttributeDefType/perm)
      (.assignName (str folder ":" permission-def-name))
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.assignToGroup true)
      (.assignToEffMembership true)
      (.save)
      (set-permission-actions)))

(defn find-permission-def
  "Finds a permission definition."
  [folder permission-def-name]
  (AttributeDefFinder/findByName (str folder ":" permission-def-name) true))

(defn create-permission-resource
  "Creates a permission resource."
  [session permission-def folder permission-name]
  (-> (AttributeDefNameSave. session permission-def)
      (.assignName (str folder ":" permission-name))
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.save)))

(defn grant-permission
  "Grants permission to a Grouper role or membership."
  [role-or-membership action permission-def]
  (.saveOrUpdate (AttributeAssign. role-or-membership action permission-def nil)))
