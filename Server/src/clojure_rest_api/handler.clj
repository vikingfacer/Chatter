(ns clojure-rest-api.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [clojure-rest-api.dbmanager :as dbman]))


(defroutes app-routes 
  (context "/User" [] (defroutes User-routes
    (GET "/" [] (response (dbman/get-user)))
    (GET "/:UserName" [UserName] (response (dbman/get-user :User UserName) ))
    (POST "/" {body :body}  (dbman/insert-user body ))
    (PUT  "/" {body :body header :header-params} (response  {:body body :headers header}) ))) ;(dbman/update-user ((:User body ) (hash-map (:Update body)) )))
                       
                       
  (GET "/" [] "HELOO BABY BOY")
  (route/not-found "Not Found try again"))

(defn wrap-spy [handler]
  (fn [request]
    (println "-------------------------------")
    (println "Incoming Request:")
    (clojure.pprint/pprint request)
    (let [response (handler request)]
      (println "Outgoing Response Map:")
      (clojure.pprint/pprint response)
      (println "-------------------------------")
      response)))

(def app
     (->(handler/api app-routes )
        (middleware/wrap-json-body)
        (middleware/wrap-json-response) 
        (wrap-spy) ))
