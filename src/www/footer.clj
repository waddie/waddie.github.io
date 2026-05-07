(ns www.footer
  "Functions for rendering the page footer."
  (:require [still.core :refer [snap!]]
            [www.schema :as schema]
            [www.util :as u]))

(defn footer-html
  "Render the footer to Hiccup."
  {:malli/schema [:function [:=> [:cat :int [:maybe :int]] [:vector :some]]]}
  [start-year end-year]
  [:footer
   [:p "© Tom Waddington " start-year
    (when (and (not (nil? end-year)) (not= start-year end-year))
      (str "-" end-year))]])

(defn footer
  "Render the page footer with the appropriate copyright date range, based on the content."
  {:malli/schema [:function
                  [:=> :cat [:vector :some]]
                  [:=> [:cat schema/BlogPost] [:vector :some]]]}
  ([]
   (let [start-year 2025
         end-year   (u/current-year-in-zone "Europe/London")]
     (footer-html start-year end-year)))
  ([post]
   (let [start-year (+ 1900 (.getYear (:published post)))
         end-year   (when (:updated post) (+ 1900 (.getYear (:updated post))))]
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
