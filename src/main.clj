(ns main
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [editscript.core :as editscript]))

(defn read-edn [path]
  (with-open [r (io/reader path)]
    (edn/read (java.io.PushbackReader. r))))

(def old (read-edn "old.edn"))
(def new (read-edn "new.edn"))

(println "Running the vanilla diff algorithm")
(time (some? (editscript/diff old
                              new
                              {;:algo :quick
                               :str-diff? true})))

(println "Running the :quick diff algorithm, but without the :active field in 'old'")
(time (some? (editscript/diff (map #(dissoc % :active) old)
                              new
                              {:algo :quick
                               :str-diff? true})))

(println "Running the :quick diff algorithm")
(time (some? (editscript/diff old
                              new
                              {:algo :quick
                               :str-diff? true})))
