(ns clojars.config
  (:require [clojure.tools.logging :as log]))

(defn resources [path]
  (when path
    (reverse
     (enumeration-seq
      (.getResources
       (.getContextClassLoader
        (Thread/currentThread))
       path)))))

(defn merge-nested [v1 v2]
  (if (and (map? v1) (map? v2))
    (merge-with merge-nested v1 v2)
    v2))

(defn load-config [url]
  (when url
    (read-string (slurp url))))

(def get-configs
  (delay
   (apply merge-with merge-nested
          (map load-config (resources "clojars.clj")))))

(defn config* [m ks]
  (reduce (fn [m k]
            (let [v (get m k ::not-found)]
              (if (= v ::not-found)
                (log/warn ks "isn't a valid config")
                v)))
          m
          ks))

(defn config [& ks]
  (config* @get-configs ks))
