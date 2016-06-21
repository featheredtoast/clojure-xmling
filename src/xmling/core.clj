(ns xmling.core
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn edit-source
  "do some edits!"
  [source]
  (zip/edit source #(assoc-in % [:content] (str "edited..." (zip-xml/text source)))))

(defn ns-tag=
  "Returns a query predicate that matches a node when its is a tag
  named tagname. Works against qname'd tags that were introduced in data.xml 0.1.0-beta"
  [tagname]
  (fn [loc]
    (when-let [node-tag (:tag (zip/node loc))]
      (or (= tagname (.getLocalPart (:tag (zip/node loc))))
          (filter #(and (zip/branch? %) (= tagname (.getLocalPart (:tag (zip/node %)))))
                  (clojure.data.zip/children-auto loc))))))

(let [input (clojure.java.io/reader (clojure.java.io/resource "test.xliff"))
      root (zip/xml-zip (xml/parse input))
      xmlns (xml/find-xmlns root)
      source (zip-xml/xml1-> root
                             clojure.data.zip/descendants
                             (zip-xml/tag= (javax.xml.namespace.QName. "urn:oasis:names:tc:xliff:document:1.2" "source")))
      edited-source (edit-source source)
      new-root (zip/root edited-source)]
  (println (xml/indent-str new-root))
  true)
