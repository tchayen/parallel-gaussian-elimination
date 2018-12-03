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

(defn run-parallel [fns]
  (let [threads (map #(Thread. %) fns)]
    (run! #(.start %) threads)
    (run! #(.join %) threads)))

(defn scalar [i j a s] (aset s i (/ (aget a i j) (aget a j j))))

(defn subtraction [a i j k s]
  (aset a j k (- (aget a j k) (* s (aget a i k)))))

(defn subtraction-row [i j a s r]
  (->>
    (range 0 (alength a))
    (map (fn [k] (fn [] (subtraction a i j k s))))
    run-parallel
    )
  (aset r j (- (aget r j) (* s (aget r i)))))

(defn division [i a r] (aset r i (/ (aget r i) (aget a i i))) (aset a i i 1))

(defn runn [& args]
  (let [data (read-input "input.txt")
        scalars (->> (data :n) (range 0) to-array)
        n (data :n) a (data :a) r (data :r)]
    (dotimes [j (data :n)]
      (->>
        (range 0 n)
        (filter (fn [i] (not= i j)))
        (map (fn [i] (fn [] (scalar i j a scalars))))
        run-parallel)
      (->>
        (range 0 n)
        (filter (fn [i] (not= i j)))
        (map (fn [i] (fn [] (subtraction-row j i a (aget scalars i) r))))
        run-parallel))
    (->>
      (range 0 n)
      (map (fn [i] (fn [] (division i a r))))
      run-parallel)
    (output data)))
