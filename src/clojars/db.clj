(ns clojars.db
  (:require [clojars.config :refer [config]]
            [korma.db :refer [defdb transaction rollback]]
            [korma.core :refer [defentity select group fields order
                                modifier exec-raw where limit values
                                raw insert update set-fields]]))

(defdb mydb (config :db))
(defentity users)
(defentity groups)
(defentity jars)

;; jars

(defn find-jar
  ([groupname jarname]
     (or (first (select jars
                        (where (and {:group_name groupname
                                     :jar_name jarname}
                                    (raw "version NOT LIKE '%-SNAPSHOT'")))
                        (order :created :desc)
                        (limit 1)))
         (first (select jars
                        (where (and {:group_name groupname
                                     :jar_name jarname
                                     :version [like "%-SNAPSHOT"]}))
                        (order :created :desc)
                        (limit 1)))))
  ([groupname jarname version]
     (first (select jars
                    (where (and {:group_name groupname
                                 :jar_name jarname
                                 :version version}))
                    (order :created :desc)
                    (limit 1)))))

(defn user-for-jar [jar]
  (first (select users
                 (where {:user (:user jar)}))))

(defn recent-versions [groupname jarname]
  (select jars
          (fields :version :created)
          (where {:group_name groupname
                  :jar_name jarname})
          (group :version)
          (order :created :desc)))

(defn jars-for-user [username]
  (select jars
          (where {:user username})
          (group :group_name :jar_name)))

;; users & groups

(defn find-user [username]
  (first (select users
                 (where {:user username}))))

(defn groups-for-user [username]
  (map :name (select groups
                     (fields :name)
                     (where {:user username}))))
