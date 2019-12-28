(ns my-2048.core
  (:require [my-2048.utils :as u]))

(enable-console-print!)

(def game 
  (.getElementById js/document "game"))

(def background 
  (.getContext game "2d"))

(def cell [116 65])
(u/set-color background "red")
(u/set-font background "77px serif")
(u/set-text background "Hello" cell)

(set! (.-onclick game) 
  (fn [] (do (js/alert "Hello") true)))