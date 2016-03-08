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
  [modifier] 
  (let [timer (Timer. 100)]
    (events/listen
     timer
     goog.Timer.TICK
     (fn [e]
       (modifier update-in [:tick] #(inc (or % 0)))))
    {:start (fn []
              (modifier assoc :start (now))
              (.start timer))
     :stop (fn []
             (.stop timer))}))

(defn timer [modifier]
  (let [v (modifier)
        now (now)
        start (:start v now)
        duration (- now start)]
    [:span
     (format-seconds duration)]))
