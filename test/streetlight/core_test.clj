(ns streetlight.core-test
  (:require [clojure.test :refer :all]
            [streetlight.core :as sl]))

(def map-a {:a {:b {:c {:d 1}}}})
(def map-b {:x {:y {:c {:z 1}}}})

(deftest paths-test
  (testing "searching paths"
    (is (= [[:a] [:a :b] [:a :b :c] [:a :b :c :d]]
           (sl/paths map-a)))
    (is (= [[:x] [:x :y] [:x :y :c] [:x :y :c :z]]
           (sl/paths map-b)))
    (is (= [[0] [1] [2]]
           (sl/paths [:one :two :three]))
        "Vectors get indexed into.")
    (is (= [[0] [1] [2]]
           (sl/paths '(:one :two :three)))
        "Lists get indexed into.")))

(deftest search-regex-test
  (is (= [[[:a :b :c :d] 1]] (sl/search-regex map-a #".*a.*")))
  (is (= [] (sl/search-regex map-b #".*a.*")))
  (is (= [[[:a :b :c :d] 1]] (sl/search-regex map-a #".*c.*")))
  (is (= [[[:x :y :c :z] 1]] (sl/search-regex map-b #".*c.*"))))

(deftest search-keys-test
  (is (= [[[:a :b :c :d] 1]] (sl/search-keys map-a :a)))
  (is (= [[[:a :b :c :d] 1]] (sl/search-keys map-a :b)))
  (is (= [[[:a :b :c :d] 1]] (sl/search-keys map-a :c)))
  (is (= [[[:a :b :c :d] 1]] (sl/search-keys map-a :d)))
  (is (= [[[:a :b :c :d] 1]] (sl/search-keys map-a :a :b :c))))

(deftest co-relate-test
  (is (= [1 :c :d {:d 1} {:c {:d 1}}]
         (sl/co-relate
          {:a {:b {:c {:d 1}}}}
          {:A {:B {:c {:d 1}}}}))))
