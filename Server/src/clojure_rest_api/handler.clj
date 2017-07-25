(ns clojure-rest-api.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            
            [clojure-rest-api.usermodel :as db-user]))


(defroutes app-routes 
  (context "/User" [] (defroutes User-routes
    (GET "/" [] 
         (response (db-user/get-user)))
    (GET "/:UserName" [UserName] 
         (response (db-user/get-user :User UserName) ))

    ; this must check for the existance before insertion
    (POST "/" {body :body} 
          (db-user/insert-user body )
          (db-user/get-user :UserName (get body "UserName")))
    (PUT  "/" {body :body header :headers}
         (let [user (get header "user")] 
            (db-user/update-user user body)
            (response  (db-user/get-user :UserName user))))
    (DELETE "/" {header :headers}
            (db-user/delete-user (get header "username"))
            (db-user/get-user ))))
                       
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
