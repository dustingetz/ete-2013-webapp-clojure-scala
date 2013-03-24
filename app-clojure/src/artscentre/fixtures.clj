(ns artscentre.fixtures
    (:use [datomic.api :only [db q] :as d]
          [artscentre.db :only [conn]]))


(defn data-with-dbid
  [data]
  (map #(merge {:db/id (d/tempid :db.part/user)} %1) data))

;;(defn dump-eid [eids] null)
;;(map #(d/entity dbval (first %)) *1)
;;(map #(d/touch %) *1)


(assert @conn "no db connection")

;; default list of skills

(def skillList ["Web Designer",
                "Objective C Developer",
                "Android Developer",
                "Classical Guitar",
                "Jazz Guitar",
                "Classical Piano",
                "Jazz Piano",
                "Composer",
                "Conductor",
                "Photographer",
                "Graphic Designer",
                "Oboe",
                "Clarinet",
                "Trumpet",
                "Horn",
                "English Horn",
                "Viola",
                "Violin",
                "Cello",
                "Bass",
                "Video Editor",
                "Painter",
                "Sculptor",
                "Dancer",
                "Choreographer",
                "Producer",
                "Lighting Designer",
                "Director"])

(def skills (map (fn [x] {:Skill/name x}) skillList))
@(d/transact @conn (data-with-dbid skills))


;; some users join
(def users [{:User/username "dustin" }
            {:User/username "jason" }])
@(d/transact @conn (data-with-dbid users))



;; we can query for them
(def dbval (db @conn))
(q '[:find ?e
     :in $ ?username
     :where [?e :User/username ?username]]
   dbval
   (:User/username (users 0)))




;; first get name/eid pairs
(defn eid-by-email [dbval]
  (let [results (q '[:find ?n ?e :where [?e :user/email ?n]] dbval)]
    (into {} results)))

((eid-by-email dbval) "dustin.getz@foo.com")

;; add some aliases
(let [users (eid-by-email dbval)
      a1 {:shorturi/alias "dustingetz"
          :shorturi/uri "http://www.dustingetz.com/"
          :shorturi/owner (users "dustin.getz@foo.com")}
      a2 {:shorturi/alias "bob"
          :shorturi/uri "http://www.bobbillard.com/"
          :shorturi/owner (users "bob.billard@foo.com")}
      a3 {:shorturi/alias "bob2"
          :shorturi/uri "http://www.bobbyyyyy.com/"
          :shorturi/owner (users "bob.billard@foo.com")}]
  @(d/transact @conn (data-with-dbid [a1 a2 a3])))


;; what is bob's list of aliases - using back references
(def dbval (db @conn))
(def bob-eid ((eid-by-email dbval) "bob.billard@foo.com"))
(def bob-e (d/entity dbval bob-eid))
(d/touch bob-e)
(def bob-owned (:shorturi/_owner bob-e))
(map d/touch bob-owned)

(def users (eid-by-email dbval))

;; or query directly
(q '[:find ?alias
     :in $ ?owner
     :where [?se :shorturi/alias ?alias]
            [?se :shorturi/owner ?ue]
            [?ue :user/email ?owner]]
   dbval "bob.billard@foo.com")
;;(map #(d/entity dbval (first %)) *1)
;;(map d/touch *1)



(def dbval (db @conn))
(map #(q '[:find ?e :in $ ?alias :where [?e :shorturi/alias ?alias]] dbval %)
     ["dustingetz" "bob"])
(map #(d/entity dbval (ffirst %)) *1)
(map #(d/touch %) *1)




(d/entity dbval (ffirst *1))
(d/touch *1)







(let [newUser ]
  @(d/transact @conn [newUser]))







{:shorturi/alias "google"
 :shorturi/uri "http://www.google.com/"
 :db/id #db/id[:db.part/user -1000001]}

{:shorturi/alias "dustingetz"
 :shorturi/uri "http://www.dustingetz.com/"
 :db/id #db/id[:db.part/user -1000002]}
