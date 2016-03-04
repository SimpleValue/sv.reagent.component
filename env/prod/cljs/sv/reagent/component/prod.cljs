(ns sv.reagent.component.prod
  (:require [sv.reagent.component.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
