(ns clojure-rest-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
 			[dbmanager :as dbman])

(defroutes app-routes
  (GET "/User/:User" [User] (dbman/get-user :UserName User))
  (GET "/conversations" [] "ayo boiiii")
  
    (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
