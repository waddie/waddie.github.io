(ns www.about-test
  (:require [clojure.test :as test :refer [deftest testing]]
            [hiccup.compiler :as hc]
            [malli.core :as m]
            [still.core :refer [snap!]]
            [www.about :as about]))

(deftest valid-about-test
  (let [data (about/fetch-about-data)
        compiled-content (rest (apply hc/compile-html (:content data)))]
    (testing "about data is valid"
      (snap! (try (m/validate [:maybe [:map [:content [:vector :some]]]] data)
                  (catch Exception _e false))
             true))
    (testing "about content compiles"
      (snap! (try (m/validate [:maybe [:sequential :string]] compiled-content)
                  (catch Exception _e false))
             true))))
