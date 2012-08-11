(ns clojars.pages
  (:require [clabango.parser :refer [render-file]]
            [clojars.db :as db]
            [laeggen.auth :as auth])
  (:import (org.mindrot.jbcrypt BCrypt)))

(defn package
  ([request package-name] (package request package-name package-name))
  ([request group package-name]
     (when-let [jar (db/find-jar group package-name)]
       (let [jar (assoc jar
                   :name (if (= (:jar_name jar) (:group_name jar))
                           (:jar_name jar)
                           (str (:group_name jar) "/" (:jar_name jar)))
                   :user (db/user-for-jar jar))]
         (render-file "clojars/templates/package.html"
                      {:jar jar
                       :versions (db/recent-versions group package-name)})))))

(defn user [request username]
  (when-let [user (db/find-user username)]
    (let [groups (db/groups-for-user username)
          jars (db/jars-for-user username)]
      (render-file "clojars/templates/user.html"
                   {:user user
                    :groups groups
                    :jars jars}))))

(defn index [request]
  "homepage")

(defn login [request]
  (if (auth/authorized? request)
    {:status 302
     :headers {"location" "/"}}
    (if (= :post (:request-method request))
      (let [form-data (:body request)
            user (db/find-user (:username form-data))]
        (if (and user (BCrypt/checkpw (:password form-data) (:password user)))
          (auth/authorize-and-forward! (:user user) "/profile/")
          (render-file "clojars/templates/login.html" {:failed? true})))
      (render-file "clojars/templates/login.html" {}))))

(defn logout [request]
  (auth/deauthorize-and-forward! request "/"))

(defn profile [request]
  (let [user (db/find-user (:laeggen-id-value request))]
    (str "profile for " (:user user))))
