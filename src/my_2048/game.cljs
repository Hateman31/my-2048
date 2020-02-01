(defn collapse [trin]
    (if (not= 3 (count trin)) 
      trin
      (let [[head mid tail] trin]
        (or (if (= mid head) [(* 2 mid) tail])
            (if (= mid tail) [head (* 2 mid)])
            trin))))


(defn new-row [row]
  [])