(ns dev.tomwaddington.www.rss
  (:require [dev.tomwaddington.www.render :refer [format-date-iso]]))

(defn feed
  "Render the RSS feed."
  [posts]
  [:feed
   (map (fn [post] [:entry [:id (name (:slug post))] [:title (:title post)]
                    [:link
                     {:href (str "https://www.tomwaddington.dev/"
                                 (name (:slug post))
                                 ".html")}] [:author [:name "Tom Waddington"]]
                    [:summary (:synopsis post)]
                    [:published (format-date-iso (:published post))]
                    [:updated (format-date-iso (:updated post))]])
        posts)])
