(ns my-2048.core
  (:require [my-2048.utils :as u]
    [my-2048.game :as g]))

(def game 
  (.getElementById js/document "game"))

(def grid-size
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (* 0.8 (min w h))))

(def tile-size
  (let [f1 #(- %1 9)
        f2 #(/ %1 4)]
    (-> (.-width game) f1 f2)))

(def background 
  (.getContext game "2d"))

(defn render-game [game-state]
  (u/clear-canvas game)
  (->> game-state
      g/matrix-to-vector
      (map list (u/get-vertexes tile-size 4 3))
      (u/draw-field background tile-size)))

(def game-state
  (atom (g/init-state)))

(.addEventListener js/document "keydown" 
  (fn [event] 
    (let [
      direction (u/arrow-direction event.key)
      shift #(g/update-grid %1 direction)]
      (if (and 
            direction 
            (->> @game-state
              (g/rotate-grid direction)
              g/grid-movable? ))
        (swap! game-state shift)))))

(add-watch game-state :updating
  #(render-game %4))

(add-watch game-state :game-ending
  #((let [game-state %4]
      (cond 
        (g/win? game-state)
          (js/alert "You won!")
        (g/lose? game-state)
          (js/alert "You lost!")))))

(render-game @game-state)
