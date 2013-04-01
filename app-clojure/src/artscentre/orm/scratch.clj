(ns artscentre.orm.scratch
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [artscentre.datomic-util]))








(def Skill (build-schema :Skill [[:name         :string :unique]
                                 [:description  :string]]))




(def ProjectInfo (build-schema :ProjectInfo [[:name     :string :unique]
                                             [:owner    :string]
                                             [:created  :instant]
                                             [:skills   :ref :many]]))





(defn read-all-projects [dbval]
  (qes '[:find ?e
         :where [?e :ProjectInfo/name]] dbval))

(defn read-skill-by-name [dbval skill-name]
  (qe '[:find ?e
         :in $ ?name
         :where [?e :Skill/name ?name]]
       dbval skill-name))




;; entry point for repl demo

(def conn (atom nil))

(defn start-dev-db []
  (reset! conn (let [uri "datomic:mem://artscentre"]
                 (d/delete-database uri)
                 (d/create-database uri)
                 (d/connect uri)))
  (let [schema-tx (concat Skill ProjectInfo)]
    @(d/transact @conn schema-tx)))


(defn load-sample-data [dbconn]

  ;; (start-dev-db)
  ;; (def dbconn @conn)
  ;; (def dbval (db dbconn))


  ;; load some skills

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

  (def txresult @(d/transact dbconn (data-with-dbid skill-list)))

  ;; (def dbval (:db-before txresult))
  (def dbval (:db-after  txresult))



  (let [clojure     (:db/id (read-skill-by-name dbval "Clojure"))
        scala       (:db/id (read-skill-by-name dbval "Scala"))
        datomic     (:db/id (read-skill-by-name dbval "Datomic"))
        java        (:db/id (read-skill-by-name dbval "Java"))
        postgresql  (:db/id (read-skill-by-name dbval "Postgresql"))

        artscentre  {:ProjectInfo/name "Artscentre"
                     :ProjectInfo/owner "Dustin"
                     :ProjectInfo/created (java.util.Date.)
                     :ProjectInfo/skills [clojure scala datomic]}

        tinyurl     {:ProjectInfo/name "Tinyurl"
                     :ProjectInfo/owner "Kevin"
                     :ProjectInfo/created (java.util.Date.)
                     :ProjectInfo/skills [java postgresql]}]

    (def txresult @(d/transact dbconn (data-with-dbid [artscentre tinyurl]a))))



  ;; (def dbval (:db-before txresult))
  (def dbval (:db-after  txresult))

  (->> (read-all-projects dbval)
     (first)
     (d/touch)
     (:ProjectInfo/skills)
     (mapv d/touch)
     )

  )





;; (read-all-projects (db @artscentre.db/conn))
