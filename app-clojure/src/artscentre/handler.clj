(ns artscentre.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.stacktrace :as stacktrace]
            ;;[ring.middleware.reload :as reload]
            [artscentre.views :as views]))


(defroutes app-routes
  (GET   "/api/whoami"                   [] (views/index))
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

(def ring-app (-> app-routes
                  stacktrace/wrap-stacktrace
                  handler/site))
