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
    (let [current-time (System/currentTimeMillis)]
  	(let [insert-this { :UserName (:UserName User-map)
                      	:PassWord (passhash 3 (:PassWord User-map) current-time)
                       	:Auth (passhash 3 (:UserName User-map) current-time)
                        :LastUpdate current-time
                        :AuthBool true
                      }]
  
  (jdbc/insert! db :USERS insert-this))))
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

(defn get-from-sequ
  "this is a simple wrapper to extract an item from the results of the user query"
  [Key Results]
  (first (map #(% Key) Results)))

(defn get-all-users-names
  "for getting all users names"
  []
  (map :username (apply vector (get-user))))

(defn get-specific-user
  [UserName]
  (get-from-sequ :username (get-user :UserName UserName)))

(defn check-user-pass 
  "helper function for checking passwords"
  [dbUserMap UserMap]
  (= (get-from-sequ :password dbUserMap) 
     (str (passhash 3 (:PassWord UserMap) (get-from-sequ :lastupdate dbUserMap)))))

(defn get-user-auth
  "checks for existance password and returns auth"
  [UserMap]
  (let [dbuser (get-user :UserName (:UserName UserMap))]
  (if (not(empty? dbuser))
        (if (check-user-pass dbuser UserMap)
          (get-from-sequ :auth dbuser)
           {:status 202})
           {:status 404})))


(defn -main [ ]

  ; (delete-user "mee")
  ; (if  (insert-user {:UserName "mee" :PassWord "nopassword" })
  ;   (do (println "\ninserted\n"))
  ;   (do (println "\nnot inserted\n")) )
  (let [testUser {:UserName "mee" :PassWord "nopassword" }
    	dbuser (get-user :UserName "mee")]
    ; (println  (check-user-pass dbuser testUser))
    (println (get-user-auth testUser)))
   	(println (get-specific-user "mee"))
  
  ; (println (exists-user "mee"))
  ; (println (get-all-users-names))
 
  )



