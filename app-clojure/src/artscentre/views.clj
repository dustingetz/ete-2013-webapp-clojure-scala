(ns artscentre.views
  (:require [ring.util.response :as response]
            [artscentre.model :as model]))


(defn form []
  (response/file-response "/form.html" {:root "resources/public"}))

(defn shorten-uri [uri]
  (str (hash uri)))

(defn formSubmit [form]
  (let [{:keys [uri]} form
        shortkey (shorten-uri uri)]
    (model/redirect-put shortkey uri)
    (response/response (str "http://.../" shortkey))))

(defn index []
  (response/redirect "/form"))

(defn redirect [key]
  (response/redirect (model/redirect-get key)))
