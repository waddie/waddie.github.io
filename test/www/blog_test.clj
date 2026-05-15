(ns www.blog-test
  (:require [clojure.test :as test :refer [deftest testing]]
            [hiccup.compiler :as hc]
            [malli.core :as m]
            [still.core :refer [snap!]]
            [www.blog :as blog]
            [www.schema :as schema]))

(deftest valid-posts-test
  (let [posts (blog/get-posts)
        compiled-content
        (map (fn [[_ post _]] (apply hc/compile-html (:body post))) posts)]
    (testing "every blog post is valid"
      (snap! (try (m/validate [:maybe
                               [:sequential [:vector [:maybe schema/BlogPost]]]]
                              posts)
                  (catch Exception _e false))
             true))
    (testing "all blog post content compiles"
      (snap! (try (m/validate [:maybe [:sequential :some]] compiled-content)
                  (catch Exception _e false))
             true))))
