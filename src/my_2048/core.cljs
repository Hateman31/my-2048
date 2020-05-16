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

(let [
  game-matrix 
    [[2 4 2 4]
    [2 4 2 4]
    [2 4 2 4]
    [2 4 2 4]]
  game-state (g/matrix-to-vector game-matrix)
  game-field (map list grid game-state)]
  (u/draw-field background game-field))

