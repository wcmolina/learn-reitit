(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type  :json
        :cookie-policy :standard
        :body          (m/encode "application/json" {:client_id  "AcQH5M8N7t6OG87SrB2CDmYRGaBvV75V"
                                                     :audience   "https://dev-bb4bbu5o.us.auth0.com/api/v2/"
                                                     :grant_type "password"
                                                     :username   "testing@cheffy.app"
                                                     :password   "password_12345678"
                                                     :scope      "openid profile email"})}
       (http/post "https://dev-bb4bbu5o.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(comment
  (get-test-token))