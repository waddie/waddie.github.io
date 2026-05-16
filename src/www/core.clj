(ns www.core
  "Functions for testing and building."
  (:gen-class)
  (:require [clojure.tools.build.api :as b]
            [clojure.java.io :refer [make-parents]]
            [cognitect.test-runner.api :as test-runner]
            [hiccup.page :as page]
            [hiccup2.core :as h]
            [www.about :refer [about]]
            [www.blog :refer [blog get-posts]]
            [www.cv :refer [cv]]
            [www.projects :refer [get-projects projects]]
            [www.robots :refer [write-robots!]]
            [www.atom :refer [feed]]
            [www.cname :refer [write-cname!]]
            [www.sitemap :refer [sitemap]]
            [www.util :refer [copy-static-files!]]))

(defn write-page!
  "Write a page to a filename."
  [filename content]
  (let [path (str "docs/" filename)]
    (make-parents path)
    (spit path content)))

(defn write-xml!
  "Write a feed to a filename."
  [filename content]
  (let [path (str "docs/" filename)]
    (make-parents path)
    (spit path
          (str (page/xml-declaration "UTF-8")
               (h/html {:escape-strings? false
                        :mode :xml}
                       content)))))

(defn build
  "Build the static site."
  [_]
  (time (do (b/delete {:path "docs/"})
            (let [posts (get-posts)
                  projs (get-projects)]
              (doseq [post posts]
                (let [[_ curr _] post]
                  (write-page! (str (name (:slug curr)) ".html")
                               (blog posts post))))
              (write-page! "about.html" (about))
              (write-page! "cv.html" (cv))
              (write-page! "index.html" (blog posts))
              (write-page! "projects.html" (projects projs))
              (write-xml! "feed.atom" (feed posts))
              (write-xml! "sitemap.xml" (sitemap))
              (write-robots!)
              (write-cname!)
              (copy-static-files!)))))

(defn run-tests [] (time (test-runner/test nil)))

(defn run-build [] (build nil) (println "Built to docs/"))
