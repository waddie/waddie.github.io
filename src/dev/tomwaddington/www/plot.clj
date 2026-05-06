(ns dev.tomwaddington.www.plot
  "bytemap.clj demonstration."
  (:require [bytemap.core :as bm]
            [bytemap.plot :as bp]
            [still.core :refer [snap!]]))

; (bm/print-plot! #(Math/sin %) [40 10] Math/PI 1)
; (bm/print-plot! #(Math/cos %) [40 10] Math/PI 1)
; (bm/print-plot! #(Math/tan %) [40 10] Math/PI 1)

(snap!
 (str "\n" (bp/plot->string #(Math/sin %) [40 10] Math/PI 1))
 "
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⢀⠤⠖⠚⠒⠒⢤⡀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⢀⠔⠁⠀⠀⠀⠀⠀⠀⠈⠢⡀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⢀⠔⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢆⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⢠⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠱⡀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡷⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⢄
⠹⡉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⢉⠝⡏⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉
⠀⠘⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠊⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠈⠢⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡰⠁⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠑⢄⠀⠀⠀⠀⠀⠀⠀⠀⡠⠊⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠑⢤⣀⣀⢀⣀⡤⠊⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀")

(defn golden-spiral
  "Draw the golden spiral on a canvas."
  [canvas {:keys [center scale max-θ start-angle]}]
  (let [φ       1.618033988749895
        [cx cy] center
        steps   1000]
    (loop [i          0
           prev-point nil
           canvas     canvas]
      (if (>= i steps)
        canvas
        (let [θ      (+ start-angle (* max-θ (/ i (dec steps))))
              r      (* scale (Math/pow φ (/ θ (/ Math/PI 2))))
              x      (+ cx (* r (Math/cos (- θ))))
              y      (+ cy (* r (Math/sin (- θ))))
              point  [(int x) (int y)]
              canvas (if prev-point
                       (bm/draw-line canvas prev-point point)
                       canvas)]
          (recur (inc i) point canvas))))))

(-> (bm/new-canvas 60 20)
    (golden-spiral {:center      [60 45]
                    :max-θ       (* 6 Math/PI)
                    :scale       0.5
                    :start-angle (* 0.75 Math/PI)})
    (bm/print-canvas!))

(let [τ      (* 2 Math/PI)
      c      30
      canvas (bm/new-canvas c (/ c 2))
      r      (- c 1)
      points 20
      canvas (reduce (fn [canvas i]
                       (let [angle (+ 0.1 (* i (/ τ points)))]
                         (bm/draw-line canvas
                                       [c c]
                                       [(+ c (* r (Math/cos angle)))
                                        (+ c (* r (Math/sin angle)))])))
                     canvas
                     (range points))]
  (bm/print-canvas! canvas))
