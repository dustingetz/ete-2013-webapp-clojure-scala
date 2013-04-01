(ns artscentre.orm.project
  (:use [datomic.api :only [db q] :as d]
        [datomico.core :only [build-schema]]
        [platform.datomic-util]))


(def schema  (build-schema :Project [[:name     :string]
                                     [:owner    :ref]
                                     [:created  :instant]]))
