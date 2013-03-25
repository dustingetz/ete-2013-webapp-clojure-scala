(ns artscentre.core
  (:use [datomic.api :only [db q] :as d])
  (:require [ring.adapter.jetty :as jetty]
            [artscentre.handler :as handler]
            [artscentre.db :as appdb]
            [artscentre.schema :as schema]
            [artscentre.fixtures :as fixtures]))


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

  )

;; (-main)
;; (start-dev-db)
;; (fixtures/load-fixtures @appdb/conn)
;; (.stop server)
;; (.start server)
