(ns clojars.validate
  (:require [clojure.string :refer [blank?]])
  (:import (org.apache.commons.validator.routines EmailValidator)))

(defn blank-protector [value]
  (if (blank? value)
    ""
    value))

(defn email-validator [field m]
  (or (.isValid (EmailValidator/getInstance) (m field))
      [field "Invalid email address"]))

(defn not-blank-validator [field m]
  (let [v (m field)]
    (or (and (not (nil? v))
             (not (blank? v)))
        [field "Cannot be blank"])))

(defn equality-validator [fields m]
  (or (apply = (map m fields))
      [fields "Must match"]))

(defn username-validator [field m]
  (or (boolean (re-matches #"[a-z0-9_-]+" (blank-protector (m field))))
      [field (str "Username must consist only of lowercase "
                  "letters, numbers, hyphens and underscores.")]))

(defn ssh-key-validator [field m]
  (let [v (m field)]
    (or (blank? v)
        (boolean (re-matches #"(ssh-\w+ \S+|\d+ \d+ \D+).*\s*" v))
        [field "Invalid SSH public key"])))

(defn validate [m & validators]
  (reduce (fn [acc validator-fn]
            (let [result (validator-fn m)]
              (if (true? result)
                acc
                (update-in acc (take 1 result) concat (rest result)))))
          {}
          validators))
