(ns artscentre.orm.skill
  (:use [datomic.api :only [db q] :as d]
        [artscentre.datomic-util]))


(defn read-all [dbval]
  (->> (qes '[:find ?e :where [?e :Skill/name]] dbval)
       (mapv d/touch)))


(defn read-by-name [dbval skillname]
  (->> (qe '[:find ?skill :in $ ?skillname
              :where [?skill :Skill/name ?skillname]]
            dbval skillname)
       (d/touch)))


(defn read-by-user [dbval username]
  (->> (qes '[:find ?skill :in $ ?username
              :where [?user :User/username ?username]
                     [?skill :User/skills]]
            dbval username)
       (mapv d/touch)))



;; (read-all (db @artscentre.db/conn))
;; (read-by-name (db @artscentre.db/conn) "Web Designer")
;; (read-for-user (db @artscentre.db/conn) "dustin")
