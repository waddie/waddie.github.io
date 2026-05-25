(ns www.cname
  (:require [babashka.fs :as fs]))

(defn write-cname!
  "Write CNAME to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (let [path "docs/CNAME"]
    (fs/create-dirs (fs/parent (fs/path path)))
    (spit path "www.tomwaddington.dev")))
