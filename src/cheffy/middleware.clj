(ns cheffy.middleware
  (:require [ring.middleware.jwt :as jwt]))

;; Convention: every middleware is preceded by wrap-
(def wrap-auth0
  {:name        ::auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap        (fn [handler]
                  (jwt/wrap-jwt handler {:alg          :RS256
                                         :jwk-endpoint "https://dev-bb4bbu5o.us.auth0.com/.well-known/jwks.json"}))})