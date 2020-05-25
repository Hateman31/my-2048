(ns my-2048.game)

(def field-transitions
  { :up  [0 4 8 12 1 5 9 13 2 6 10 14 3 7 11 15]
    :down [15 11 7 3 14 10 6 2 13 9 5 1 12 8 4 0]
    :right [3 2 1 0 7 6 5 4 11 10 9 8 15 14 13 12]})

(defn matrix-to-vector [matrix]
  (loop [[x & xs] matrix, res []]
    (let [el (reduce conj res x)]
      (if (nil? xs)
        el
        (recur xs el)))))

(defn has-pair? [row]
    (some true? (map == row (rest row))))

(defn lose? [grid]
  (let [game-field (matrix-to-vector grid)]
    (not (or 
      (some zero? game-field)
      (some has-pair? grid)))))

(defn win? [grid]
  (let [game-field (matrix-to-vector grid)]
      (some (partial == 2048) grid)))

(defn collapse-row [row]
  (loop [res [], row row]
    (let [[x & [y & xs :as ys]] row]
      (cond
        (nil? y) (into res row)
        (== x y) (recur (conj res (* 2 x)) xs)
        :else (recur (conj res x) ys)))))

(defn field-transform [direction game-field]
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

(defn get-2-or-4 []
  (let [p (rand)]
    (if (>= p 0.95) 4 2)))

(defn get-rand-index [coll fun]
  (let [coll-map (map-indexed vector coll)
        indexes (for 
          [[i item] coll-map :when (fun item)] i)]
    (if (some number? indexes)
      (rand-nth indexes))))

(def contains-zero?
  (partial some zero?))

(defn get-empty-cell [grid]
  (let 
    [row-index (get-rand-index grid contains-zero?)]
    (if (number? row-index)
      [row-index (get-rand-index (grid row-index) zero?)])))

(defn update-coll [coll index new-item]
  (let [coll-map (map-indexed vector coll)]
    (vec (for [[n item] coll-map]
      (if (= n index) new-item item)))))

(defn add-new-item [grid cell]
  (let [[x y] cell
        row (grid x)
        new-item (get-2-or-4)
        new-row (update-coll row y new-item)]
      (update-coll grid x new-row)))

(def update-row
  (comp add-zeroes collapse-row del-zeroes))

(def divide-by-4 
  (comp (partial mapv vec) (partial partition 4)))

(defn transform-grid [direction grid]
  (let [game-field (matrix-to-vector grid)]
    (field-transform direction game-field)))

(defn transform [direction]
  (let [f (partial transform-grid direction)]
    (comp vec divide-by-4 f))) 

(defn update-grid [grid direction] 
  (if (= direction :left)
    (mapv update-row grid)
    (let [t (transform direction)
          shifted-grid
            (->> grid
              t (mapv update-row) t)
          free-cell (get-empty-cell shifted-grid)]
      (if free-cell
        (add-new-item shifted-grid free-cell)
        shifted-grid))))
    