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

(defn load-skills [dbconn]
  (let [skills (map (fn [x] {:Skill/name x}) skillList)
        txresult @(d/transact dbconn (data-with-dbid skills))]
    txresult))

(defn read-skills [dbval & skills]
  (map (partial skill/read-by-name dbval) skills))


(defn load-fixtures [dbconn]

  (let [txresult (load-skills dbconn)
        dbval (:db-after txresult)]
    )




  (def users
    (let [clojure (skill/read-by-name dbval "Clojure")
          scala   (skill/read-by-name dbval "Scala")
          datomic (skill/read-by-name dbval "Datomic")
          haskell (skill/read-by-name dbval "Haskell")
          groovy  (skill/read-by-name dbval "Groovy")]
      [{:User/username "dustin" :User/skills (map eid [clojure scala datomic])}
       {:User/username "jason"  :User/skills (map eid [scala haskell groovy])}]))

  (def txresult @(d/transact dbconn (data-with-dbid users)))

  )

;; (def dbconn @artscentre.db/conn)
;; (def dbval (db dbconn))
;; (def dbval (:db-before txresult))
;; (def dbval (:db-after  txresult))
