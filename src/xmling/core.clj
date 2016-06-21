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
  [file]
  (let [input (clojure.java.io/reader (clojure.java.io/resource file))
        root (zip/xml-zip (xml/parse input))
        xmlns (xml/find-xmlns root)
        source (zip-xml/xml1-> root clojure.data.zip/descendants)
        edited-source (edit-source source)]
    (println xmlns)
    root))

;; note this forgets its root namespace. xmlns is not present in the output...
(xml/indent-str (read-xliff "test.xliff"))
;; emit-str would fail for this, despite only adding namespaces to xml:lang
(println (xml/indent-str (read-xliff "test-bad.xliff")))


(let [input (clojure.java.io/reader (clojure.java.io/resource "test.xliff"))
      root (zip/xml-zip (xml/parse input))
      xmlns (xml/find-xmlns root)
      source (zip-xml/xml1-> root
                            clojure.data.zip/descendants
                            (zip-xml/attr= :lang "EN-US"))
      edited-source (edit-source source)
      new-root (zip/root edited-source)]
  (println (xml/indent-str new-root))
    true)
