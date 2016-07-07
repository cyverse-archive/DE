(ns sharkbait.permissions
  (:import [edu.internet2.middleware.grouper.attr AttributeDefNameSave AttributeDefSave AttributeDefType]
           [edu.internet2.middleware.grouper.attr.assign AttributeAssign]
           [edu.internet2.middleware.grouper.attr.finder AttributeDefFinder AttributeDefNameFinder]))

(defn find-permission-def
  "Finds a permission definition."
  ([full-permission-def-name]
   (AttributeDefFinder/findByName full-permission-def-name false))
  ([folder permission-def-name]
   (find-permission-def (str folder ":" permission-def-name))))

(defn- remove-permission-def-names
  "Removes all names associated with a permission definition."
  [permission-def]
  (dorun (map #(.delete %) (-> (AttributeDefNameFinder.)
                               (.assignAttributeDefId (.getId permission-def))
                               (.findAttributeNames)))))

(defn remove-permission-def
  "Removes a permission definition."
  ([full-permission-def-name]
   (when-let [permission-def (find-permission-def full-permission-def-name)]
     (remove-permission-def-names permission-def)
     (.delete permission-def)))
  ([folder permission-def-name]
   (remove-permission-def (str folder ":" permission-def-name))))
