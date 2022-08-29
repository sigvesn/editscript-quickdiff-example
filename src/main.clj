(ns main
  (:require [clojure.edn :as edn]
            [clojure.java.shell :as shell]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [editscript.core :as editscript]))

(defn write-diff [diff path]
  (with-open [w (io/writer path)]
    (.write w (json/write-str (editscript/get-edits diff)))
    (println (:out (shell/sh "du" "-sh" path)))))

(defn read-edn [path]
  (with-open [r (io/reader path)]
    (edn/read (java.io.PushbackReader. r))))

(def old (read-edn "old.edn"))
(def new (read-edn "new.edn"))

(println "Running the vanilla diff algorithm")
(let [diff (time (editscript/diff old
                                  new))]
  (write-diff diff "out/diff.json"))

(def old-non-active (mapv #(dissoc % :active) old))
(println "Running the :quick diff algorithm, but without the :active field in 'old'")
(let [diff (time (editscript/diff old-non-active
                                  new
                                  {:algo :quick
                                   :str-diff :none}))]
  (write-diff diff "out/diff_fields.json"))

(println "Running the :quick diff algorithm with line str-diff, but without the :active field in 'old'")
(let [diff (time (editscript/diff old-non-active
                                  new
                                  {:algo :quick
                                   :str-diff :line}))]
  (write-diff diff "out/diff_fields_line.json"))

(println "Running the :quick diff algorithm with word str-diff, but without the :active field in 'old'")
(let [diff (time (editscript/diff old-non-active
                                  new
                                  {:algo :quick
                                   :str-diff :word}))]
  (write-diff diff "out/diff_fields_word.json"))

(println "Running the :quick diff algorithm with char str-diff, but without the :active field in 'old'")
(let [diff (time (editscript/diff old-non-active
                                  new
                                  {:algo :quick
                                   :str-diff :character}))]
  (write-diff diff "out/diff_fields_char.json"))



(println "Running the :quick diff algorithm without str-diff")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :str-diff :none}))]
  (write-diff diff "out/diff_quick_no_str.json"))

(println "Running the :quick diff algorithm without str-diff on sets")
(def old-set (read-edn "old.edn"))
(def new-set (read-edn "new.edn"))
(let [diff (time (editscript/diff old-set
                                  new-set
                                  {:algo :quick
                                   :str-diff :none}))]
  (write-diff diff "out/diff_quick_no_str_set.json"))

(println "Running the :quick diff algorithm with line str diff")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :str-diff :line}))]
  (write-diff diff "out/diff_quick_line.json"))

(println "Running the :quick diff algorithm with word str diff")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :str-diff :word}))]
  (write-diff diff "out/diff_quick_word.json"))

(println "Running the :quick diff algorithm with char str diff and timeout of 1sec")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :vec-timeout 60
                                   :str-diff :character}))]
  (write-diff diff "out/diff_quick_char_timeout.json"))

(println "Running the :quick diff algorithm with char str diff")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :str-diff :character}))]
  (write-diff diff "out/diff_quick_char.json"))
