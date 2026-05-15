(ns user
  #_{:clj-kondo/ignore [:unused-namespace :unused-referred-var]}
  (:require [clojure.repl :refer [doc source]]
            [hiccup.compiler :as hc]
            [malli.core :as m]
            [malli.dev :as md]
            [malli.dev.pretty :as mdp]
            [malli.instrument :as mi]
            [malli.provider :as mp]
            [still.core :refer [snap!]]
            [www.blog :as blog]
            [www.core :refer [run-build run-tests]]
            [www.schema :as schema]
            [www.util :as util]))

(comment
  (set! *warn-on-reflection* true)
  (set! *warn-on-reflection* false))

(md/start! {:report (mdp/reporter (mdp/-printer {:colors       false
                                                 :print-length 30
                                                 :print-level  2
                                                 :print-meta   false
                                                 :width        80}))})

(comment
  (mi/collect!)
  (m/function-schemas))

(comment
  (do (in-ns 'user) (run-tests)))

(comment
  (do (in-ns 'user) (run-build)))
