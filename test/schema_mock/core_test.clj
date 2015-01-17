(ns schema-mock.core-test
  (:require [clojure.test :refer :all]
            [schema-mock.core :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [schema.test]
            [schema.core :as s]
            [clj-time.core :as time]))

(use-fixtures :once schema.test/validate-schemas)

(defn an-hour-before [date] (time/minus date (time/hours 1)))
(defn gte  [a b]
    (>=  (compare a b) 0))
(defn hour-ago? [t] (gte (an-hour-before (time/now)) t))   "at least an hour ago"  

;; it would be my preference to generate these, but it's
;; simpler for most people to read this way.

;; these named constants and functions are designed to
;; evoke what matters about them, and about their relationships
;; to each other, compared to hard-coded values.
(def a-timestamp (time/date-time 2015 1 1))
(def a-customer {:uuid "aeiou"})
(defn an-order-from [customer] {:who customer :when a-timestamp :what "bought 3 things"})
(defn a-click-preceding [order] {:who (:who order) :when (an-hour-before (:when order))})
(defn a-timestamp-preceding [date] (time/minus date (time/minutes 5)))

(defn conversion-period-for [order] (time/minus (:when order) conversion-period))

(def NotLessThanAnHourAgo (s/named (s/both org.joda.time.DateTime (s/pred hour-ago?)) "at least an hour ago"))

(deftest basic-conversion-test
  (testing "an order preceded by an ad click is a conversion"
    (let [customer a-customer
          order (an-order-from customer)
          click (a-click-preceding order)
          starting-date (a-timestamp-preceding (:when order)) 
          expected-conversion {:click click :outcome order}]
      (with-redefs [fetch-orders (s/fn [s :- (s/eq starting-date)
                                        e :- NotLessThanAnHourAgo]
                                   [order])
                    fetch-clicks (s/fn [start :- (s/eq (conversion-period-for order))
                                        end :- (s/eq (:when order))
                                        c :- (s/eq customer)]
                                   [click])]
        (let [result (calculate-conversions-since starting-date)]
          (is (= [expected-conversion] result)))))))

