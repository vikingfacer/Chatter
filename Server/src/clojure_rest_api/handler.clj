(ns clojure-rest-api.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [clojure-rest-api.dbmanager :as dbman]))


(defroutes app-routes 
  (context "/User" [] (defroutes User-routes
    (GET "/" [] 
         (response (dbman/get-user)))
    (GET "/:UserName" [UserName] 
         (response (dbman/get-user :User UserName) ))

    ; this must check for the existance before insertion
    (POST "/" {body :body} 
          (dbman/insert-user body )
          (dbman/get-user :UserName (get body "UserName")))
    (PUT  "/" {body :body header :headers}
         (let [user (get header "user")] 
            (dbman/update-user user body)
            (response  (dbman/get-user :UserName user))))
    (DELETE "/" {header :headers}
            (dbman/delete-user (get header "username"))
            (dbman/get-user ))))
                       
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
