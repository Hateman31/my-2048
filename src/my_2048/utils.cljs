(ns my-2048.utils)

(defn draw-square [point ctx]
  (let [[m n] point]
    (.fillRect ctx m n 80 80)))

(defn get-grid [step rank gap]
  (let [line (for [x (range rank)] (+ (* x rank) gap))]
    (for [m line] (for [n line] [m n]))))

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
  (dorun (for [row grid] ;; build grid
    (dorun (for [point row] (draw-square point ctx))))))
