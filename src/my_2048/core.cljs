(ns my-2048.core
  (:require [my-2048.utils :as u]))

(enable-console-print!)

(def game 
  (.getElementById js/document "game"))

(def background 
  (.getContext game "2d"))

(def grid
  (u/get-grid 80 4 3))

; (def cell [116 65])
; (u/set-color background "red")
; (u/set-font background "77px serif")
; (u/set-text background "Hello" cell)

(u/build-grid grid background)

