(ns schema-mock.core
  (require [schema.core :as s]
           [clj-time.core :as time]))

(def conversion-period (time/days 1))

(defn fetch-orders [start-date end-date]
  ;; go to the database and fetch orders between those datetimes
  [])

(defn fetch-clicks [start-date end-date customer]
  ;; go to the database and get clicks between these dates for this customer
  [])

(defn calculate-conversions-since [start-date]
  [])
