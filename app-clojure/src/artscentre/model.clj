(ns artscentre.model
  (:use [datomic.api :only [db q] :as d]
        [artscentre.db :only [conn]]))


(defn redirect-get [key]
  (let [results (q '[:find ?url
                     :in $ ?key
                     :where [?r :redirect/key ?key]
                     [?r :redirect/uri ?url]]
                   (db @conn) key)]
    (first results)))

(defn redirect-put [key uri]
  (assert @conn "no db connection")
  (let [new-redirect {:db/id (d/tempid :db.part/user)
                      :redirect/key key
                      :redirect/uri uri}]
    @(d/transact @conn [new-redirect])))
