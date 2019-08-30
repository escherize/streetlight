(ns streetlight.core
  (:require [clojure.walk :as walk]
            [clojure.set :as set]))

(defn paths
  "Returns a sequence of [key and index path -> value] pairs for a given datastructure."
  [m]
  (letfn [(->map [m-or-coll] (cond
                               (map? m-or-coll)
                               m-or-coll

                               (coll? m-or-coll)
                               (into {} (map-indexed (fn [i x] [i x]) m-or-coll))

                               :else ::m-or-coll-not-map-or-collection))
          (children [node]
            (let [v (->map (get-in m node))]
              (if (map? v)
                (map (fn [x] (conj node x)) (keys v))
                [])))
          (branch? [node] (-> (children node) seq boolean))]
    (->> m
         ->map
         keys
         (map vector)
         (mapcat #(tree-seq branch? children %))
         (vec))))

(defn path-search
  [m path-filter-fn]
  (->> m
       paths
       (filter path-filter-fn)
       (map (juxt identity #(get-in m %)))
       (remove (fn [[_path value]] (or (coll? value) (map? value))))
       (sort-by (comp count first))
       vec))

(defn path-search-demo [m]
  (do (path-search m println) nil))

(defn search-regex
  "Finds all paths and values that match re-term as a component.

  This should be used when you're not quite sure what you are looking for. "
  [m re-term]
  (path-search m #(some (fn [node] (re-matches re-term (str node))) %)))

(defn search-keys
  "Finds all paths and values that have ks on the path.

  This should be used when you're more sure about what you are looking for."
  [m & ks]
  (path-search m #(some (set ks) %)))

(defn p "Prints newline delimited collections." [& m]
  (doseq [item m]
    (doseq [line item] (println line))
    (println "")))

(defn explode
  "Returns the set of all pieces of m"
  [m]
  (let [*out (atom #{})]
    (walk/postwalk
     (fn [x]
       (swap! *out conj x)
       x)
     m)
    @*out))

(defn- remove-duplicate-vectors
  "[{:a 1} [:a 1] :not-touched]
   ;;; => [[:a 1] :not-touched]"
  [items]
  (let [item-set (set items)
        kill-set (set (filter (fn [x]
                                (and (coll? x)
                                     (vector? x)
                                     (= 2 (count x))
                                     (item-set (apply hash-map x))))
                              items))]
    (remove kill-set items)))

(defn co-relate
  ([m1 m2] (co-relate m1 m2 vector?))
  ([m1 m2 remove-fn]
   (->> (set/intersection (explode m1) (explode m2))
        (remove-duplicate-vectors)
        (sort-by (comp count str))
        vec)))
