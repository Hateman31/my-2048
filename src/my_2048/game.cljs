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

(defn has-zero? [row]
  (some zero? row))

(defn can-merge? [[a b]]
  (or
    (and (> a 0) (== a b))
    (and (zero? a) (> b a))))

(defn shift-possible? [row]
  (->> (map list row (rest row))
    (some can-merge?)))

(defn grid-movable? [grid]
  (some shift-possible? grid))

(defn collapse-row [row]
  (loop [res [], row row]
    (let [[x & [y & xs :as ys]] row]
      (cond
        (nil? y) (into res row)
        (== x y) (recur (conj res (* 2 x)) xs)
        :else (recur (conj res x) ys)))))

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

(defn field-transform [direction game-field]
  (let [transition (field-transitions direction)]
    (for [cell-num transition]
      (get game-field cell-num))))

(defn rotate-grid [direction grid]
  (if (= direction :left)
    grid
    (->> grid
      matrix-to-vector
      (field-transform direction)
      divide-by-4
      vec)))

(defn update-grid [grid direction] 
    (let [rotate #(rotate-grid direction %1) 
          update-rows #(mapv update-row %1)
          shifted-grid
            (if (= direction :left)
              (update-rows grid)
              (->> grid
                rotate 
                update-rows 
                rotate))
          free-cell (get-empty-cell shifted-grid)]
      (if free-cell
        (add-new-item shifted-grid free-cell)
        shifted-grid)))

(defn init-state []
  (let [
      row [0 0 0 0]
      state (vec (repeat 4 row))
      rand-pair #(take 2 (shuffle (range 4)))
      [x1 x2] (rand-pair)
      [y1 y2] (rand-pair)]
    (-> state
      (add-new-item [x1 y1])
      (add-new-item [x2 y2]))))

(defn lose? [grid]
  (let [
    movable-row? #(or (has-zero? %1) (has-pair? %1))
    rotated-grid (rotate-grid :up grid)
    unmovable-grid? #(not-any? movable-row? %1)]
    (and 
      (unmovable-grid? grid)
      (unmovable-grid? rotated-grid))))

(defn win? [grid]
  (let [game-field (matrix-to-vector grid)]
      (some (partial == 2048) grid)))
