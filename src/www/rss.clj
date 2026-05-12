(ns www.rss
  (:require [www.render :refer [format-date-iso]]
            [www.schema :as schema]))

(defn feed
  "Render the RSS feed to Hiccup."
  {:malli/schema [:function
                  [:=> [:cat [:vector schema/BlogPost]] [:vector :some]]]}
  [posts]
  [:feed {:xmlns "http://www.w3.org/2005/Atom"}
   [:id "https://www.tomwaddington.dev/"]
   [:link {:href "https://www.tomwaddington.dev"}]
   [:link
    {:href "https://www.tomwaddington.dev/feed.atom"
     :rel  "self"}]
   [:title "Tom Waddington’s Blog"]
   [:updated (format-date-iso (java.util.Date.))]
   (map (fn [post]
          (let [published (format-date-iso (:published post))
                updated   (if (:updated post)
                            (format-date-iso (:updated post))
                            published)
                url       (str "https://www.tomwaddington.dev/"
                               (name (:slug post))
                               ".html")]
            [:entry
             [:author [:name "Tom Waddington"]]
             [:id url]
             [:link {:href url}]
             [:published published]
             [:summary (:synopsis post)]
             [:title (:title post)]
             [:updated updated]]))
        posts)])
