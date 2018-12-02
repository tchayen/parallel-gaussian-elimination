(ns clotw)
(require '[clojure.string :as str])

(defn parse-line [line] (map read-string (str/split line #" ")))

(defn read-input [path]
  (let [lines (->> path (clojure.java.io/reader) line-seq)
        n (->> lines first Integer/parseInt)
        a (->> lines (drop 1) (map parse-line) (take n) to-array-2d)
        r (->> lines last parse-line to-array)]
    {:n n :a a :r r}))

(defn stringify [a] (-> a seq prn-str (str/replace #"[\(\)]" "")))

(defn output [data]
  (spit
   "output.txt"
   (str
    (prn-str (data :n))
    (->> (data :a) seq (map stringify) (str/join))
    (stringify (data :r)))))

(defn scalar [i j a s] (aset s i (/ (aget a i j) (aget a j j))))

(defn subtraction-row [i j a s r]
  (go (alength a) (fn [k] (fn [] (subtraction a i j k s))) pass-all)
  (aset r j (- (aget r j) (* s (aget r i)))))

(defn subtraction [a i j k s]
  (aset a j k (- (aget a j k) (* s (aget a i k)))))

(defn division [i a r] (aset r i (/ (aget r i) (aget a i i))) (aset a i i 1))

(defn go [n fu fi]
  (let [threads (->> (range 0 n) (filter fi) (map fu) (map #(Thread. %)))]
    (run! #(.start %) threads)
    (run! #(.join %) threads)))

(defn pass-all [_] true)

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
        (fn [i] (fn [] (subtraction-row j i (data :a) (aget scalars i) (data :r))))
        (fn [i] (not= i j))))
    (go (data :n) (fn [i] (fn [] (division i (data :a) (data :r)))) pass-all)
    (output data)))
