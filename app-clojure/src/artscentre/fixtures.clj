(ns artscentre.fixtures
    (:use [datomic.api :only [db q] :as d]
          [artscentre.datomic-util]
          [artscentre.orm.skill :as skill]))


;;(defn dump-eid [eids] null)
;;(map #(d/entity dbval (first %)) *1)
;;(map #(d/touch %) *1)

(def skillList ["Web Designer"
                "Objective C"
                "Android"
                "Scala"
                "Clojure"
                "Datomic"
                "Java"
                "Groovy"
                "Haskell"])


(defn load-fixtures [dbconn]

  ;; (def dbconn @artscentre.db/conn)
  ;; (def dbval (db dbconn))

  (def skills (map (fn [x] {:Skill/skillname x}) skillList))
)
  (def txresult @(d/transact dbconn (data-with-dbid skills)))

  (def dbval (:db-before txresult))
  (def dbval (:db-after  txresult))

  (def users
    (let [clojure (skill/read-by-name dbval "Clojure")
          scala   (skill/read-by-name dbval "Scala")
          datomic (skill/read-by-name dbval "Datomic")
          haskell (skill/read-by-name dbval "Haskell")
          groovy  (skill/read-by-name dbval "Groovy")]
      [{:User/username "dustin" :User/skills [clojure scala datomic]}
       {:User/username "jason"  :User/skills [scala haskell groovy]}]))

  (def txresult @(d/transact dbconn (data-with-dbid users)))

  )
