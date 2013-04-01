(ns platform.ring-auth)


(def ^{:dynamic true :doc "The currently logged in user bound for the life of a Ring request."} *user*)

(defn wrap-auth
  [handler]
  (fn [request]
    (binding [*user* "dustingetz"]   ;; stubbed out auth
      (handler request))))
