(ns artscentre.orm.user
  (:use [datomic.api :only [db q] :as d])
  (:use [artscentre.datomic-util]))


(defn read-by-name [dbval username]
  (->> (qe '[:find ?e :in $ ?username
              :where [?e :User/username ?username]]
            dbval username)
       (d/touch)))
