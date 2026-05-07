(ns www.projects
  "Functions for rendering the project page."
  (:require [clojure.java.io :as io]
            [www.render :refer [render-page]]
            [www.schema :as schema]
            [www.util :refer [fetch-file-data]]))

(defn get-projects
  "Fetch the projects from the filesystem in ascending alphabetical order."
  {:malli/schema [:function [:=> :cat [:vector schema/Project]]]}
  []
  (into []
        (sort #(compare (:title %1) (:title %2))
              (map #(fetch-file-data (.getCanonicalPath %))
                   (filter #(not= \. (first (.getName %)))
                           (.listFiles (io/file "projects")))))))

(defn projects
  "Render the projects index."
  {:malli/schema [:function [:=> [:cat [:vector schema/Project]] :string]]}
  [projects]
  (let [title "Projects"
        list  [:ol {:class "project-list"}]]
    (render-page
     {:body    [:main {:class "index"}
                [:article {:class "projects"} [:h1 title]
                 (reduce (fn [list project]
                           (conj
                            list
                            [:li
                             [:a
                              {:href   (:url project)
                               :target "_blank"} [:span (:title project)]]
                             (reduce
                              conj
                              [:div {:class "description"}
                               (when-let [clojars (:clojars project)]
                                 [:p
                                  [:a
                                   {:href   (str "https://clojars.org/" clojars)
                                    :target "_blank"}
                                   [:img
                                    {:alt (str (:title project) " on Clojars")
                                     :height 20
                                     :src (str
                                           "https://img.shields.io/clojars/v/"
                                           clojars
                                           ".svg")}]]])]
                              (:description project))]))
                         list
                         projects)]]
      :section :projects
      :title   title})))
