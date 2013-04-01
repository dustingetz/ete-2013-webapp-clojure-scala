(ns platform.datomic-ring
  (:use [datomic.api :only [db q] :as d]))

;; modified from bobby's datomic helpers: https://gist.github.com/bobby/3150938

(def ^{:dynamic true :doc "A Datomic database value used over the life of a Ring request."} *dbval*)
(def ^{:dynamic true :doc "A Datomic connection bound for the life of a Ring request."} *dbconn*)

(defn wrap-datomic
  [dbconn handler]
  (fn [request]
    (binding [*dbconn* dbconn
              *dbval* (d/db @dbconn)]
      (handler request))))
