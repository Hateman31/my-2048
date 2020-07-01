(ns my-2048.utils)

(def arrow-direction {
      "ArrowDown" :down
      "ArrowUp" :up
      "ArrowLeft" :left
      "ArrowRight" :right})

(def tile-color {
  0 "#776e65",
  2 "#eee4da",
  4 "#ede0c8",
  8 "#f2b179",
  16 "#f59563",
  32 "#f67c5f",
  64 "#f65e3b",
  128 "#edcf72",
  256 "#edcc61",
  512 "#edcc61",
  1024 "#edc53f",
  2048 "#edc22e"})

(defn draw-square [point ctx]
  (let [[m n] point]
    (.fillRect ctx m n 80 80)))

(defn get-row [size delta]
  (mapv #(* delta %1) (range size)))

(defn get-grid [step rank gap]
  (let [delta (+ step gap)
      line (get-row rank delta)
      f #(reduce conj (vec %1) (vec %2))
      cells (for [m line] (for [n line] [n m]))]
    (reduce f cells)))

(defn get-ctx [canvas]
  (.getContext canvas "2d"))

(defn set-color [ctx color]
  (set! (.-fillStyle ctx) color))

(defn set-font [ctx font]
  (set! (.-font ctx) font))

(defn get-font-size [value]
  (str 
    (cond 
      (< value 16) 45
      (< value 128) 40
      (< value 1024) 33
      :else 27)))

(defn get-delta-x [value]
  (cond 
    (< value 16) 24
    (< value 128) 16
    (< value 1024) 9
    :else 6))

(defn draw-field [ctx game-field]
  (doseq [[point cell-value] game-field] 
    (set-color ctx "black")
    (draw-square point ctx))
  (doseq [[point cell-value] game-field] 
    (if (> cell-value 0)
      (let [text (str cell-value)
          [x y] point
          font-size (get-font-size cell-value)
          font (str font-size "px serif")
          delta-x (get-delta-x cell-value)
          delta-y 55]
        (set-color ctx "orange")
        (set-font ctx font)
        (.fillText ctx text (+ x delta-x) (+ y delta-y))))))
      
(defn clear-canvas [canvas]
  (let [ctx (get-ctx canvas)
        w (.-width canvas)
        h (.-height canvas)]
    (.clearRect ctx 0 0 w h)))
