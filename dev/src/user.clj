(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc.sql :as sql]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
;; reset will act on changed files
;; reset-all will reload everything regardless of changed/unchanged files
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

;; Coercion: process of transforming params and responses
(comment
  (sql/update! db :recipe {:name "Wil"} {:recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"})
  (sql/delete! db :recipe {:recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"})

  (set! *print-namespace-maps* false)

  (-> (app {:request-method :get
            :uri            "/v1/recipes/1234-recipe"})
      :body
      (slurp))
  (-> (app {:request-method :post
            :uri            "/v1/recipes"
            :body-params {:name "My recipe"
                          :prep-time 49
                          :img "image-url"}})
      :body
      (slurp))
  (go)
  (halt)
  (reset))