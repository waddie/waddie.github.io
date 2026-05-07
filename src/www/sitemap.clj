(ns www.sitemap
  (:require [www.render :refer [format-date-iso]]
            [www.util :refer [list-files]]))

(defn get-html
  "Fetch a list of html files from the docs/ folder in alphabetical order."
  {:malli/schema [:function [:=> :cat [:vector :file]]]}
  []
  (into []
        (sort #(compare (.getName %1) (.getName %2))
              (list-files "docs" ".html"))))

(defn sitemap
  "Render the sitemap to Hiccup."
  {:malli/schema [:function [:=> :cat [:vector :some]]]}
  []
  [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
   (map (fn [page] [:url
                    [:loc
                     (str "https://www.tomwaddington.dev/" (.getName page))]
                    [:lastmod
                     (format-date-iso
                      (java.time.ZonedDateTime/ofInstant
                       (java.time.Instant/ofEpochMilli (.lastModified page))
                       (java.time.ZoneId/of "Europe/London")))]])
        (get-html))])
