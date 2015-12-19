(ns sharkbait.subjects
  (:require [clojure.string :as string])
  (:import [edu.internet2.middleware.grouper SubjectFinder]))

(def find-subject
  "Finds a subject with the given ID. This function is memoized because it appears that searching
  for a subject more than once can cause null pointer exceptions."
  (memoize (fn [subject-id required?] (SubjectFinder/findByIdentifier subject-id required?))))

(def find-root-subject
  "Returns the root subject. This function is memoized for efficiency, since the root subject won't
  change during a session."
  (memoize (fn [] (SubjectFinder/findRootSubject))))

(defn find-subjects
  "Finds all subjects with usernames in the given list of usernames. This function produces some false
  positives, but it was far more efficient than calling searchByIdentifier for each identifier separately
  or calling searchByIdentifiers. This function compensates for the false positives by filtering the result
  set. "
  [ids]
  (let [id-set (set ids)]
    (filter #(contains? id-set (.getId %))
            (SubjectFinder/findAll (string/join "," ids)))))
