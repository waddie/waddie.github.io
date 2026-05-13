(ns www.cv
  "Functions for building the CV page."
  (:require [www.icons :refer
             [email-link github-link linkedin-link tangled-link]]
            [www.render :refer [format-date-MY render-page]]
            [www.schema :as schema]
            [www.util :refer [fetch-file-data]]))

(defn fetch-cv-data
  "Retrieve and parse the CV data."
  {:malli/schema [:function [:=> :cat schema/CV]]}
  []
  (fetch-file-data "cv/cv.edn"))

(defn arrangements
  "Return working arrangement in English."
  {:malli/schema [:function [:=> [:cat :keyword] :string]]}
  [a]
  (a {:hybrid  "Hybrid"
      :on-site "On-site"
      :remote  "Remote"}))

(defn roles
  "Render roles for a company."
  {:malli/schema [:function [:=> [:cat schema/Experience] [:vector :some]]]}
  [company]
  (when (seq (:roles company))
    [:ol {:reversed true}
     (map (fn [role] [:li
                      [:h4 (:title role)
                       (when (:department role) (str ", " (:department role)))]
                      [:p (format-date-MY (:start-date role)) " – "
                       (or (format-date-MY (:end-date role)) "Present")]
                      (map (fn [achievement] [:p achievement])
                           (:achievements role))])
          (:roles company))]))

(defn experience
  "Render the Experience section."
  {:malli/schema [:function [:=> [:cat schema/CV] [:vector :some]]]}
  [data]
  (when (seq (:experience data))
    [:ol {:reversed true}
     (map (fn [company] [:li {:id (:slug company)}
                         [:h3
                          [:a
                           {:data-hx-boost "false"
                            :href          (:url company)
                            :target        "_blank"} (:company company)]]
                         [:p (:location company) " ("
                          (arrangements (:arrangement company)) ")"]
                         [:p (:overview company)] (roles company)])
          (:experience data))]))

(defn education
  "Render the Education section."
  {:malli/schema [:function [:=> [:cat schema/CV] [:vector :some]]]}
  [data]
  (when (seq (:education data))
    [:ol {:reversed true}
     (map (fn [institution]
            [:li {:id (:slug institution)}
             [:h3
              [:a
               {:data-hx-boost "false"
                :href (:url institution)} (:institution institution)]]
             [:p (format-date-MY (:start-date institution)) " – "
              (or (format-date-MY (:end-date institution)) "Present")]
             [:p (:degree institution) " " [:i (:grade institution)]]
             [:p "Dissertation: " (:dissertation institution)]])
          (:education data))]))

(defn experience-quicklinks
  "Render Experience quicklinks."
  {:malli/schema [:function [:=> [:cat schema/CV] [:vector :some]]]}
  [data]
  (when (seq (:experience data))
    [:li
     [:a {:href "#experience"} [:span {:class "screenreader-only"} "Skip to "]
      [:span {:class "label"} "Experience"]]
     [:ol {:reversed true}
      (map (fn [company] [:li
                          [:a {:href (str "#" (name (:slug company)))}
                           [:span {:class "screenreader-only"} "Skip to "]
                           (:company company)]])
           (:experience data))]]))

(defn education-quicklinks
  "Render Education quicklinks."
  {:malli/schema [:function [:=> [:cat schema/CV] [:vector :some]]]}
  [data]
  (when (seq (:education data))
    [:li
     [:a {:href "#education"} [:span {:class "screenreader-only"} "Skip to "]
      [:span {:class "label"} "Education"]]
     [:ol {:reversed true}
      (map (fn [institution] [:li
                              [:a {:href (str "#" (name (:slug institution)))}
                               [:span {:class "screenreader-only"} "Skip to "]
                               (:institution institution)]])
           (:education data))]]))

(defn nav
  "Render shortcut nav."
  {:malli/schema [:function [:=> [:cat schema/CV] [:vector :some]]]}
  [data]
  [:nav {:class "cv"} github-link tangled-link linkedin-link email-link
   [:ul {:class "quicklinks"} (experience-quicklinks data)
    (education-quicklinks data)]])

(defn cv
  "Render the content for the CV page."
  {:malli/schema [:function [:=> :cat :string]]}
  []
  (let [title "Curriculum Vitae"
        data  (fetch-cv-data)]
    (render-page
     {:body    [:main {:class "cv"}
                [:article {:class "cv"} [:h1 title]
                 [:div {:class "experience"}
                  [:h2 {:id "experience"} "Experience"]
                  [:p "Never twice the same industry."] (experience data)]
                 [:div {:class "education"} [:h2 {:id "education"} "Education"]
                  (education data)]] (nav data)]
      :section :cv
      :title   title})))
