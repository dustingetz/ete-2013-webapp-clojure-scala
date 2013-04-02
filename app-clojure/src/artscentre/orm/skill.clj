(ns artscentre.orm.skill
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]))


(def schema (build-schema :Skill [[:name         :string :unique]
                                  [:description  :string]]))


(defn read-all [dbval]
  (qes '[:find ?e :where [?e :Skill/name]] dbval))


(defn read-by-name [dbval skillname]
  (qe '[:find ?skill :in $ ?skillname
        :where [?skill :Skill/name ?skillname]]
            dbval skillname))


(defn read-by-user [dbval username]
  (qes '[:find ?skills :in $ ?username
         :where [?user  :User/username ?username]
                [?user  :User/skills ?skills]]
       dbval username))
