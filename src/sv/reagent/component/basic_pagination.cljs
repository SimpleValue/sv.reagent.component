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

(defn view [params]
  (let [{:keys [page page-size count]} params
        start-row (* page page-size)
        last-page (dec (.ceil js/Math
                              (/ count
                                 page-size)))]
    {:page page
     :page-size page-size
     :count count
     :last-page last-page
     :start-row start-row
     :end-row (let [end-row (+ start-row page-size)]
                (if (< end-row count)
                  end-row
                  count))
     :next (not= page last-page)
     :prev (not= page 0)}))

(def defaults
  {:page-size default-page-size})

(defn pagination [model page-change]
  (let [m @model]
    [:div
     (when (:page m)
       (let [v (view (merge defaults m))]
         [:div
          (when (:prev v)
            [a "First " (fn [e]
                          (go
                            (<! (page-change 0))
                            (swap! model assoc :page 0)))])
          (when (:prev v)
            [a "< Prev" (fn [e]
                          (go
                            (when (< 0 (:page v))
                              (let [page (dec (:page v))]
                                (<! (page-change page))
                                (swap! model assoc :page page)))))])
          (let [{:keys [start-row end-row]} v]
            [:span " Rows "
             start-row " - " end-row
             " of " (:count v) " "])
          (when (:next v)
            [a "Next >" (fn [e]
                          (go
                            (let [page (inc (:page v))]
                              (<! (page-change page))
                              (swap! model assoc :page page))))])
          (when (:next v)
            [a " Last" (fn [e]
                         (go
                           (let [page (:last-page v)]
                             (<! (page-change page))
                             (swap! model assoc :page page))))])]))]))
