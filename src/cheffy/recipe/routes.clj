(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]))

(defn routes
  [env] ;; jdbc-url "..."
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}
                 :get {:handler (recipe/list-all-recipes db)
                       :summary "List all recipes"}}]))