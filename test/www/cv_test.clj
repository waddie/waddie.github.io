(ns www.cv-test
  (:require [clojure.test :as test :refer [deftest testing]]
            [malli.core :as m]
            [still.core :refer [snap!]]
            [www.cv :as cv]
            [www.schema :as schema]))

(deftest valid-cv-test
  (let [cv-data (cv/fetch-cv-data)]
    (testing "cv is valid"
      (snap! (try (m/validate schema/CV cv-data) (catch Exception _e false))
             true))))
