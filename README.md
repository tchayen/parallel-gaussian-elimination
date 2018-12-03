# parallel-gaussian-elimination

Tiny Clojure implementation of parallelized Gaussian elimination

## Installation

Get Clojure and `lein`.
Run `lein repl` in this directory and call:

```clojure
(load-file "main.clj")
(clotw/start)
```

## Running

Provide `input.txt` file containing matrix in the following format:

```
3
0.1 0.2 0.3
0.4 0.5 0.6
0.7 0.8 0.9
1.0 1.1 1.2
```

It is the size of the matrix, its values and X column.

Output comes in a matching format with matrix and X updated.

```
3
1 0.0 0.0
0.0 1 0.0
0.0 0.0 1
-5.333333333333332 1.6666666666666665 4.0
```
