(ns artscentre.handler
  (:use [compojure.core]
        [ring.middleware.json]
        [artscentre.datomic-ring :only [*dbval* *dbconn* wrap-datomic]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [datomic.api :only [db q] :as d]
            [ring.middleware.stacktrace :as stacktrace]
            [ring.util.response :as r]
            ;;[ring.middleware.reload :as reload]
            [artscentre.api :as api]
            [artscentre.db]))


(defroutes app-routes
  (GET   "/api/whoami"                   [] (r/response (api/whoami *dbval*)))
  (GET   "/api/list-skills-user-picker"  [] (r/response (api/listSkillsUserPicker *dbval*)))
  (GET   "/api/update-user-skills"       [] (r/response {}))
  (GET   "/api/project-add-member"       [] (r/response {}))
  (GET   "/api/project-remove-member"    [] (r/response {}))
  (GET   "/api/delete-project"           [] (r/response {}))
  (GET   "/api/list-owned-projects"      [] (r/response {}))
  (GET   "/api/list-joined-projects"     [] (r/response {}))
  (GET   "/api/list-eligible-projects"   [] (r/response {}))


  ;; (GET   "/login"   [] (views/form))
  ;; (POST  "/login"   {form :params}    (do (prn form) (views/formSubmit form)))
  ;; (GET   "/:key"   [key] (views/redirect key))

  (route/files "/" {:root "../webapp"})
  (route/not-found "Not Found"))


(def ring-app (-> (handler/api app-routes)
                  (partial wrap-datomic artscentre.db/conn)
                  stacktrace/wrap-stacktrace
                  wrap-json-body
                  wrap-json-response
                  wrap-json-params))
