(ns clojure-rest-api.db-utilities 
  		(require	[clojure.java.jdbc :as jdbc]
            [clojure.data.json :as json]
            [clj-time.local :as l]))


; hasher function for Unique names
(defn hash-this! 
  "preforms a simple hashing of the argument + time"
  [this]
  (reduce * (map hash this)))

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

(defn passhash
  "this will hash a lot"
  [hashings password salt]
  (loop [n hashings, hashthis password, with salt]
    (if (zero? n) 
      hashthis
      (recur (dec n) (* (hash hashthis) (hash with)) salt))))

; (loop [n 3, h "hello", k "kkk"] 
;   #_=> (if (zero? n) 
;   #_=> h
;   #_=> (recur (dec n) (* (hash h) (hash k)) k)))

(defn -main 
  []
   (def hash1 (passhash 3 "this word" "salt"))
   (def hash2 (passhash 3 "this word" "salt"))
   (println hash1, "hash1   ", hash2, "hash2   " (= hash1 hash2) ))








