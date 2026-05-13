(ns www.header "Functions for rendering the page header.")

(defn header
  "Render the page header to Hiccup. Nav link to the current page should be disabled if appropriate."
  {:malli/schema [:function
                  [:=> [:cat [:enum :about :blog :cv :index :projects]]
                   [:vector :some]]]}
  [section]
  [:header
   [:img
    {:alt           ""
     :fetchpriority "high"
     :height        1080
     :src           "/static/avatar.png"
     :width         1080}] [:p "Tom Waddington"]
   [:nav
    [:ul
     (if (contains? #{:blog :index} section)
       [:li.active
        (if (= section :blog) [:a {:href "/index.html"} "Blog"] [:span "Blog"])]
       [:li [:a {:href "/index.html"} "Blog"]])
     (if (= section :projects)
       [:li.active [:span "Projects"]]
       [:li [:a {:href "/projects.html"} "Projects"]])
     (if (= section :cv)
       [:li.active [:span "CV"]]
       [:li [:a {:href "/cv.html"} "CV"]])
     (if (= section :about)
       [:li.active [:span "About"]]
       [:li [:a {:href "/about.html"} "About"]])]]])
