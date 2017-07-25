(ns clojure-rest-api.usermodel	  
  (:require [clojure-rest-api.db-utilities :refer :all :exclude [-main]]
    		[clojure.java.jdbc :as jdbc]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [clj-time.format :as ctf]
            [clj-time.jdbc]
            [clj-time.coerce :as c]))


(def db (Create-dbfrom-json "resources/configuration.json"))

; creation of user table
(defn init-user-table
  "Create a table to store USERS"
  []
  (jdbc/db-do-commands db 
  (jdbc/create-table-ddl
   :USERS
   [[:id :integer :primary :key :AUTO_INCREMENT]
   [:UserName "varchar(40)" :unique]
   [:PassWord "varchar(40)"]
   [:Auth 	  "varchar(255)"]
   [:LastUpdate :bigint]
   [:AuthBool :bool ]])))
; deletion of user table
(defn drop-user-table 
  "Delete the user table"
  []
  (jdbc/db-do-commands db
  (jdbc/drop-table-ddl :USERS)))


; USERS CRUD

; read users
(defn get-user
  "different gets for user"
  ([]
   (jdbc/query db ["SELECT * FROM USERS"]))
  ([column value]   
  (jdbc/query db [(str "SELECT * FROM USERS WHERE " (name column) " = ?") value])

  ))

(defn exists-user 
  [UserName]
  (if(not( empty? (get-user :UserName UserName)))
    true
    false))

; creation of user
(defn insert-user
  "inserts a user into the user table"
  [User-map]
  (if (not(exists-user (:UserName User-map)))
  (do
  	(let [insert-this { :UserName (:UserName User-map)
                      	:PassWord (passhash 3 (:PassWord User-map) (rand-int 100000000))
                       	:Auth (hash-this! (:UserName User-map))
                        :LastUpdate (rand-int 1000000000)
                        :AuthBool true
                      }]
  
  (jdbc/insert! db :USERS insert-this)))
  (do false)))

; update of user
(defn update-user
  "update user where something equals something"
  [UserName update-map]
  (if (not (exists-user (:update-map)))
   (do
    (jdbc/update! db :USERS update-map ["UserName = ?" UserName]))
   false))

; delete user
(defn delete-user
  "delete user from table"
  [UserName]
  (jdbc/delete! db 
        :USERS
        ["UserName = ?" UserName]))

(defn -main [ ]

  (delete-user "mee")
  (if  (insert-user {:UserName "mee" :PassWord "nopassword" })
    (do (println "inserted"))
    (do (println "not inserted")) )
  (println (exists-user "mee"))
  
  )



