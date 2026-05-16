(ns www.robots
  (:require [clojure.java.io :refer [make-parents]]))

(defn write-robots!
  "Write robots.txt to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (let [path "docs/robots.txt"]
    (make-parents path)
    (spit
     path
     "User-agent: *
Allow: /
Crawl-delay: 5
Sitemap: https://www.tomwaddington.dev/sitemap.xml
")))
