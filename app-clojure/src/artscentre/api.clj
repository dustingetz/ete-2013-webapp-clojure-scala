(ns artscentre.api
  (:use [datomic.api :only [db q] :as d])
  (:require [artscentre.orm.skill :as skill]))

(defn whoami [user dbval]
  {:firstName "Dustin"
   :lastName "Getz"
   :email "dustin.getz@gmail.com"
   :username "dustin"
   :created (java.util.Date.)})






(defn listSkillsUserPicker [user dbval]

  (let [all-skills   (->> (skill/read-all dbval)
                          (mapv d/touch))
        user-skills  (->> (skill/read-by-user dbval)
                          (mapv d/touch))]

    ;; (->> (map :Skill/name all-skills)
    ;;      (into #{})
    ;;      {:id id
    ;;       :name name
    ;;       :enabled true}
    ;;      (partial contains? (map :Skill/name user-skills))
    ;;      )

    )





  ;; get the skills for our user
  ;; merge them into the desired payload shape
  {}
  )
