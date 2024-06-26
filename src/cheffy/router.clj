(ns cheffy.router
  (:require [reitit.ring :as ring]
            [cheffy.recipe.routes :as recipe]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]))

(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc  true
     :swagger {:basePath "/"
               :info     {:title       "Cheffy API Reference"
                          :description "Cheffy API is organized around REST..."
                          :version     "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  ;; muuntaja (content negotiation lib)
  {:data {:coercion coercion-spec/coercion
          :muuntaja   m/instance
          :middleware [swagger/swagger-feature
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})

(defn routes
  [env]
  (ring/ring-handler
    ;; Creates a ring-handler out of a router
    (ring/router
      [swagger-docs
       ["/v1"
        (recipe/routes env)]]
      router-config)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))
