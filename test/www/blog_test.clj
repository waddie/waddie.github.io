(ns www.blog-test
  (:require [clojure.test :as test :refer [deftest testing]]
            [hiccup.compiler :as hc]
            [malli.core :as m]
            [still.core :refer [snap!]]
            [www.blog :as blog]
            [www.schema :as schema]))

(deftest valid-posts-test
  (let [posts (blog/get-posts)
        compiled-content (map #(rest (apply hc/compile-html (:body %))) posts)]
    (testing "every blog post is valid"
      (snap! (try (m/validate [:maybe [:vector schema/BlogPost]] posts)
                  (catch Exception _e false))
             true))
    (testing "all blog post content compiles"
      (snap! (try (m/validate [:maybe [:sequential [:sequential :string]]]
                              compiled-content)
                  (catch Exception _e false))
             true))))
