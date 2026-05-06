(ns dev.tomwaddington.www.header "Functions for rendering the page header.")

(defn ^:private header-html
  "Render the page header. Nav link to the current page should be disabled if appropriate."
  [section]
  [:header
   [:img
    {:alt ""
     :src "/static/avatar.png"}] [:p "Tom Waddington"]
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

(defn header "Render the page header." [section] (header-html section))
