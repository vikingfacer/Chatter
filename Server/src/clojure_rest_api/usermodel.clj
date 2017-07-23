(ns clojure-rest-api.usermodel	  
  (:require [clojure-rest-api.db-utilities :refer [Create-dbfrom-json]]
    		[clojure.java.jdbc :as jdbc]
            [clojure.data.json :as json]
            [clj-time.local :as l]))


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
   [:LastUpdate "varchar(40)"]
   [:AuthBool :bool ]])))
; deletion of user table
(defn drop-user-tabel 
  "Delete the user table"
  []
  (jdbc/db-do-commands db
  (jdbc/drop-table-ddl :USERS)))


; USERS CRUD
; creation of user
(defn insert-user
  "inserts a user into the user table"
  [User-map]
  (jdbc/insert! db
        :USERS
        (conj User-map {:AuthBool false})))

; read users
(defn get-user
  "different gets for user"
  ([]
   (jdbc/query db ["SELECT * FROM USERS"]))
  ([column value]   
  (jdbc/query db [(str "SELECT * FROM USERS WHERE " (name column) " = ?") value])

  ))

; update of user
(defn update-user
  "update user where something equals something"
  [UserName update-map]
  (jdbc/update! db
        :USERS
        update-map
        ["UserName = ?" UserName]))

; delete user
(defn delete-user
  "delete user from table"
  [UserName]
  (jdbc/delete! db 
        :USERS
        ["UserName = ?" UserName]))

(defn -main [ ]
  (println "this is Usermodel"))



