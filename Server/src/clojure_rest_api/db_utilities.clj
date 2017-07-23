(ns clojure-rest-api.db-utilities 
  		(require	[clojure.java.jdbc :as jdbc]
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