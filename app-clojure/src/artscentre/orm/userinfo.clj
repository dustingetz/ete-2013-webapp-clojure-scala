(ns artscentre.orm.userinfo
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]))


(def schema (build-schema :UserInfo [[:username   :string :unique]
                                     [:firstName  :string]
                                     [:lastName   :string]
                                     [:email      :string :unique]
                                     [:created    :instant]]))


(defn read-by-name [dbval username]
  (->> (qe '[:find ?e :in $ ?username
              :where [?e :User/username ?username]]
            dbval username)
       (d/touch)))
