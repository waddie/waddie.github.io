(ns dev.tomwaddington.www.footer
  "Functions for rendering the page footer."
  (:require [dev.tomwaddington.www.util :as u]
            [still.core :refer [snap!]]))

(defn ^:private footer-html
  "Render the footer to Hiccup."
  [start-year end-year]
  [:footer
   [:p "© Tom Waddington " start-year
    (when (not= start-year end-year) (str "-" end-year))]])

(defn footer
  "Render the page footer with the appropriate copyright date range, based on the content."
  ([]
   (let [start-year 2025
         end-year   (u/current-year-in-zone "Europe/London")]
     (footer-html start-year end-year)))
  ([post]
   (let [start-year (+ 1900 (.getYear (:published post)))
         end-year   (+ 1900 (.getYear (:updated post)))]
     (footer-html start-year end-year))))

(snap! (footer {:body      [[:p "This is test content."]]
                :published #inst "2025-03-15T02:00Z"
                :slug      :test-post
                :synopsis  "This is just a test."
                :title     "Test Post"
                :updated   #inst "2025-03-15T02:00Z"})
       [:footer [:p "© Tom Waddington " 2025 nil]])

(snap! (footer {:body      [[:p "This is test content."]]
                :published #inst "2025-03-15T02:00Z"
                :slug      :test-post
                :synopsis  "This is just a test."
                :title     "Test Post"
                :updated   #inst "2026-01-05T02:00Z"})
       [:footer [:p "© Tom Waddington " 2025 "-2026"]])
