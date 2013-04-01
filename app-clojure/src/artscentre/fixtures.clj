(ns artscentre.fixtures
    (:use [datomic.api :only [db q] :as d]
          [platform.datomic-util])
    (:require [artscentre.orm.skill :as skill]
              [artscentre.orm.projectinfo :as projectinfo]))


(def skill-list [{:Skill/name        "Clojure"
                  :Skill/description "omg parens"}

                 {:Skill/name        "Datomic"
                  :Skill/description "i got 99 proiblems but state ain't one"}

                 {:Skill/name        "Scala"
                  :Skill/description "what is this OO malarky (just kidding)"}

                 {:Skill/name        "Java"
                  :Skill/description "every empire eventually falls"}

                 {:Skill/name        "Postgresql"
                  :Skill/description "sometimes it's cool to be old"}])


(defn load-fixtures [dbconn]

  (let [txresult    @(d/transact dbconn (data-with-dbid skill-list))
        dbval       (:db-after txresult)
        skills      (->> (map (partial skill/read-by-name dbval) ["Clojure" "Scala" "Datomic" "Java" "Postgresql"])
                         (map (fn [entity]
                                (let [eid  (:db/id entity)
                                      name (:Skill/name entity)]
                                  [name eid])))
                         (into {}))

        artscentre  {:ProjectInfo/name    "Artscentre"
                     :ProjectInfo/owner   "Dustin"
                     :ProjectInfo/created (java.util.Date.)
                     :ProjectInfo/skills  [(skills "Clojure")
                                           (skills "Datomic")]}

        tinyurl     {:ProjectInfo/name    "Tinyurl"
                     :ProjectInfo/owner   "Kevin"
                     :ProjectInfo/created (java.util.Date.)
                     :ProjectInfo/skills  [(skills "Java")
                                           (skills "Postgresql")]}]

    @(d/transact dbconn (data-with-dbid [artscentre tinyurl])))

  )

;; (def dbconn @artscentre.db/conn)
;; (def dbval (db dbconn))
;; (def dbval (:db-before txresult))
;; (def dbval (:db-after  txresult))
