(ns artscentre.orm.projectinfo
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]
        [clojure.set :as set]))


(def schema (build-schema :ProjectInfo [[:name     :string :unique]
                                        [:owner    :ref]
                                        [:members  :ref :many]
                                        [:created  :instant]
                                        [:skills   :ref :many]]))


(defn read-all [dbval]
  (->> (qes '[:find ?e
              :where [?e :ProjectInfo/name]] dbval)
       (mapv d/touch)))


(defn- read-projects-matching-user-skills [user dbval]
  (qes '[:find ?project :in $ ?username
         :where [ ?user :User/username ?username ]       ; resolve the user
                [ ?user :User/skills ?skill ]            ; user's skills
                [ ?project :ProjectInfo/skills ?skill ]  ; intersect the user's skills with the project's skills
                ]
       dbval user))

(defn- read-projects-for-user [user dbval]
  (qes '[:find ?project :in $ ?username
         :where [ ?user :User/username ?username ]
                [ ?project :ProjectInfo/members ?user]]
       dbval user))

(defn read-elligible-projects [user dbval]
  (let [projects   (read-projects-matching-user-skills user dbval)
        myprojects (read-projects-for-user user dbval)]

    ;; subtract out any projects we're already in
    (set/difference (into #{} (map :db/id projects))
                    (into #{} (map :db/id myprojects)))))



(comment ; for testing in a repl

  (def dbconn artscentre.db/conn)
  (def dbval (d/db @dbconn))

  (->> (read-projects-matching-user-skills "dustin" dbval)
       (mapv :ProjectInfo/name))

  (->> (read-projects-for-user "dustin" dbval)
       (mapv :ProjectInfo/name))

  (->> (read-elligible-projects "dustin" dbval)
       (mapv :ProjectInfo/name))

  (->> (read-all dbval)
       (mapv :ProjectInfo/name))

  )
