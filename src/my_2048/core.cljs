(ns my-2048.core
  (:require [my-2048.utils :as u]
    [my-2048.game :as g]
    [rxjs :as rx]
    [my-2048.swipe :as swipe]
    ))

(def game 
  (.getElementById js/document "game"))

(def score-label (.getElementById js/document "score"))

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

(defn render-score [new-score] 
  (set! (.-textContent score-label) new-score))

(def game-state
  (atom (g/init-state)))

(def prev-game-state
  (atom []))

(def score
  (atom (g/get-score @game-state)))

(defn update-field! [direction] 
      (let [
        shift #(g/update-grid %1 direction)]
        (if (and 
              direction 
              (->> @game-state
                (g/rotate-grid direction)
                g/grid-movable? ))
          (swap! game-state shift))))

(def undobtn
  (.getElementById js/document "undobtn"))

(defn main []
 (do 
  (set! (.-disabled undobtn) true)

  (.subscribe (swipe/arrowSwipe) update-field!)
  (.subscribe (swipe/touchSwipe game) update-field!)
  ;; (.subscribe (swipe/touchSwipe js/document) update-field!)
  (.subscribe 
    (rx/fromEvent (.getElementById js/document "newgamebtn") "click") 
      #(do 
        (reset! game-state (g/init-state))
        (set! (.-disabled undobtn) true)))
  
  (.subscribe 
    (rx/fromEvent undobtn "click") 
      #(do (reset! game-state @prev-game-state)
        (set! (.-disabled undobtn) true)))

  (add-watch game-state :updating
    #(do 
       (render-game %4)
       (reset! prev-game-state %3)
       (set! (.-disabled undobtn) false)
       (swap! score (fn [] (g/get-score %4)))
       (render-score @score)))

  (add-watch game-state :game-ending
    #(let [game-state %4]
        (cond 
          (g/win? game-state)
            (js/alert "You won!")
          (g/lose? game-state)
            (js/alert "You lost!"))))
  
  (render-game @game-state)
  (render-score @score)))
