(ns artscentre.api
  (:use [datomic.api :only [db q] :as d]
        [platform.datomic-util])
  (:require [artscentre.orm.skill :as skill]
            [artscentre.orm.projectinfo :as projectinfo]))


(defn whoami [user dbval]
  {:firstName "Dustin"
   :lastName "Getz"
   :email "dustin.getz@gmail.com"
   :username "dustin"
   :created (java.util.Date.)})





(defrecord UserSkillPicker [id name enabled])


(defn listSkillsUserPicker [user dbval]

  (let [all-skills          (->> (skill/read-all dbval)
                                 (mapv d/touch))
        user-skills-by-eid  (->> (skill/read-by-user dbval user)
                                 (mapv d/touch)
                                 (by-eid))]

    (map (fn [entity]
           (let [eid      (:db/id entity)
                 name     (:Skill/name entity)
                 enabled  (contains? user-skills-by-eid eid)]
             (UserSkillPicker. eid name enabled)))
         all-skills)))





(defn list-elligible-projects
  "proejcts which the user has a matching skill, but is not a member"
  [user dbval]

  (let [ps (projectinfo/read-elligible-projects user dbval)]
    (do
      (mapv d/touch ps)
      (mapv :ProjectInfo/skills ps)
      (mapv :ProjectInfo/members ps))
    ps))


(comment

  (def dbconn artscentre.db/conn)
  (def dbval (d/db @dbconn))

  (list-elligible-projects "dustin" dbval)


  )
