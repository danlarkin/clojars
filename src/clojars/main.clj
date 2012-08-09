(ns clojars.main
  (:require [clojars.config :refer [config]]
            [clojars.pages]
            [laeggen.core :as laeggen]
            [laeggen.dispatch :refer [urls!]]))

(urls!
 #"^/$" #'clojars.pages/index
 #"^/users/([^/]+)/$" #'clojars.pages/user
 #"^/([^/]+)/$" #'clojars.pages/package
 #"^/([^/]+)/([^/]+)/$" #'clojars.pages/package)

(defn main []
  (laeggen/start {:port (config :port)
                  :prefix "clojars.pages"
                  :append-slash? true}))
