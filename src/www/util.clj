(ns www.util
  "Utility functions."
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [still.core :refer [snap!]]))

(defn list-files
  "List files with suffix from directory."
  {:malli/schema [:function
                  [:=> [:cat :string :string] [:maybe [:sequential :file]]]]}
  [dir suffix]
  (seq (fs/list-dir dir (str "*" suffix))))

(defn fetch-file-data
  "Retrieve and parse an EDN file."
  {:malli/schema [:function [:=> [:cat :string] [:maybe :any]]]}
  [f]
  (edn/read-string (slurp f)))

(defn current-year-in-zone
  "Returns the current year in a given timezone. Falls back to Europe/London if the timezone is unrecognised."
  {:malli/schema [:function [:=> [:cat :string] :int]]}
  [zone-id]
  (-> (try (java.time.ZoneId/of zone-id)
           (catch java.time.zone.ZoneRulesException _e
             (java.time.ZoneId/of "Europe/London")))
      (java.time.ZonedDateTime/now)
      (.getYear)))

(snap! (instance? java.lang.Integer (current-year-in-zone "Europe/London"))
       true)
(snap! (instance? java.lang.Integer (current-year-in-zone "Z")) true)
(snap! (= (current-year-in-zone "Europe/London")
          (current-year-in-zone "Bite/Me"))
       true)

(defn get-fonts
  "Fetch a list of fonts from the filesystem."
  {:malli/schema [:function [:=> :cat [:vector :string]]]}
  []
  (into [] (map #(str (fs/canonicalize %)) (list-files "f" ".woff2"))))

(defn get-static
  "Fetch the static from the filesystem."
  {:malli/schema [:function [:=> :cat [:vector :string]]]}
  []
  (into []
        (map #(str (fs/canonicalize %))
             (filter #(not= \. (first (str (fs/file-name %))))
                     (fs/list-dir "static")))))

(defn copy-static-files!
  "Copy static files to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (doseq [f (apply conj (get-static) (get-fonts))]
    (let [destination (s/replace f #"(\/f\/|\/static\/)" "/docs$1")]
      (fs/create-dirs (fs/parent (fs/path destination)))
      (fs/copy (fs/path f) (fs/path destination)))))

(defn with-neighbours
  {:malli/schema [:function
                  [:=>
                   [:cat [:sequential :any]]
                   [:sequential [:vector [:maybe :any]]]]]}
  [xs]
  (map vector (cons nil xs) xs (concat (rest xs) [nil])))
