(ns my-2048.game)

(def field-transitions
  { :up  [0 4 8 12 1 5 9 13 2 6 10 14 3 7 11 15]
    :down [12 8 4 0 13 9 5 1 14 10 6 2 15 11 7 3]
    :right [3 2 1 0 7 6 5 4 11 10 9 8 15 14 13 12]})

(defn collapse-row [row]
  (loop [res [], row row]
    (let [[x & [y & xs :as ys]] row]
      (cond
        (nil? y) (into res row)
        (== x y) (recur (conj res (* 2 x)) xs)
        :else (recur (conj res x) ys)))))

(defn field-transform [game-field direction]
  (let [transition (field-transitions direction)]
    (for [cell-num transition]
      (get game-field cell-num))))

(defn del-zeroes [row]
  (vec (for [c row :when (not= c 0)] c)))

(defn add-zeroes [row]
    (let [len (count row)
          delta (- 4 len)
          zeroes (repeat delta 0)]
      (reduce conj row zeroes)))

(defn matrix-to-vector [matrix]
  (loop [[x & xs] matrix, res []]
    (let [el (reduce conj res x)]
      (if (nil? xs)
        el
        (recur xs el)))))

(defn update-grid [grid direction] 
  (let [game-field (matrix-to-vector grid)
        transformed-field (field-transform game-field direction)
        new-grid (partition 4 transformed-field)]
    (vec (for [row new-grid]
        ((comp add-zeroes collapse-row del-zeroes) row)))))
