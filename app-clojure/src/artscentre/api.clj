(ns artscentre.api
  (:use [artscentre.orm.skill :as skill]
        [datomic.api :only [db q] :as d]))

(defn whoami [dbval]
  {:firstName "Dustin"
   :lastName "Getz"
   :email "dustin.getz@gmail.com"
   :username "dustin"
   :created (java.util.Date.)})

(defn listSkillsUserPicker [dbval]
  ;; get all the skills

  (let [all-skills   (->> (skill/read-all dbval)
                          (d/touch))
        user-skills  (->> (skill/read-by-user dbval)
                          (d/touch))]

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
