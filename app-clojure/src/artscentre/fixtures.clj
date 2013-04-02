(ns artscentre.fixtures
    (:use [datomic.api :only [db q] :as d]
          [platform.datomic-util])
    (:require [artscentre.orm.skill :as skill]
              [artscentre.orm.projectinfo :as projectinfo]))


(def skills     [{:Skill/name        "Clojure"
                  :Skill/description "omg parens"
                  :db/id             #db/id[:db.part/user -1000001]}

                 {:Skill/name        "Datomic"
                  :Skill/description "i got 99 proiblems but state ain't one"
                  :db/id             #db/id[:db.part/user -1000002]}

                 {:Skill/name        "Scala"
                  :Skill/description "what is this OO malarky (just kidding)"
                  :db/id             #db/id[:db.part/user -1000003]}

                 {:Skill/name        "Java"
                  :Skill/description "every empire eventually falls"
                  :db/id             #db/id[:db.part/user -1000004]}

                 {:Skill/name        "Postgresql"
                  :Skill/description "sometimes it's cool to be old"
                  :db/id             #db/id[:db.part/user -1000005]}])

(def clojure    #db/id[:db.part/user -1000001])
(def datomic    #db/id[:db.part/user -1000002])
(def scala      #db/id[:db.part/user -1000003])
(def java       #db/id[:db.part/user -1000004])
(def postgresql #db/id[:db.part/user -1000005])

(def users      [{:User/username  "dustin"
                  :User/skills    [clojure datomic]
                  :db/id          #db/id[:db.part/user -1000008]}

                 {:User/username  "jason"
                  :User/skills    [scala java postgresql datomic]
                  :db/id          #db/id[:db.part/user -1000009]}])


(def jason    #db/id[:db.part/user -1000008])
(def dustin   #db/id[:db.part/user -1000009])



(def projects   [{:ProjectInfo/name     "Artscentre"
                  :ProjectInfo/owner    jason
                  :ProjectInfo/members  [jason]
                  :ProjectInfo/created  (java.util.Date.)
                  :ProjectInfo/skills   [clojure datomic]
                  :db/id                #db/id[:db.part/user -1000006]}

                 {:ProjectInfo/name     "Tinyurl"
                  :ProjectInfo/owner    dustin
                  :ProjectInfo/members  [dustin]
                  :ProjectInfo/created  (java.util.Date.)
                  :ProjectInfo/skills   [java postgresql]
                  :db/id                #db/id[:db.part/user -1000007]}])





(defn load-fixtures [dbconn]
  @(d/transact dbconn (concat skills users projects)))




;; (defn load-skills
;;   "transacts some skills, and returns the transacted entities
;;    as a map of name->eid for use in future transactions"
;;   [dbconn]
;;   (let [txresult    @(d/transact dbconn (data-with-dbid skill-list))
;;         dbval       (:db-after txresult)
;;         skills      (->> (map (partial skill/read-by-name dbval) ["Clojure" "Scala" "Datomic" "Java" "Postgresql"])
;;                          (map (fn [entity]
;;                                 (let [eid  (:db/id entity)
;;                                       name (:Skill/name entity)]
;;                                   [name entity])))
;;                          (into {}))]
;;     skills))
