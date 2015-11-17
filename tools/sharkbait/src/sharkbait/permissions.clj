(ns sharkbait.permissions
  (:import [edu.internet2.middleware.grouper.attr AttributeDefSave AttributeDefType]
           [edu.internet2.middleware.grouper.misc SaveMode]))

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
  [session permission-name folder]
  (-> (AttributeDefSave. session)
      (.assignAttributeDefType AttributeDefType/perm)
      (.assignName (str folder ":" permission-name))
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.assignToGroup true)
      (.assignToEffMembership true)
      (.save)
      (set-permission-actions)))
