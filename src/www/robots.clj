(ns www.robots
  (:require [babashka.fs :as fs]))

(defn write-robots!
  "Write robots.txt to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (let [path "docs/robots.txt"]
    (fs/create-dirs (fs/parent (fs/path path)))
    (spit
     path
     "User-agent: *
Allow: /
Crawl-delay: 5
Sitemap: https://www.tomwaddington.dev/sitemap.xml
")))
