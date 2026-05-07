(ns www.about
  "Functions for rendering the About page."
  (:require [www.render :refer [render-page]]
            [www.util :refer [fetch-file-data]]))

(defn fetch-about-data
  "Retrieve and parse the About page content."
  {:malli/schema [:function [:=> :cat [:map [:content [:vector :some]]]]]}
  []
  (fetch-file-data "about/about.edn"))

(defn about
  "Render the About page from EDN read from the FS, to a string of HTML."
  {:malli/schema [:function [:=> :cat :string]]}
  []
  (let [title   "About"
        content (fetch-about-data)]
    (render-page
     {:body    [:main {:class "about"}
                (reduce conj [:article [:h1 title]] (:content content))]
      :section :about
      :title   title})))
