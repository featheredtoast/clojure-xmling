(ns xmling.core
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

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

(defn search-by-tag
  [namespace name]
  (fn [loc]
    (let [tag (:tag (zip/node loc))]
      (= tag (javax.xml.namespace.QName. namespace name)))))

(defn search-by-tag-local
  [name]
  (fn [loc]
    (when-let [tag (:tag (zip/node loc))]
      (= (.getLocalPart tag) name))))

(defn edit-sources
  [loc]
  (println loc)
  (let [old-content (zip-xml/text (zip/xml-zip loc))] ;;this isn't perfect but it works. It strips out all tags, however.
    (println old-content)
    (assoc-in loc [:content] (str "edited..." old-content))))

(defn ns-tag=
  "Returns a query predicate that matches a node when its is a tag
  named tagname. Works against qname'd tags that were introduced in data.xml 0.1.0-beta"
  [tagname]
  (fn [loc]
    (when-let [node-tag (:tag (zip/node loc))]
      (or (= tagname (.getLocalPart node-tag))
          (filter #(and (zip/branch? %) (= tagname (.getLocalPart (:tag (zip/node %)))))
                  (clojure.data.zip/children-auto loc))))))

(let [input (clojure.java.io/reader (clojure.java.io/resource "test.xliff"))
      root (zip/xml-zip (xml/parse input))
      edited-sources (tree-edit
                      root
                      (search-by-tag-local "source")
                      edit-sources)
      new-root edited-sources]
  (println (xml/indent-str edited-sources))
  true)
