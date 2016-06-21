(ns xmling.core
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn edit-source
  "do some edits! returns the changed root"
  [source]
  (zip/edit source #(assoc-in % [:content] (str "edited..." (zip-xml/text source)))))

(defn tree-edit
  "Take a zipper, a function that matches a pattern in the tree,
   and a function that edits the current location in the tree.  Examine the tree
   nodes in depth-first order, determine whether the matcher matches, and if so
   apply the editor. Taken from https://ravi.pckl.me/short/functional-xml-editing-using-zippers-in-clojure/"
  [zipper matcher editor]
  (loop [loc zipper]
    (if (zip/end? loc)
      (zip/root loc)
      (if-let [matcher-result (matcher loc)]
        (let [new-loc (zip/edit loc editor)]
          (if (not (= (zip/node new-loc) (zip/node loc)))
            (recur (zip/next new-loc))))
        (recur (zip/next loc))))))

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
      edited-sources (tree-edit
                      root
                      (fn [loc] (let [tag (:tag (zip/node loc))] (= tag (javax.xml.namespace.QName. "urn:oasis:names:tc:xliff:document:1.2" "source"))))
                      (fn [loc]
                        (let [old-content (first (:content loc))] ;;this isn't perfect but it works...
                          (println old-content)
                          (assoc-in loc [:content] (str "edited..." old-content)))))
      new-root edited-sources]
  (println (xml/indent-str edited-sources))
  true)
