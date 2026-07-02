(ns www.projects
  "Functions for rendering the project page."
  (:require [babashka.fs :as fs]
            [www.render :refer [render-page]]
            [www.schema :as schema]
            [www.util :refer [fetch-file-data]]))

(defn get-projects
  "Fetch the projects from the filesystem in ascending alphabetical order."
  {:malli/schema [:function [:=> :cat [:vector schema/Project]]]}
  []
  (into []
        (sort #(compare (:title %1) (:title %2))
              (map #(fetch-file-data (str (fs/canonicalize %)))
                   (filter #(not= \. (first (str (fs/file-name %))))
                           (fs/list-dir "projects"))))))

(defn get-categories
  "Fetch category metadata from the filesystem."
  {:malli/schema [:function [:=> :cat [:map-of :keyword schema/Category]]]}
  []
  (fetch-file-data (str (fs/canonicalize "categories.edn"))))

(defn categorise
  "Group projects by category, sorted by category title."
  {:malli/schema
   [:function
    [:=>
     [:cat
      [:vector schema/Project]
      [:map-of :keyword schema/Category]]
     [:vector [:tuple :keyword schema/Category [:vector schema/Project]]]]]}
  [projects categories]
  (->> (group-by :category projects)
       (map (fn [[category-key ps]] [category-key (categories category-key)
                                     ps]))
       (sort-by (comp :title second))
       (into [])))

(defn nav
  "Render shortcut nav."
  {:malli/schema
   [:function
    [:=>
     [:cat [:vector [:tuple :keyword schema/Category [:vector schema/Project]]]]
     [:vector :some]]]}
  [grouped]
  [:nav {:class "projects"}
   [:ul {:class "quicklinks"}
    (map (fn [[category-key category projects]]
           [:li
            [:a {:href (str "#" (name category-key))}
             [:span {:class "label"} (:title category)]]
            [:ol
             (map (fn [project] [:li
                                 [:a {:href (str "#" (name (:slug project)))}
                                  (:title project)]])
                  projects)]])
         grouped)]])

(defn project-item
  "Render a single project's list item."
  {:malli/schema [:function [:=> [:cat schema/Project] [:vector :some]]]}
  [project]
  [:li
   [:a
    {:href (:url project)
     :id   (name (:slug project))} [:span (:title project)]]
   (reduce conj
           [:div {:class "description"}
            (when-let [clojars (:clojars project)]
              [:p
               [:a
                {:href (str "https://clojars.org/" clojars)}
                [:img
                 {:alt    (str (:title project) " on Clojars")
                  :height 20
                  :src    (str "https://img.shields.io/clojars/v/"
                               clojars
                               ".svg")}]]])]
           (when (:description project) (:description project)))])

(defn category-section
  "Render a category heading and its projects."
  {:malli/schema [:function
                  [:=>
                   [:cat :keyword schema/Category [:vector schema/Project]]
                   [:vector :some]]]}
  [category-key category projects]
  [:div {:class "category"}
   [:h2 {:id (name category-key)} (:title category)]
   (:description category)
   (reduce conj [:ol {:class "project-list"}] (map project-item projects))])

(defn projects
  "Render the projects index."
  {:malli/schema [:function [:=> [:cat [:vector schema/Project]] :string]]}
  [projects]
  (let [title   "Projects"
        grouped (categorise projects (get-categories))]
    (render-page
     {:body    [:main {:class "index"}
                [:article {:class "projects"}
                 [:h1 title]
                 [:p "Free and open source software that I build and maintain."]
                 (map (fn [[category-key category ps]]
                        (category-section category-key category ps))
                      grouped)] (nav grouped)]
      :section :projects
      :title   title})))
