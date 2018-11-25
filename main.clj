(ns clotw)
(require '[clojure.string :as str])

(defn read-input [path]
  (let [parse-line (fn [line] (map read-string (str/split line #" ")))
        lines (->> path (clojure.java.io/reader) line-seq)
        n (->> lines first Integer/parseInt)
        a (->> lines (drop 1) (map parse-line) (take n) to-array-2d)
        r (->> lines last parse-line to-array)]
    {:n n :a a :r r}))

(defn stringify-array [a] (-> a seq prn-str (str/replace #"[\(\)]" "")))

(defn output [data]
  (spit
    "output.txt"
    (str
      (prn-str (data :n))
      (->> (data :a) seq (map stringify-array) (str/join))
      (stringify-array (data :r)))))

(defn scalar [i j a s] (aset s i (/ (aget a i j) (aget a j j))))

(defn zeroes [i j a s r]
  (dotimes [k (alength a)]
    (aset a j k (- (aget a j k) (* s (aget a i k)))))
  (aset r j (- (aget r j) (* s (aget r i)))))

(defn division [i a r] (aset r i (/ (aget r i) (aget a i i))) (aset a i i 1))

(defn go [n fu fi]
  (let [threads
        (->>
          (range 0 n)
          (filter fi)
          (map fu)
          (map #(Thread. %)))]
    (run! #(.start %) threads)
    (run! #(.join %) threads)))

(defn -main [& args]
  (let [data (read-input "input.txt")
        scalars (->> (data :n) (range 0) to-array)]
    (dotimes [j (data :n)]
      (go
        (data :n)
        (fn [i] (fn [] (scalar i j (data :a) scalars)))
        (fn [i] (not= i j)))
      (go
        (data :n)
        (fn [i] (fn [] (zeroes j i (data :a) (aget scalars i) (data :r))))
        (fn [i] (not= i j))))
    (go (data :n) (fn [i] (fn [] (division i (data :a) (data :r)))) (fn [i] true))
    (output data)))
