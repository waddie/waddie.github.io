(ns www.util
  "Utility functions."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [still.core :refer [snap!]])
  (:import [java.io File FilenameFilter]))

(defn list-files
  "List files with suffix from directory."
  {:malli/schema [:function
                  [:=> [:cat :string :string] [:maybe [:sequential :file]]]]}
  [dir suffix]
  (seq (.listFiles (File. dir)
                   (reify
                    FilenameFilter
                      (accept [_this _dir name] (.endsWith name suffix))))))

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
  (into [] (map #(.getCanonicalPath %) (list-files "f" ".woff2"))))

(defn get-static
  "Fetch the static from the filesystem."
  {:malli/schema [:function [:=> :cat [:vector :string]]]}
  []
  (into []
        (map #(.getCanonicalPath %)
             (filter #(not= \. (first (.getName %)))
                     (.listFiles (io/file "static"))))))

(defn copy-static-files!
  "Copy static files to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (doseq [f (apply conj (get-static) (get-fonts))]
    (io/copy (io/file f)
             (io/file (s/replace f #"(\/f\/|\/static\/)" "/docs$1")))))

(defn with-neighbours
  {:malli/schema [:function
                  [:=>
                   [:cat [:sequential :any]]
                   [:sequential [:vector [:maybe :any]]]]]}
  [xs]
  (map vector (cons nil xs) xs (concat (rest xs) [nil])))
