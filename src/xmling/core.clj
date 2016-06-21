(ns xmling.core
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn get-source-text
  "given a source element, get its stuff?"
  [e]
  (zip-xml/text e))
(defn get-all-sources
  "get all source elements"
  [xml-root]
  (zip-xml/xml-> xml-root
                 clojure.data.zip/descendants
                 :source))
(defn edit-source
  "do some edits!"
  [source]
  (zip/edit source #(assoc-in % [:content] (str "edited..." (zip-xml/text source)))))
(defn read-xliff
  "Reads xliff!"
  []
  (let [input (clojure.java.io/reader (clojure.java.io/resource "test.xliff"))
        root (zip/xml-zip (xml/parse input))
        sources (get-all-sources root)
        edited-source (edit-source (first sources))
        new-root (zip/root edited-source)]
    new-root))

(xml/emit-str (read-xliff))
