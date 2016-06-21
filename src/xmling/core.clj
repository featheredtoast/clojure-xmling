(ns xmling.core
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn get-source-element
  "given a source element, get its stuff?"
  [e]
  (zip-xml/text e))
(defn read-xliff
  "Reads xliff!"
  []
  (let [input (clojure.java.io/reader (clojure.java.io/resource "test.xliff"))
        root (zip/xml-zip (xml/parse input))
        sources (mapv get-source-element
                      (zip-xml/xml-> root
                                      clojure.data.zip/descendants
                                      :source))]
    sources))

(read-xliff)
