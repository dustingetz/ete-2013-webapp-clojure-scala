(ns artscentre.orm.user
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]))


(def schema (build-schema :User [[:username  :string :unique]
                                 [:skills    :ref :many]]))


(defn read-by-name [dbval username]
  (->> (qe '[:find ?e :in $ ?username
             :where [?e :User/username ?username]]
         dbval username)
    (d/touch)))
