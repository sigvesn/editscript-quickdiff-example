(ns main
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [editscript.core :as editscript]))

(defn write-diff [diff path]
  (with-open [w (io/writer path)]
    (.write w (json/write-str (editscript/get-edits diff)))))

(defn read-edn [path]
  (with-open [r (io/reader path)]
    (edn/read (java.io.PushbackReader. r))))

(def old (read-edn "old.edn"))
(def new (read-edn "new.edn"))

(println "Running the vanilla diff algorithm")
(let [diff (time (editscript/diff old
                                  new
                                  {;:algo :quick
                                   :str-diff? true}))]
  (write-diff diff "out/diff.json"))

(println "Running the :quick diff algorithm, but without the :active field in 'old'")
(let [diff (time (editscript/diff (map #(dissoc % :active) old)
                                  new
                                  {:algo :quick
                                   :str-diff? true}))]
  (write-diff diff "out/diff_fields.json"))

(println "Running the :quick diff algorithm")
(let [diff (time (editscript/diff old
                                  new
                                  {:algo :quick
                                   :str-diff? true}))]
  (write-diff diff "out/diff_quick.json"))
