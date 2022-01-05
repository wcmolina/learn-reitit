(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [recipes (recipe-db/find-all-recipes db)]
      ;; rr = ring response, returns a skeletal Ring response with the given body, status of 200, and no headers
      (rr/response recipes))))