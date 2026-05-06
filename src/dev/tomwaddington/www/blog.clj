(ns dev.tomwaddington.www.blog
  "Functions for rendering the blog index and posts."
  (:require [clojure.java.io :as io]
            [dev.tomwaddington.www.icons :refer [feed-link]]
            [dev.tomwaddington.www.render :refer [format-date-long render-page]]
            [dev.tomwaddington.www.util :refer [fetch-file-data]]))

(defn get-posts
  "Fetch the posts from the filesystem in descending date order."
  []
  (into []
        (sort #(compare (:published %2) (:published %1))
              (map #(fetch-file-data (.getCanonicalPath %))
                   (filter #(not= \. (first (.getName %)))
                           (.listFiles (io/file "posts")))))))

(defn nav
  "Render the left navigation."
  ([posts]
   [:nav {:class "blog"}
    [:ol {:reversed true}
     (map (fn [current-post] [:li
                              [:a
                               {:href
                                (str "/" (name (:slug current-post)) ".html")}
                               [:span (:title current-post)]
                               (format-date-long (:published current-post))]])
          posts)] feed-link])
  ([posts post]
   [:nav {:class "blog"}
    [:ol {:reversed true}
     (map (fn [current-post]
            [:li {:class (when (= (:slug current-post) (:slug post)) "active")}
             (if (= (:slug current-post) (:slug post))
               [:span [:span (:title current-post)]
                (format-date-long (:published current-post))]
               [:a {:href (str "/" (name (:slug current-post)) ".html")}
                [:span (:title current-post)]
                (format-date-long (:published current-post))])])
          posts)] feed-link]))

(defn blog
  "Render the blog index or a single post."
  ([posts]
   (let [title "Blog"]
     (render-page
      {:body    [:main {:class "index"}
                 [:article {:class "blog"} [:h1 title]
                  [:ol
                   {:class    "post-list"
                    :reversed true}
                   (map (fn [post]
                          [:li
                           [:a {:href (str "/" (name (:slug post)) ".html")}
                            [:span (:title post)]]
                           (format-date-long (:published post))
                           [:p {:class "synopsis"} (:synopsis post)]])
                        posts)]] (nav posts)]
       :section :index
       :title   title})))
  ([posts post]
   (let [title   (:title post)
         article [:article {:class "blog"} [:h1 title]
                  (format-date-long (:published post))]]
     (render-page {:body    [:main {:class "blog"}
                             (reduce conj article (:body post))
                             (nav posts post)]
                   :post    post
                   :section :blog}))))
