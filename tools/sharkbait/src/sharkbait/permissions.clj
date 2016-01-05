(ns sharkbait.permissions
  (:import [edu.internet2.middleware.grouper.attr AttributeDefNameSave AttributeDefSave AttributeDefType]
           [edu.internet2.middleware.grouper.attr.assign AttributeAssign]
           [edu.internet2.middleware.grouper.attr.finder AttributeDefFinder]
           [edu.internet2.middleware.grouper.misc SaveMode]
           [edu.internet2.middleware.grouper.permissions PermissionFinder]))

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

(defn role-permission-exists?
  "Determines whether or not a role permission exists."
  [role action permission-def-name]
  (-> (PermissionFinder.)
      (.addRole role)
      (.assignActions [action])
      (.addPermissionName permission-def-name)
      (.findPermissions)
      (seq)))

(defn grant-role-permission
  "Grants permission to a Grouper role."
  [role action permission-def-name]
  (when-not (role-permission-exists? role action permission-def-name)
    (.saveOrUpdate (AttributeAssign. role action permission-def-name nil))))

(defn role-membership-permission-exists?
  "Determines whether or not a membershiup within a role has permission to a resource."
  [role member action permission-def-name]
  (-> (PermissionFinder.)
      (.addRole role)
      (.addMemberId (.getId member))
      (.assignActions [action])
      (.addPermissionName permission-def-name)
      (.findPermissions)
      (seq)))

(defn grant-role-membership-permission
  "Grants permission to a membership within a role."
  [role member action permission-def-name]
  (when-not (role-membership-permission-exists? role member action permission-def-name)
    (.saveOrUpdate (AttributeAssign. role member action permission-def-name nil))))
