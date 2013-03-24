(ns artscentre.schema
  (:use [datomico.core :only [build-schema]]))


(def User (build-schema :User [[:username :string :unique]]))

(def UserInfo (build-schema :UserInfo [[:username   :string :unique]
                                       [:firstName  :string]
                                       [:lastName   :string]
                                       [:email      :string :unique]
                                       [:created    :instant]]))

(def Skill (build-schema :Skill [[:name :string :unique]]))

(def Project (build-schema :Project [[:name     :string]
                                     [:owner    :ref]
                                     [:created  :instant]]))

(def ProjectInfo (build-schema :ProjectInfo [[:name     :string :unique]
                                             [:owner    :ref]
                                             [:created  :instant]
                                             [:members  :ref :many]
                                             [:skills   :ref :many]]))
