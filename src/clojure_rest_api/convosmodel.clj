(ns clojure-rest-api.convosmodel
 (require  [clojure.java.jdbc :as jdbc]
                [clojure.data.json :as json]
                [clj-time.local :as l]
                [clojure-rest-api.db-utilities :refer [Create-dbfrom-json]]))

(def db (Create-dbfrom-json "resources/configuration.json"))

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
  (jdbc/query db [(str "SELECT * FROM " (name conversationID) " WHERE id BETWEEN " from " AND " to)])))

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
