(ns cheffy.server
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [cheffy.router :as router]
            [next.jdbc :as jdbc]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty
  [_ config]
  ;; Overwrite port from config.edn (use Heroku's)
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/prep-key :db/postgres
  [_ config]
  ;; Overwrite port from config.edn (use Heroku's)
  (merge config {:jdbc-url (env :jdbc-database-url)}))

(defmethod ig/init-key :server/jetty
  ;; Unused arg === _
  ;; keys is used to destructure map
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :cheffy/app
  [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgres
  [_ {:keys [jdbc-url]}]
  (println "\nConfigured db")
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  ;; slurp is used to read a file basically
  ;; ig/read-string will parse those # literals defined in the config.edn file
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))

(comment
  (app {:request-method :get
        :uri            "/"})
  (-main))