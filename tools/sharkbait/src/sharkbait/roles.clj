(ns sharkbait.roles
  (:import [edu.internet2.middleware.grouper GroupFinder GroupSave MembershipFinder]
           [edu.internet2.middleware.grouper.group TypeOfGroup]
           [edu.internet2.middleware.grouper.membership MembershipType]
           [edu.internet2.middleware.grouper.misc SaveMode]))

(defn create-role
  "Creates a Grouper role."
  [session folder role-name]
  (-> (GroupSave. session)
      (.assignName (str folder ":" role-name))
      (.assignTypeOfGroup TypeOfGroup/role)
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.save)))

(defn replace-members
  "Replaces the members of a role."
  [role subjects]
  (.replaceMembers role subjects))

(defn find-role
  "Finds a Grouper role."
  [session role-name]
  (GroupFinder/findByName session role-name true))

(defn find-effective-membership
  "Finds an effective membership in a role."
  [session role subject]
  (first (MembershipFinder/findEffectiveMemberships session role subject nil nil 0)))
