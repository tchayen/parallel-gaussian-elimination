(ns clotw)
(require '[clojure.string :as str])

(defn read-input [path]
  (let [parse-line (fn [line] (map read-string (str/split line #" ")))
        lines (->> path (clojure.java.io/reader) line-seq)
        n (->> lines first Integer/parseInt)
        a (->> lines (drop 1) (map parse-line) (take n) to-array-2d)
        r (->> lines last parse-line to-array)]
    {:n n :a a :r r}))

(defn scalar [i j a s] (aset s i (/ (aget a i j) (aget a j j))))

(defn zeroes [i j a s]
  (dotimes [k (alength a)]
    (aset a j k (- (aget a j k) (* (get s i) (aget a i k))))))

(defn division [i a r]
  (aset r i (/ (aget r i) (aget a i i)))
  (aset a i i 1))

(defn -main [& args]
  (let [data (read-input "input.txt")
        scalars (->> (data :n) (range 0) to-array)]
    (dotimes [j (data :n)]
      (let [threads
            (->>
              (range 0 (data :n))
              (filter #(not= % j))
              (map #(fn [] (scalar % j (data :a) scalars)))
              (map #(Thread. %)))]
        ; (doseq [i (seq threads)] (i))
        (run! #(.start %) threads)
        (run! #(.join %) threads))

      (let [threads
            (->>
              (range 0 (data :n))
              (filter #(not= % j))
              (map #(fn [] (zeroes % j (data :a) scalars)))
              (map #(Thread. %)))]
        (run! #(.start %) threads)
        (run! #(.join %) threads))

      (let [threads
            (->>
              (range 0 (data :n))
              (filter #(not= % j))
              (map #(fn [] (division % (data :a) (data :r))))
              (map #(Thread. %)))]
        (run! #(.start %) threads)
        (run! #(.join %) threads))
    )

        (-> scalars seq println)
        (->> (data :a) seq (map seq) println)
        (-> (data :r) seq println)
        ))
