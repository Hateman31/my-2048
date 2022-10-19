(ns my-2048.swipe
    (:require 
      [rxjs :as rx]
      [my-2048.utils :as u]))

(def one-touch?
  (rx/filter (fn [t] (== t.touches.length 1))))

(def prevent-def 
  (rx/tap #(do (.preventDefault %1))))

(defn get-xy [event] 
  (let [
    touches (if (= event.type "touchend") 
                event.changedTouches
                event.touches)
    point (aget touches 0)]
    [point.pageX point.pageY]))
    
(defn log [msg]
  (fn [event] (println msg (get-xy event))))

(defn get-direction [dx dy]
  (cond 
    (>= (Math/abs dx) (Math/abs dy))
      (if (> dx 0) :right :left)
    (< (Math/abs dx) (Math/abs dy))
      (if (> dy 0) :up :down)))

(defn arrowSwipe []
  (.pipe (rx/fromEvent js/document "keydown")
   (rx/map (fn [event] (u/arrow-direction event.key)))))

(defn touchSwipe [el]
    (let [ 
        get-stream  #(prevent-def (rx/fromEvent el %))
        touch-start (get-stream "touchstart")
        touch-move  (get-stream "touchmove")
        touch-end   (get-stream "touchend")
        changedDirection (atom 0)
        is-growing? (fn [x0 y0 pt nt]
                      (let [[px py] pt
                            [nx ny] nt
                            dpx (Math/abs (- px x0))
                            dpy (Math/abs (- y0 py))
                            dnx (Math/abs (- nx x0))
                            dny (Math/abs (- y0 ny))]
                          (and (>= dnx dpx) (>= dny dpy))))
        swipe-pipe  (fn [ts]
                      (let [
                        touch0 (aget ts.touches 0)
                        x0 touch0.pageX
                        y0 touch0.pageY] 
                        (.pipe 
                            touch-move
                            one-touch?
                            (rx/map get-xy)
                            (rx/map (fn [[nx ny] _]
                                      [[nx ny] (get-direction (- nx x0) (- y0 ny))]))
                            (rx/pairwise)
                            (rx/tap (fn [[[pt old-direction] [nt new-direction]] _]
                                        (swap! 
                                          changedDirection 
                                          #(if (and (zero? @changedDirection) (= old-direction new-direction)) 0 1))))
                            (rx/takeWhile 
                                (fn [[[pt old-direction] [nt new-direction]] _]
                                        (is-growing? x0 y0 pt nt)))
                            (rx/takeWhile                            
                                (fn [[[pt old-direction] [nt new-direction]] _]
                                  (or (= old-direction new-direction)
                                      (and (not= old-direction new-direction) (= @changedDirection 1)))))
                            (rx/takeUntil touch-end)
                            (rx/takeLast 1)
                            (rx/map (fn [[_ [_ direction]] _ _] direction))
                        )))   
        drag  (.pipe touch-start 
                  one-touch?
                  ;; (rx/tap #(println "ololo"))
                  (rx/switchMap swipe-pipe))
      ]    
    drag
))