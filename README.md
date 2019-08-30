# Streetlight

A Clojure library designed to throw light onto deep and dark datastructures

> A policeman sees a drunk man searching for something under a streetlight and asks what the drunk has lost. He says he lost his keys and they both look under the streetlight together. After a few minutes the policeman asks if he is sure he lost them here, and the drunk replies, no, and that he lost them in the park. The policeman asks why he is searching here, and the drunk replies, "this is where the light is".
- ( https://en.wikipedia.org/wiki/Streetlight_effect )

![streetlight](resources/streetlight.jpg)

## Usage

Consider this map:

``` clojure
{:a {:b {:c {:d 1}}}}
```

It's not big enough to be painful to search through, but some are. :)

#### Path Searching

##### search with keywords

If you know what keys you want to find, you can use `search-keys`:

``` clojure
(search-keys {:a {:b {:c {:d 1}}}} :c)
;;=> [[[:a :b :c :d] 1]]
```

This returns the path containing your keys and the value at that path.

##### search with a regex

If you are unsure exactly what you're looking for, use `search-regex`:

``` clojure
(search-regex {:a {:b {:c {:d 1}}}} #".*d.*")
;;=> [[[:a :b :c :d] 1]]
```

### co-relate

Now let's consider two maps, these are small and easy to read but should give you an idea. We can find where they 'overlap' by using co-relate.


``` clojure
(co-relate
 {:a {:b {:c {:d 1}}}}
 {:A {:B {:c {:d 1}}}})

;; => [1 :c :d {:d 1} {:c {:d 1}}]
```

This lists all the shared shapes of our two maps!

I have used this to see how two big maps relate to one another.

## License

Copyright © 2019

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
