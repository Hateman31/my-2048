(ns my-2048.utils)

(defn draw-square [point ctx]
  (let [[m n] point]
    (.fillRect ctx m n 80 80)))

(defn get-row [step size gap]
  (for [rank (range size)] (* rank (+ step gap))))

(defn get-grid [step rank gap]
  (let [line (get-row step rank gap)
        f #(reduce conj %1 %2)
        cells (for [m line] (for [n line] [m n]))]
        (reduce f cells)))

(defn get-ctx [canvas]
  (.getContext canvas "2d"))

(defn set-color [ctx color]
  (set! (.-fillStyle ctx) color))

(defn set-font [ctx font]
  (set! (.-font ctx) font))

(defn set-text [ctx text cell]
  (let [[x y] cell]
    (.fillText ctx text x y)))

(defn build-grid [grid ctx]
  (dorun (for [point grid] (draw-square point ctx))))

(defn clear-canvas [canvas]
  (let [ctx (get-ctx canvas)
        w (.-width canvas)
        h (.-height canvas)]
    (.clearRect ctx 0 0 w h)))

