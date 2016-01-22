(ns sharkbait.members
  (:import [edu.internet2.middleware.grouper MemberFinder]))

(defn find-subject-member
  "Finds a member record for a subject, optionally creating it if necessary."
  [session subject create?]
  (MemberFinder/findBySubject session subject create?))
