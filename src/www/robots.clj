(ns www.robots)

(defn write-robots!
  "Write robots.txt to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (spit
   "docs/robots.txt"
   "User-agent: *
Allow: /
Crawl-delay: 5
Sitemap: https://www.tomwaddington.dev/sitemap.xml
"))
