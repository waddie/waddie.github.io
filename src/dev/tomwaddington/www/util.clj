(ns dev.tomwaddington.www.util
  "Utility functions."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(defn fetch-file-data
  "Retrieve and parse an EDN file."
  [f]
  (edn/read-string (slurp f)))

(defn current-year-in-zone
  "What year is it in the given zone?"
  [zone-id]
  (-> (java.time.ZoneId/of zone-id)
      (java.time.ZonedDateTime/now)
      (.getYear)))

(defn get-fonts
  "Fetch the fonts from the filesystem."
  []
  (into []
        (map #(.getCanonicalPath %)
             (filter #(not= \. (first (.getName %)))
                     (.listFiles (io/file "f"))))))

(defn get-static
  "Fetch the static from the filesystem."
  []
  (into []
        (map #(.getCanonicalPath %)
             (filter #(not= \. (first (.getName %)))
                     (.listFiles (io/file "static"))))))

(defn copy-static-files
  []
  (doseq [f (apply conj (get-static) (get-fonts))]
    (io/copy (io/file f)
             (io/file (s/replace f #"(\/f\/|\/static\/)" "/target$1")))))
