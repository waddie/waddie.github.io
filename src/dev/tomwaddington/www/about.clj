(ns dev.tomwaddington.www.about
  "Functions for rendering the About page."
  (:require [dev.tomwaddington.www.render :refer [render-page]]
            [dev.tomwaddington.www.util :refer [fetch-file-data]]))


(defn ^:private fetch-about-data
  "Retrieve and parse the About page content."
  []
  (fetch-file-data "about/about.edn"))

(defn about
  "Render the About page."
  []
  (let [title   "About"
        content (fetch-about-data)]
    (render-page
     {:body    [:main {:class "about"}
                (reduce conj [:article [:h1 title]] (:content content))]
      :section :about
      :title   title})))
