(ns my-2048.game)

(defn new-row [row]
  (loop [res [], row row]
    (let [[x & [y & xs :as ys]] row]
      (cond
        (nil? y) (into res row)
        (== x y) (recur (conj res (* 2 x)) xs)
        :else (recur (conj res x) ys)))))

(defn collapse-cells [grid] 
  nil)

(def grid-transform 
  { :up  [[0 4 8 12] [1 5 9 13] [2 6 10 14] [3 7 11 15]]
    :down [[12 8 4 0] [13 9 5 1] [14 10 6 2] [15 11 7 3]]
    :right [[3 2 1 0] [7 6 5 4] [11 10 9 8] [15 14 13 12]]})

(defn matrix-to-vector [matrix]
  (loop [[x & xs] matrix, res []]
    (let [el (reduce conj res x)]
      (if (nil? xs)
        el
        (recur xs el)))))