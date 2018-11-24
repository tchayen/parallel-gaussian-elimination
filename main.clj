(require '[clojure.string :as str])

(defn read-input [path]
  (let [parse-line (fn [line] (str/split line #" "))
        lines (->>
                path
                (clojure.java.io/reader)
                line-seq)
        n (->>
            lines
            first
            Integer/parseInt)
        a (->>
            lines
            (drop 1)
            (map parse-line)
            (take n)
            (into []))
        r (->>
            lines
            last
            parse-line
            (into []))]
    (println {:n n :a a :r r})))

; (defn scalar [i j a]
;   )

; (defn zeroes [i j a s]
;   )

; (defn division [i a r]
;   )

; (let [func #(println "t")
;       threads (repeatedly 5 #(Thread. func))]
;   (run! #(.start %) threads)
;   (println "running...")
;   (run! #(.join %) threads)
;   (println "finish"))



(let [n (Integer/parseInt (read-line))]
  (dotimes [i n]
    (read-line)))

    (with-open [rdr (clojure.java.io/reader "input.txt")] (reduce conj [] (line-seq rdr)))