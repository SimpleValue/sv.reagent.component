(ns sv.reagent.component.timer
  (:require [goog.events :as events])
  (:import goog.Timer))

(defn format-seconds
  [duration-ms]
  (str (long (/ duration-ms 1000))
       "."
       (long (/ (mod duration-ms 1000) 10))
       "s"))

(defn now []
  (.getTime (js/Date.)))

(defn timer-process
  [model]
  (let [timer (Timer. 100)]
    (events/listen
     timer
     goog.Timer.TICK
     (fn [e]
       (swap! model update-in [:tick] #(inc (or % 0)))))
    {:start (fn []
              (swap! model assoc
                     :start (now)
                     :running true)
              (.start timer))
     :stop (fn []
             (.stop timer)
             (swap!
              model
              (fn [m]
                (assoc
                 m
                 :running false
                 :duration (- (now) (:start m))))))}))

(defn timer [model]
  (let [v @model
        now (now)
        start (:start v now)
        duration (- now start)]
    [:span
     (format-seconds duration)]))
