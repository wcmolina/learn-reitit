(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc :as jdbc]
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

(def router
  (reitit.core/router
    ["/v1/recipes/:recipe-id"
     {:coercion reitit.coercion.spec/coercion
      :parameters {:path {:recipe-id string?}}}]
    ;; Compile  at router creation time, not on the request time
    {:compile reitit.coercion/compile-request-coercers}))

;; Coercion: process of transforming params and responses
(comment
  (reitit.coercion/coerce!
    (reitit.core/match-by-path router "/v1/recipes/1234-recipe"))

  (app {:request-method :get
        :uri            "/v1/recipes/1234"})

  (require '[clojure.pprint :refer [pprint]])
  (require '[reitit.core])
  (require '[reitit.coercion])
  (require '[reitit.coercion.spec])

  (pprint (macroexpand '(ns coercion (:require [clojure.pprint :refer [pprint]]))))

  (jdbc/execute! db ["SELECT * FROM recipe WHERE public = true;"])
  (time
    (with-open [conn (jdbc/get-connection db)]
      {:public (sql/find-by-keys conn :recipe {:public true})
       :drafts (sql/find-by-keys conn :recipe {:public false :uid "auth0|5ef440986e8fbb001355fd9c"})}))
  (with-open [conn (jdbc/get-connection db)]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
          [recipe] (sql/find-by-keys conn :recipe {:recipe_id recipe-id})
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (when (seq recipe)
        (assoc recipe
          :recipe/steps steps
          :recipe/ingredients ingredients))))
  (go)
  (halt)
  (reset))