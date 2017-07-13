(ns clojure-rest-api.dbmanager
      (:require [clojure.java.jdbc :as jdbc]
                [clojure.data.json :as json]
                [clj-time.local :as l]))

; hasher function for Unique names
(defn Unique-hash-this! 
  "preforms a simple hashing of the argument + time"
  [this]
  (reduce * (map hash [this l/local-now])))

; DB OPTIONS from json

(defn readinconfig 
  "Reads in json file of configurations
   For database."
  [filestr] 
  (json/read-str 
    (slurp filestr) :key-fn keyword))

(defn Create-dbfrom-json
  "takes a json created the db connection"
	[jsonfile]
 
 	(let [json (readinconfig jsonfile) ]
		
    {:classname (:classname json) ; must be in classpath
           :subprotocol (:subprotocol json)
           :subname (str "//" (:db-host json) ":" (:db-port json) "/" (:db-name json))
           :user (:user json)
           :password (:password json)}))

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
   [:AuthBool :bool ]])))
; deletion of user table
(defn drop-user-tabel 
  "Delete the user table"
  []
  (jdbc/db-do-commands db
  (jdbc/drop-table-ddl :USERS)))
; conversation tables should be made first then added to converations table
(defn init-conversation
  "Creates a Conversation"
  [converationID]
  (jdbc/db-do-commands db
  (jdbc/create-table-ddl
    (keyword converationID)
   [[:id :integer :primary :key :AUTO_INCREMENT]
   [:Auther "varchar(40)"]
   [:Message :text]])))

; deletion of conversations table
(defn drop-conversation 
  "Delete the user table"
  [conversationID]
  (jdbc/db-do-commands db
  (jdbc/drop-table-ddl (keyword conversationID) {:entities clojure.string/upper-case})))

; create table
(defn init-convos-table
  "Create a table to store Conversations"
  []
  (jdbc/db-do-commands db
  (jdbc/create-table-ddl
   :CONVERSATIONS
   [[:id :integer :primary :key :AUTO_INCREMENT]
   [:Owner "varchar(40)"]
   [:Members :text]
   [:Auth 	  "varchar(255)"]
   [:ConversationID "varchar(255)" :unique]])))

; deletion of table
(defn drop-convos-table 
  "Delete the user table"
  []
  (jdbc/db-do-commands db
  (jdbc/drop-table-ddl :CONVERSATIONS {:entities clojure.string/upper-case})))

(defn create-database
  "this inits all the tables for a database"
  []
  (init-user-table)
  (init-convos-table))

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

; conversation CRUD
; creation of row
(defn insert-conversation-message
  "inserts a message into a conversation"
  [conversationID Message-map]  
  (jdbc/insert! db conversationID Message-map))
; read will need a lot of work
(defn get-conversation-message
  "gets messages from table"
  ([conversationID]
  (jdbc/query db [(str "SELECT * FROM " (name conversationID) )]))
  ([conversationID from]
  (jdbc/query db [(str "SELECT * FROM " (name conversationID) " WHERE id >= ?") from]))
  ([conversationID from to]
  (jdbc/query db [(str "SELECT * FROM "	(name conversationID) " WHERE id BETWEEN " from " AND " to)])))

(defn delete-conversation-message
  "delets a message from the converation table"
  [conversationID message-id]
  jdbc/delete! conversationID ["id = ?" message-id])


; conversations CRUD
; creation of row
(defn insert-convo
  "Inserting a conversation into the table"
  [conversation-map]
  (jdbc/insert! db
      	:CONVERSATIONS
        conversation-map)
   (init-conversation (:ConversationID conversation-map)))

; read rows
(defn get-conversation 
  "gets the conversations"
  ([]
  (jdbc/query db ["SELECT * FROM CONVERSATIONS"]))
  ([column value]
   (if (= :Members column)
     (do (jdbc/query db [(str "SELECT * FROM CONVERSATIONS WHERE " (name column) " LIKE '%" value "%'") ]))
     (do (jdbc/query db [(str "SELECT * FROM CONVERSATIONS WHERE " (name column) " = ?") value])))))

; update of row
(defn update-convo
  "update a conversation"
  [ConversationID update-map]
  (jdbc/update! db 
        :CONVERSATIONS
        update-map
        ["ConversationID = ?" ConversationID]))

; delete row
(defn delete-convo
  [convo]
  (drop-conversation convo)
  (jdbc/delete! db 
        :CONVERSATIONS
        ["ConversationID = ?" convo]))


(defn insert-alot-of-messages
	"inserts a lot of the same message"
	[conversation X]
 	  (loop [x X]
      (when (> x 1)
	  (insert-conversation-message conversation {:Auther "me" :Message "this is a message cool huh"})     
      (recur (- x 1)))))
    
(defn -main 
  [ ] 
  (try
    
      (println (get-conversation :Owner "him" ))

    	(catch Exception e (prn (str "caught exception: " (.getMessage e))))))








