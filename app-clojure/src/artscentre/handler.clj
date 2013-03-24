(ns artscentre.handler
  (:use [compojure.core]
        [ring.middleware.json])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.stacktrace :as stacktrace]
            [ring.util.response :as r]
            ;;[ring.middleware.reload :as reload]
            [artscentre.api :as api]
            [artscentre.views :as views]))


(defroutes app-routes
  (GET   "/api/whoami"                   [] (r/response (api/whoami)))
  (GET   "/api/list-skills-user-picker"  [] (views/index))
  (GET   "/api/update-user-skills"       [] (views/index))
  (GET   "/api/project-add-member"       [] (views/index))
  (GET   "/api/project-remove-member"    [] (views/index))
  (GET   "/api/delete-project"           [] (views/index))
  (GET   "/api/list-owned-projects"      [] (views/index))
  (GET   "/api/list-joined-projects"     [] (views/index))
  (GET   "/api/list-eligible-projects"   [] (views/index))


  (GET   "/login"   [] (views/form))
  (POST  "/login"   {form :params}    (do (prn form) (views/formSubmit form)))
;;  (GET   "/:key"   [key] (views/redirect key))

  (route/files "/" {:root "../webapp"})
  (route/not-found "Not Found"))

(def ring-app (-> (handler/api app-routes)
                  stacktrace/wrap-stacktrace
                  wrap-json-body
                  wrap-json-response
                  wrap-json-params))
