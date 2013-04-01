(ns artscentre.core
  (:use [datomic.api :only [db q] :as d])
  (:require [ring.adapter.jetty :as jetty]
            [artscentre.handler :as handler]
            [artscentre.db :as appdb]
            [artscentre.orm.projectinfo :as projectinfo]
            [artscentre.orm.skill :as skill]
            [artscentre.fixtures :as fixtures]))


(defn -main [& m]
  (def server (jetty/run-jetty handler/ring-app {:port 3001 :join? false}))
  (start-dev-db)
  (load-fixtures (@appdb/conn))
  )

(defn start-dev-db []
  (reset! appdb/conn (let [uri "datomic:mem://artscentre"]
                  (d/delete-database uri)
                  (d/create-database uri)
                  (d/connect uri)))

  (let [schema-tx (concat skill/schema
                          projectinfo/schema)]
    @(d/transact @appdb/conn schema-tx))

  )




(comment
  ;; Some common REPL operations

  (-main)

  (start-dev-db)
  (fixtures/load-fixtures @appdb/conn)

  (.stop server)
  (.start server)

  (->> (projectinfo/read-all (db @appdb/conn))
       (first)
       (:ProjectInfo/skills)
       (mapv d/touch))
  )
