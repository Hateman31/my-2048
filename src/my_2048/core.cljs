(ns my-2048.core
  (:require [my-2048.utils :as u]
    [my-2048.game :as g]))

(enable-console-print!)

(def game 
  (.getElementById js/document "game"))

(def background 
  (.getContext game "2d"))

(def grid
  (u/get-grid 80 4 3))

(def game-state
  (atom (g/init-state)))

(defn render-game [game-state]
  (let [
      game-field (map list grid (g/matrix-to-vector game-state))] 
    (u/clear-canvas game)
    (u/draw-field background game-field)))

(.addEventListener js/document "keydown" 
  (fn [event] 
    (let [direction (u/arrow-direction event.key)]
      (if direction
        (let [shift #(g/update-grid %1 direction)]
          (swap! game-state shift))))))

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
