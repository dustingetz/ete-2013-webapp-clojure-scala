(ns platform.datomic-util
  (:use [datomic.api :only [db q] :as d]))


(defn qes [query dbval & args]
  (->> (apply d/q query dbval args)
       (mapv first)
       (mapv (partial d/entity dbval))))

(defn qe [query dbval & args]
  (let [res (apply d/q query dbval args)]
    (d/entity dbval (ffirst res))))


(defn data-with-dbid
  "adds a tempid, for use with upsert"
  [data]
  (map #(merge {:db/id (d/tempid :db.part/user)} %1) data))

(defn eid [e] (:db/id e))

(defn by-eid
  "turns a seq of entities into a map from eid->entity"
  [entities]
  (->> (map (fn [entity]
              (let [eid (:db/id entity)]
                [eid entity]))
            entities)
       (into {})))
