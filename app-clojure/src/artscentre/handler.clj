(ns artscentre.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.stacktrace :as stacktrace]
            ;;[ring.middleware.reload :as reload]
            [artscentre.views :as views]))


(defroutes app-routes
  (GET   "/"       []                (views/index))
  (POST  "/form"   {form :params}    (do (prn form) (views/formSubmit form)))
  (GET   "/form"   []                (views/form))
  (GET   "/:key"   [key]             (views/redirect key))

  (route/resources "/")
  (route/not-found "Not Found"))

(def ring-app (-> app-routes
                  ;;#(reload/wrap-reload % ['tinyurl-datomic.handler])
                  stacktrace/wrap-stacktrace
                  handler/site))
