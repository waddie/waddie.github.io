(ns www.cname
  (:require [clojure.java.io :refer [make-parents]]))

(defn write-cname!
  "Write CNAME to the target folder."
  {:malli/schema [:function [:=> :cat :nil]]}
  []
  (let [path "docs/CNAME"]
    (make-parents path)
    (spit path "www.tomwaddington.dev")))
