# xmling

An attempt to see what it's like to parse an xml document in clojure

[data.xml](https://github.com/clojure/data.xml)
[data.zip](https://clojure.github.io/data.zip/)
[clojure.zip](https://clojure.github.io/clojure/clojure.zip-api.html)

inspired by [this post](http://clojure-doc.org/articles/tutorials/parsing_xml_with_zippers.html)

At first glance, it looks and feels as good as a linq query. However, I am having trouble with namespaces.

Namespace issues come about because between data.xml v 0.0.8 and 0.1.0, :node changes from a string to a [qname](http://docs.oracle.com/javase/7/docs/api/javax/xml/namespace/QName.html). This complicates finding nodes

## License

Copyright © 2016

Distributed under the Eclipse Public License version 1.0
