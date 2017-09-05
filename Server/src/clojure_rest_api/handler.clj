(ns clojure-rest-api.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            
            [clojure-rest-api.usermodel :as db-user]
            [clojure-rest-api.db-utilities :as uts]))

(defn Check-password?
  [db-user requ-user]
  (= (:password db-user) 
     (str (uts/passhash 3 requ-user (:lastupdate db-user)))))

(defroutes app-routes 
  (context "/User" [] 
           
    (GET "/" []
         (response (db-user/get-all-users-names))) ;this is

    (GET "/:UserName" [UserName] 
         
         (response (db-user/get-specific-user UserName)))
           
    (POST "/" {body :body} 
          (if (not(db-user/exists-user (get body "username")))
          (do
            (db-user/insert-user {:UserName (get body "username")
                                  :PassWord (get body "password")})
            (response 
              (select-keys 
                (first(db-user/get-user :username (get body "username"))) 
                [:username :auth])))
          (str "User already exists") ))
    ; this needs to check if user is not logged in.
    (POST "/Auth" {body :body}
          (if (db-user/exists-user (get body "username"))
            (let [ dbuser (first(db-user/get-user :username (get body "username")))]             
              (if (Check-password? dbuser (get body "password"))
                (if (= (:authbool dbuser) false)
                  (do
                    (db-user/update-user (get body "username")
                                         {:AuthBool true}) 
                    (response 
                        {"username" (:username dbuser)
                         "auth" (:auth dbuser)}))
                  (str "Auth is already given out"))
                
                (str "User pass does not match"))) 
            
            (str "User does not exist")))
    
    (PUT  "/" {body :body}
         ; primarially used for changing password
          (if (db-user/exists-user (get body "username"))
            (let [user (first (db-user/get-user :username (get body "username")))] 
              (if (Check-password? user (get body "password"))
                  (do 
                    (db-user/update-user (get body "username")
                                         (let [sys-time (System/currentTimeMillis)]
                                         {:lastupdate sys-time 
                                          :password (uts/passhash 3 (get body "newpassword") sys-time)}))
                    (response{(keyword "changed") (select-keys
                                (first(db-user/get-user :username (get body "username")))
                                [:username])}))
                  (str "user pass does not match")))              
              (str "user does not exist")))
    
    (PUT "/logout"{body :body}
         (if (db-user/exists-user (get body "username"))
           (let [dbuser (first (db-user/get-user :username (get body "username")))]
             (if (= (:authbool dbuser ) true)
               (if (Check-password? dbuser (get body "password"))
                   (do (db-user/update-user (get body "username")
                                            {:AuthBool false
                                             :Auth (uts/passhash 3 (get body "username")
                                                               (System/currentTimeMillis))})
                        (response {:loggedOut 
                                  {:username (:username dbuser)}}))
                   (str "user pass does not match"))
               (str "user is logged out")
               ))))
    
    (DELETE "/" {body :body}
            
            (if  (db-user/exists-user (get body "username"))
              (let [ dbuser (first (db-user/get-user :username (get body "username")))]
                
                (if (Check-password? dbuser (get body "password"))
                  (do
                    (db-user/delete-user (get body "username"))
                    (response "deleted"))
                  (str "user pass does not match")))
              (str "user does not exist"))))
  
    (context "/Convos"[]
              
        (POST "/" {body :body}
              (response body)))
                   
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
     (->(handler/api  app-routes)
        (middleware/wrap-json-body)
        (middleware/wrap-json-response) 
        (wrap-spy)))
