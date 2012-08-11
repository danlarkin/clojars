(ns clojars.main
  (:require [clojars.config :refer [config]]
            [clojars.pages]
            [laeggen.auth :as auth]
            [laeggen.core :as laeggen]
            [laeggen.dispatch :refer [urls!]]))

(urls!
 #"^/$" #'clojars.pages/index
 #"^/users/([^/]+)/$" #'clojars.pages/user
 #"^/login/$" #'clojars.pages/login
 #"^/logout/$" #'clojars.pages/logout
 #"^/profile/$" (auth/authorization-required "/" #'clojars.pages/profile)
 #"^/([^/]+)/$" #'clojars.pages/package
 #"^/([^/]+)/([^/]+)/$" #'clojars.pages/package)

(defn main []
  (laeggen/start {:port (config :port)
                  :append-slash? true}))
