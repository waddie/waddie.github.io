(ns www.render
  "Utility functions for rendering data to Hiccup."
  (:require [clojure.edn :as edn]
            [clojure.string :as s]
            [hiccup.page :as page]
            [still.core :refer [snap!]]
            [www.footer :refer [footer]]
            [www.head :refer [head]]
            [www.header :refer [header]]
            [www.schema :as schema]))

(defn render-page
  "Render a page in the standard template."
  {:malli/schema [:function
                  [:=>
                   [:cat
                    [:map
                     [:title {:optional true} :string]
                     [:post {:optional true} schema/BlogPost]
                     [:section :keyword]
                     [:body [:vector :some]]]]
                   :string]]}
  [{:keys [title post section body]}]
  (page/html5 {:lang "en-GB"}
              (head {:post  post
                     :title title})
              [:body {:data-hx-boost "true"} (header section) body
               (if (coll? post) (footer post) (footer))]))

(def month-names
  ["January" "February" "March" "April" "May" "June" "July" "August" "September"
   "October" "November" "December"])

(defn number-suffix
  "Returns the correct English suffix for a number."
  {:malli/schema [:function [:=> [:cat :int] :string]]}
  [i]
  (let [last-digit (last (str i))]
    (cond (= 11 i) "th"
          (= 12 i) "th"
          (= 13 i) "th"
          (= \1 last-digit) "st"
          (= \2 last-digit) "nd"
          (= \3 last-digit) "rd"
          :else "th")))

(snap! (number-suffix 3) "rd")
(snap! (number-suffix 11) "th")
(snap! (number-suffix 22) "nd")

(defn format-date-MY
  "Format inline short date as Hiccup."
  {:malli/schema [:function [:=> [:cat :string] [:vector :some]]]}
  [date]
  (let [split (s/split date #"-")
        year  (first split)
        month (month-names (- (parse-long (last split)) 1))]
    [:time {:datetime date} month " " year]))

(snap! (format-date-MY "2008-09")
       [:time {:datetime "2008-09"} "September" " " "2008"])

(defn format-date-YMD
  "Format a Date in yyyy-mm-dd format."
  {:malli/schema [:function [:=> [:cat inst?] :string]]}
  [date]
  (let [day   (.getDate date)
        month (+ 1 (.getMonth date))
        year  (+ 1900 (.getYear date))]
    (str year "-" (format "%02d" month) "-" (format "%02d" day))))

(defn format-date-long
  "Format a Date in Hiccup 1st January 2025 format as Hiccup."
  {:malli/schema [:function [:=> [:cat inst?] [:vector :some]]]}
  [date]
  (let [day        (.getDate date)
        month      (.getMonth date)
        month-name (month-names month)
        year       (+ 1900 (.getYear date))]
    [:time
     {:class    "publication-date"
      :datetime (format-date-YMD date)} day [:sup (number-suffix day)] " "
     month-name " " year]))

(defmulti format-date-iso "Format a date for ISO." class)

(defmethod format-date-iso java.util.Date
  [date]
  (let [day      (format "%02d" (.getDate date))
        month    (format "%02d" (+ 1 (.getMonth date)))
        year     (+ 1900 (.getYear date))
        hours    (format "%02d" (.getHours date))
        minutes  (format "%02d" (.getMinutes date))
        seconds  (format "%02d" (.getSeconds date))
        timezone (.getTimezoneOffset date)]
    (str year
         "-" month
         "-" day
         "T" hours
         ":" minutes
         ":" seconds
         ".000" (if (= timezone 0) "Z" "+01:00"))))

(defmethod format-date-iso java.time.ZonedDateTime
  [date]
  (let [day      (format "%02d" (.getDayOfMonth date))
        month    (format "%02d" (.getMonthValue date))
        year     (.getYear date)
        hours    (format "%02d" (.getHour date))
        minutes  (format "%02d" (.getMinute date))
        seconds  (format "%02d" (.getSecond date))
        timezone (.getOffset date)]
    (str year
         "-" month
         "-" day
         "T" hours
         ":" minutes
         ":" seconds
         ".000" (if (= (str timezone) "+00:00") "Z" "+01:00"))))

(let [date (edn/read-string "#inst \"2026-01-28T19:02:00.000Z\"")]
  (snap! (format-date-YMD date) "2026-01-28")
  (snap! (format-date-iso date) "2026-01-28T19:02:00.000Z")
  (snap! (format-date-long date)
         [:time
          {:class    "publication-date"
           :datetime "2026-01-28"} 28 [:sup "th"] " " "January" " " 2026]))
