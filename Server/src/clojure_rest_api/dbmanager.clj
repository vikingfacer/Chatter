(ns clojure-rest-api.dbmanager
      (:require [clojure.java.jdbc :as jdbc]
                [clojure.data.json :as json]
                [clj-time.local :as l]
                [clojure-rest-api.usermodel :as Usermodel]
                [clojure-rest-api.convosmodel :as convosmodel]))




(defn create-database
  "this inits all the tables for a database"
  []
  (Usermodel/init-user-table)
  (convosmodel/init-convos-table))

(defn insert-alot-of-messages
	"inserts a lot of the same message"
	[conversation X]
 	  (loop [x X]
      (when (> x 1)
	  (convosmodel/insert-conversation-message conversation {:Auther "me" :Message "this is a message cool huh"})     
      (recur (- x 1)))))
    
(defn -main 
  [ ] 
  (try
    
      (println "creating database")
      (create-database)

    	(catch Exception e (prn (str "caught exception: " (.getMessage e))))))








