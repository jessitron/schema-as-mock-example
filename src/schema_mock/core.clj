(ns schema-mock.core
  (require [schema.core :as s]
           [clj-time.core :as time]))

(def conversion-period (time/days 1))
(defn conversion-period-for  [order]  (time/minus  (:when order) conversion-period))

(defn fetch-orders [start-date end-date]
  ;; go to the database and fetch orders between those datetimes
  [])

(defn fetch-clicks [start-date end-date customer]
  ;; go to the database and get clicks between these dates for this customer
  [])

(declare notice-conversions)

(defn calculate-conversions-since [start-date]
  (let [end-date (time/minus (time/now) (time/hours 1))
        orders (fetch-orders start-date end-date)]
    (for [o orders]
      (let [clicks (fetch-clicks (:when o) (conversion-period-for o) (:who o))]
        (notice-conversions o clicks)))))

(defn- notice-conversions [orders clicks]
  [])
