(ns www.blog
  "Functions for rendering the blog index and posts."
  (:require [clojure.core.match :refer [match]]
            [www.icons :refer [feed-link]]
            [www.render :refer [format-date-long render-page]]
            [www.schema :as schema]
            [www.util :refer [fetch-file-data list-files with-neighbours]]))

(defn get-posts
  "Fetch the posts from the filesystem in descending date order."
  {:malli/schema [:function
                  [:=> :cat [:sequential [:vector [:maybe schema/BlogPost]]]]]}
  []
  (with-neighbours (sort #(compare (:published %2) (:published %1))
                         (filter :published
                                 (map #(fetch-file-data (.getCanonicalPath %))
                                      (list-files "posts" ".edn"))))))

(defn nav
  "Render the left navigation."
  {:malli/schema [:function
                  [:=>
                   [:cat
                    [:sequential [:vector [:maybe schema/BlogPost]]]]
                   [:vector :some]]
                  [:=>
                   [:cat
                    [:sequential [:vector [:maybe schema/BlogPost]]]
                    [:vector [:maybe schema/BlogPost]]]
                   [:vector :some]]]}
  ([posts]
   [:nav {:class "blog"}
    [:ol {:reversed true}
     (map (fn [[_ current-post _]]
            [:li
             [:a
              {:href (str "/" (name (:slug current-post)) ".html")}
              [:span (:title current-post)]
              (format-date-long (:published current-post))]])
          posts)] feed-link])
  ([posts [_ post _]]
   [:nav {:class "blog"}
    [:ol {:reversed true}
     (map (fn [[_ current-post _]]
            [:li {:class (when (= (:slug current-post) (:slug post)) "active")}
             (if (= (:slug current-post) (:slug post))
               [:span [:span (:title current-post)]
                (format-date-long (:published current-post))]
               [:a {:href (str "/" (name (:slug current-post)) ".html")}
                [:span (:title current-post)]
                (format-date-long (:published current-post))])])
          posts)] feed-link]))

(defn neighbour-link
  [post rel]
  [:li
   {:class rel}
   [:a {:href (str "/" (name (:slug post)) ".html")}
    [:span (:title post)]]])

(defn blog
  "Render the blog index or a single post."
  {:malli/schema [:function
                  [:=>
                   [:cat [:sequential [:vector [:maybe schema/BlogPost]]]]
                   :string]
                  [:=>
                   [:cat
                    [:sequential [:vector [:maybe schema/BlogPost]]]
                    [:vector [:maybe schema/BlogPost]]]
                   :string]]}
  ([posts]
   (let [title "Blog"]
     (render-page
      {:body    [:main {:class "index"}
                 [:article {:class "blog"}
                  [:h1 title]
                  [:p "Always end on a song."]
                  [:ol
                   {:class    "post-list"
                    :reversed true}
                   (map (fn [[_ post _]]
                          [:li
                           [:a {:href (str "/" (name (:slug post)) ".html")}
                            [:span (:title post)]]
                           (format-date-long (:published post))
                           [:p {:class "synopsis"} (:synopsis post)]])
                        posts)]] (nav posts)]
       :section :index
       :title   title})))
  ([posts post]
   (let [[next curr prev] post
         title            (:title curr)
         published        (format-date-long (:published curr))
         updated          (when (:updated curr)
                            (format-date-long (:updated curr)))
         updated-notif    (when (and updated (not= published updated))
                            (list " (updated: "
                                  (format-date-long (:updated curr))
                                  ")"))
         article          [:article {:class "blog"}
                           [:h1 title]
                           `[:p.publication-date
                             ~@(list published updated-notif)
                            ]]]
     (render-page
      {:body    [:main {:class "blog"}
                 (reduce conj
                         article
                         (conj (:body curr)
                               (match [(nil? prev) (nil? next)]
                                 [true false] [:nav
                                               [:ol
                                                (neighbour-link next "next")]]
                                 [false true] [:nav
                                               [:ol
                                                (neighbour-link prev "prev")]]
                                 [false false] [:nav
                                                [:ol
                                                 (neighbour-link prev "prev")
                                                 (neighbour-link next "next")]]
                                 :else (throw (Exception. "Impossible post")))))
                 (nav posts post)]
       :post    curr
       :section :blog}))))
