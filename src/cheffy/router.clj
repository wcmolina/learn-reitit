(ns cheffy.router
  (:require [reitit.ring :as ring]
            [cheffy.recipe.routes :as recipe]))

(defn routes
  [env]
  (ring/ring-handler
    ;; Creates a ring-handler out of a router
    (ring/router
      [["/v1"
        (recipe/routes env)]])))