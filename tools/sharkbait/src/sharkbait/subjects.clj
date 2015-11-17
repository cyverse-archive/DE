(ns sharkbait.subjects
  (:import [edu.internet2.middleware.grouper SubjectFinder]))

(def find-subject
  "Finds a subject with the given ID. This function is memoized because it appears that searching
  for a subject more than once can cause null pointer exceptions."
  (memoize (fn [subject-id] (SubjectFinder/findById subject-id true))))

(def find-root-subject
  "Returns the root subject. This function is memoized for efficiency, since the root subject won't
  change during a session."
  (memoize (fn [] (SubjectFinder/findRootSubject))))
