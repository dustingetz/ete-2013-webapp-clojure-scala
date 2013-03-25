(ns artscentre.views
  (:require [ring.util.response :as response]))


(defn index [] (response/redirect "/form"))
