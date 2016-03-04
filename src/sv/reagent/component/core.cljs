(ns sv.reagent.component.core
  (:require [reagent.core :as reagent]))

;; -------------------------
;; Views

(defn home-page []
  [:div ""])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
