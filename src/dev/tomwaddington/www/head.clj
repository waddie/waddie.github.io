(ns dev.tomwaddington.www.head
  "Render the metadata section to Hiccup."
  (:require [clojure.core.match :refer [match]]))

(defn ^:private head-html
  "Render the head section."
  [title & rest]
  [:head [:meta {:charset "utf-8"}]
   [:meta
    {:content "width=device-width,initial-scale=1.0"
     :name    "viewport"}]
   [:link
    {:href "/static/style.css"
     :rel  "stylesheet"}]
   [:link
    {:href "/static/highlight.css"
     :rel  "stylesheet"}] [:title title] [:script {:src "/static/htmx.min.js"}]
   [:link
    {:href "/static/favicon.ico"
     :rel  "icon"}]
   [:link
    {:href "/static/apple-touch-icon.png"
     :rel  "apple-touch-icon"}]
   [:meta
    {:content
     (or
      (first rest)
      "Tom Waddington is a computer programmer based in London, United Kingdom.")
     :name "description"}]])

(defn head
  "Render the head section appropriately for the type of page.

  Post pages take their title and description from the post data."
  [{:keys [title post]}]
  (match [(some? title) (some? (:title post))]
    [true true] (head-html (str (:title post) " | Blog | Tom Waddington")
                           (:synopsis post))
    [_ true] (head-html (str (:title post) " | Blog | Tom Waddington")
                        (:synopsis post))
    [true _] (head-html (str title " | Tom Waddington"))
    :else (head-html "Tom Waddington")))
