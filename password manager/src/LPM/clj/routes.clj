(ns LPM.clj.routes
  (:require [compojure.core :refer [defroutes POST]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.data.json :as cjson]
            [LPM.clj.user :as usr]
            [clojure.tools.logging :as log]))

(defroutes app-routes
  (POST "/create-account" {:keys [body]}
    (let [profile-name (:userProfileName body)
          login-password (:userLoginPassword body)]
      (log/info "L->Handling account creation..." body)
      (usr/on-create-account profile-name login-password)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (cjson/write-str {:message "User created successfully"})}))
  (POST "/add-a-new-password" {:keys [body]}
    (let [profile-name (:userProfileName body)
          login-password (:userLoginPassword body)
          passwords (:passwords body)]
      (log/info "L->Handling password adding" body)
      (usr/add-new-password profile-name login-password passwords)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (cjson/write-str {:message "L-> Password added successfully"})})))

(def handler
  (-> app-routes
      (wrap-cors :access-control-allow-origin  #".*"
                 :access-control-allow-methods [:get :post :options])
      (wrap-json-body)
      (wrap-json-response)))

(defn -main [& args]
  (run-jetty handler {:port 3000 :join? false}))