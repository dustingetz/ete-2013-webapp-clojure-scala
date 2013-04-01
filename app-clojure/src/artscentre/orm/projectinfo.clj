(ns artscentre.orm.projectinfo
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]))


(def schema (build-schema :ProjectInfo [[:name     :string :unique]
                                        [:owner    :string]
                                        [:created  :instant]
                                        [:skills   :ref :many]]))


(defn read-all [dbval]
  (->> (qes '[:find ?e
              :where [?e :ProjectInfo/name]] dbval)
       (mapv d/touch)))
