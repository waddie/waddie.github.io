(ns www.projects-test
  (:require [clojure.test :as test :refer [deftest testing]]
            [hiccup.compiler :as hc]
            [malli.core :as m]
            [still.core :refer [snap!]]
            [www.projects :as projects]
            [www.schema :as schema]))

(deftest valid-projects-test
  (let [projects         (projects/get-projects)
        compiled-content (map #(rest (apply hc/compile-html (:description %)))
                              projects)]
    (testing "every project is valid"
      (snap! (try (m/validate [:maybe [:vector schema/Project]] projects)
                  (catch Exception _e false))
             true))
    (testing "every project description compiles"
      (snap! (try (m/validate [:maybe [:sequential [:sequential :string]]]
                              compiled-content)
                  (catch Exception _e false))
             true))))
