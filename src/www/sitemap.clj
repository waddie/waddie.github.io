(ns www.sitemap
  (:require [babashka.fs :as fs]
            [www.render :refer [format-date-iso]]
            [www.util :refer [list-files]]))

(defn get-html
  "Fetch a list of html files from the docs/ folder in alphabetical order."
  {:malli/schema [:function [:=> :cat [:vector :file]]]}
  []
  (into []
        (sort #(compare (str (fs/file-name %1)) (str (fs/file-name %2)))
              (list-files "docs" ".html"))))

(defn sitemap
  "Render the sitemap to Hiccup."
  {:malli/schema [:function [:=> :cat [:vector :some]]]}
  []
  [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
   (map (fn [page] [:url
                    [:loc
                     (str "https://www.tomwaddington.dev/" (fs/file-name page))]
                    [:lastmod
                     (format-date-iso
                      (java.time.ZonedDateTime/ofInstant
                       (java.time.Instant/ofEpochMilli
                        (fs/file-time->millis (fs/last-modified-time page)))
                       (java.time.ZoneId/of "Europe/London")))]])
        (get-html))])
