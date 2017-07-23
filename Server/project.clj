(defproject clojure-rest-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 
                 [clj-time "0.13.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.7.0-alpha3"]
                 [mysql/mysql-connector-java "5.1.42"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clojure-rest-api.handler/app
         :auto-reload? true
         :auto-refresh? false}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}}
    :main clojure-rest-api.dbmanager/-main)
