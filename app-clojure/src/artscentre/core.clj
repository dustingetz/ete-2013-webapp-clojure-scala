(ns artscentre.core
  (:use [datomic.api :only [db q] :as d])
  (:require [ring.adapter.jetty :as jetty]
            [artscentre.handler :as handler]
            [artscentre.db :as appdb]
            [artscentre.schema :as schema]))


(defn -main [& m]
  (def server (jetty/run-jetty handler/ring-app {:port 3001 :join? false})))

(defn start-dev-db []
  (reset! appdb/conn (let [uri "datomic:mem://seattle"]
                  (d/delete-database uri)
                  (d/create-database uri)
                  (d/connect uri)))

  (let [schema-tx (concat schema/User
                          schema/UserInfo
                          schema/Skill
                          schema/Project
                          schema/ProjectInfo)]
    @(d/transact @appdb/conn schema-tx))

  ;; (let [fixtures-tx (read-string (slurp "install/fixtures.dtm"))]
  ;;   @(d/transact @appdb/conn fixtures-tx))
  )

;; (-main)
;; (start-dev-db)
;; (.stop server)
;; (.start server)
