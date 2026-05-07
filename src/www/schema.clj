(ns www.schema
  (:require [malli.core :as m]
            [malli.registry :as mr]
            [malli.util :as mu]))

(defn file? [x] (instance? java.io.File x))

(def BlogPost
  (-> [:map
       [:body [:vector :some]]
       [:published inst?]
       [:slug :keyword]
       [:synopsis :string]
       [:title :string]
       [:updated {:optional true} [:maybe inst?]]]
      (mu/closed-schema)))

(def Project
  (-> [:map
       [:description [:vector :some]]
       [:title :string]
       [:url :string]
       [:clojars {:optional true} :string]]
      (mu/closed-schema)))

(def Education
  (-> [:map
       [:degree :string]
       [:dissertation {:optional true} :string]
       [:end-date :string]
       [:grade :string]
       [:institution :string]
       [:slug :keyword]
       [:start-date :string]
       [:url :string]]
      (mu/closed-schema)))

(def Experience
  (-> [:map
       [:arrangement [:enum :hybrid :on-site :remote]]
       [:company :string]
       [:location :string]
       [:overview :string]
       [:slug :keyword]
       [:url :string]
       [:roles
        [:vector
         [:map
          [:achievements [:vector :string]]
          [:department {:optional true} :string]
          [:end-date :string]
          [:start-date :string]
          [:title :string]
          [:type {:optional true} :keyword]]]]]
      (mu/closed-schema)))

(def CV
  (-> [:map
       [:education [:vector Education]]
       [:experience [:vector Experience]]]
      (mu/closed-schema)))

(mr/set-default-registry! (merge (m/default-schemas)
                                 {:file (m/-simple-schema {:pred file?
                                                           :type :file})}))
