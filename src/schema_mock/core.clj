(ns schema-mock.core
  (require [schema.core :as s]
           [clj-time.core :as time]))

(def conversion-period (time/days 1))
(defn conversion-period-for  [order]  (time/minus  (:when order) conversion-period))

(def Customer {:uuid s/Str})
(def Order { :who Customer :when org.joda.time.DateTime :what s/Any})
(def Click { :who Customer :when org.joda.time.DateTime })
(def Conversion {:click Click :outcome Order})

(defn fetch-orders [start-date end-date]
  ;; go to the database and fetch orders between those datetimes
  [])

(defn fetch-clicks [start-date end-date customer]
  ;; go to the database and get clicks between these dates for this customer
  [])

(declare notice-conversions)

(s/defn calculate-conversions-since :- [Conversion] [start-date]
  (let [end-date (time/minus (time/now) (time/hours 1))
        orders (fetch-orders start-date end-date)]
    (apply concat
      (for [o orders]
        (let [clicks (fetch-clicks (conversion-period-for o) (:when o) (:who o))]
          (notice-conversions o clicks))))))

(s/defn notice-conversions :- [Conversion] [order :- Order
                                            clicks :- [Click]]
  ;; dummy implementation: stupid-easiest way to green
  [{:click (first clicks) :outcome order}])
