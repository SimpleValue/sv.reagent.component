(ns sv.reagent.component.basic-pagination
  "A very basic pagination component."
  (:require [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- a [label click]
  [:a
   {:href "#"
    :onClick (fn [e]
               (.preventDefault e)
               (click e))}
   label])

(def default-page-size 10)

(defn page-row-idxs [params]
  (let [page-size (:page-size params default-page-size)
        page (:page params)
        page-count (:page-count params)
        start (inc (* page page-size))
        end* (dec (+ start page-size))
        end (if (< end* page-count)
              end*
              page-count)]
    {:start start
     :end end}))

(defn pagination [modifier page-change]
  (let [v (modifier)]
    (if-not (:page v)
      [:div ""]
      [:div
       [a "First " (fn [e]
                     (go
                       (<! (page-change 0))
                       (modifier assoc :page 0)))]
       [a "< Prev" (fn [e]
                     (go
                       (when (< 0 (:page v))
                         (<! (page-change (dec (:page v))))
                         (modifier update-in [:page] dec))))]
       (let [{:keys [start end]} (page-row-idxs v)]
         [:span " Rows "
          start " - " end
          " of " (:page-count v) " "])
       [a "Next >" (fn [e]
                     (go
                       (when (> (:page-count v)
                                (let [page-size (:page-size v default-page-size)]
                                  (* (inc (:page v)) page-size)))
                         (<! (page-change (inc (:page v))))
                         (modifier update-in [:page] inc))))]
       [a " Last" (fn [e]
                    (go
                      (let [page (long (/ (:page-count v)
                                          (:page-size v default-page-size)))]
                        (<! (page-change page))
                        (modifier assoc :page page))))]])))

