(ns dev.tomwaddington.www.blog-test
  (:require [clojure.test :refer [deftest testing]]
            [still.core :refer [snap]]
            [dev.tomwaddington.www.blog :as blog]
            [hiccup2.core :as h]))

(def ^:private posts
  [{:body      [[:p "This is test content."]]
    :published #inst "2025-03-15T02:00Z"
    :slug      :test-post
    :synopsis  "This is just a test."
    :title     "Test Post"
    :updated   #inst "2025-03-15T02:00Z"}
   {:body      [[:p "This is more test content."]]
    :published #inst "2025-04-29T02:00Z"
    :slug      :test-post-the-second
    :synopsis  "This is also a test."
    :title     "Another Test Post"
    :updated   #inst "2025-04-29T02:00Z"}])

(deftest post-snapshot-test
  (testing "generates expected HTML structure"
    (let [result (blog/blog posts (first posts))]
      (snap ::post-basic result))))

(deftest blog-snapshot-test
  (testing "generates expected page structure"
    (let [result (str (h/raw (blog/blog posts)))]
      (snap ::blog-basic result))))
