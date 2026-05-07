(ns www.core
  "Functions for testing and building."
  (:require [cognitect.test-runner.api :as test-runner]
            [hiccup.page :as page]
            [hiccup2.core :as h]
            [www.about :refer [about]]
            [www.blog :refer [blog get-posts]]
            [www.cv :refer [cv]]
            [www.projects :refer [get-projects projects]]
            [www.robots :refer [write-robots!]]
            [www.rss :refer [feed]]
            [www.sitemap :refer [sitemap]]
            [www.util :refer [copy-static-files!]]))

(defn write-page!
  "Write a page to a filename."
  [filename content]
  (spit (str "docs/" filename) content))

(defn write-xml!
  "Write a feed to a filename."
  [filename content]
  (spit (str "docs/" filename)
        (str (page/xml-declaration "UTF-8")
             (h/html {:escape-strings? false
                      :mode :xml}
                     content))))

(defn build
  "Build the static site."
  [_]
  (time (let [posts (get-posts)
              projs (get-projects)]
          (doseq [post posts]
            (write-page! (str (name (:slug post)) ".html") (blog posts post)))
          (write-page! "about.html" (about))
          (write-page! "cv.html" (cv))
          (write-page! "index.html" (blog posts))
          (write-page! "projects.html" (projects projs))
          (write-xml! "feed.rss" (feed posts))
          (write-xml! "sitemap.xml" (sitemap))
          (write-robots!)
          (copy-static-files!))))

(defn run-tests [] (time (test-runner/test nil)))

(defn run-build [] (build nil) (println "Built to docs/"))
