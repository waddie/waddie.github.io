(ns dev.tomwaddington.www.core
  "Functions for testing and building."
  (:require [cognitect.test-runner.api :as test-runner]
            [dev.tomwaddington.www.about :refer [about]]
            [dev.tomwaddington.www.blog :refer [blog get-posts]]
            [dev.tomwaddington.www.cv :refer [cv]]
            [dev.tomwaddington.www.projects :refer [get-projects projects]]
            [dev.tomwaddington.www.rss :refer [feed]]
            [dev.tomwaddington.www.util :refer [copy-static-files]]
            [hiccup2.core :as h]))

(defn ^:private write-page
  "Write a page to a filename."
  [filename content]
  (spit (str "docs/" filename) (str (h/raw content))))

(defn ^:private write-feed
  "Write a feed to a filename."
  [filename content]
  (spit (str "docs/" filename)
        (str (h/html {:escape-strings? false
                      :mode :xml}
                     content))))

(defn build
  "Build the static site."
  [_]
  (time (let [posts (get-posts)]
          (doseq [post posts]
            (write-page (str (name (:slug post)) ".html") (blog posts post)))
          (let [ps (get-projects)]
            (write-page "index.html" (projects ps)))
          (write-feed "feed.rss" (feed posts))
          (write-page "about.html" (about))
          (write-page "cv.html" (cv))
          ; (write-page "index.html" (blog posts))
          (copy-static-files))))

(defn run-tests
  []
  (in-ns 'dev.tomwaddington.www.core)
  (time (test-runner/test nil)))

(defn run-build
  []
  (in-ns 'dev.tomwaddington.www.core)
  (build nil)
  (println "Built to docs/"))

(comment
  (run-tests))

(comment
  (run-build))
