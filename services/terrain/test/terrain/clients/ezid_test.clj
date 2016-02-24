(ns terrain.clients.ezid-test
  (:use [clojure.test]
        [terrain.clients.ezid]))

;; Re-def private functions so they can be tested in this namespace.
(def anvl-escape   #'terrain.clients.ezid/anvl-escape)
(def anvl-unescape #'terrain.clients.ezid/anvl-unescape)
(def anvl-decode   #'terrain.clients.ezid/anvl-decode)
(def anvl-encode   #'terrain.clients.ezid/anvl-encode)

(deftest anvl-escape-test
  (is (= (anvl-escape "Test % escape") "Test %25 escape")
      "anvl-escape '%' character")
  (is (= (anvl-escape "Test \n escape") "Test %0A escape")
      "anvl-escape '\\n' character")
  (is (= (anvl-escape "Test \r escape") "Test %0D escape")
      "anvl-escape '\\r' character")
  (is (= (anvl-escape "Test : escape") "Test %3A escape")
      "anvl-escape ':' character")
  (is (= (anvl-escape "% This \n is \r a : Test") "%25 This %0A is %0D a %3A Test")
      "anvl-escape characters forwards")
  (is (= (anvl-escape ": This \r is \n a % Test") "%3A This %0D is %0A a %25 Test")
      "anvl-escape characters backwards")
  (is (= (anvl-escape "%test: This \r\n is a %25 Test\n") "%25test%3A This %0D%0A is a %2525 Test%0A")
      "anvl-escape mixed characters"))

(deftest anvl-unescape-test
  (is (= (anvl-unescape "Test %3A unescape") "Test : unescape")
      "anvl-unescape ':' character")
  (is (= (anvl-unescape "Test %0D unescape") "Test \r unescape")
      "anvl-unescape '\\r' character")
  (is (= (anvl-unescape "Test %0A unescape") "Test \n unescape")
      "anvl-unescape '\\n' character")
  (is (= (anvl-unescape "Test %25 unescape") "Test % unescape")
      "anvl-unescape '%' character")
  (is (= (anvl-unescape "%3A This %0D is %0A a %25 Test") ": This \r is \n a % Test")
      "anvl-unescape characters forwards")
  (is (= (anvl-unescape "%25 This %0A is %0D a %3A Test") "% This \n is \r a : Test")
      "anvl-unescape characters backwards")
  (is (= (anvl-unescape "%25test%3A This %0D%0A is a %2525 Test%0A") "%test: This \r\n is a %25 Test\n")
      "anvl-unescape mixed characters"))

(deftest anvl-decode-test
  (is (= (anvl-decode "") {})
      "anvl-decode Empty ANVL string")
  (is (= (anvl-decode "Not an ANVL string") "Not an ANVL string")
      "anvl-decode Not an ANVL string")
  (is (= (anvl-decode ["Not" "a string"]) ["Not" "a string"])
      "anvl-decode Not a string")
  (is (= (anvl-decode {:not "a string"}) {:not "a string"})
      "anvl-decode Not a string")
  (is (= (anvl-decode "some: simple string") {:some "simple string"})
      "anvl-decode 1 key with a simple string")
  (is (= (anvl-decode "test: %3A unescape") {:test ": unescape"})
      "anvl-decode 1 key with a ':' character")
  (is (= (anvl-decode "test: %0D unescape") {:test "\r unescape"})
      "anvl-decode 1 key with a '\\r' character")
  (is (= (anvl-decode "test: %0A unescape") {:test "\n unescape"})
      "anvl-decode 1 key with a '\\n' character")
  (is (= (anvl-decode "test: %25 unescape") {:test "% unescape"})
      "anvl-decode 1 key with a '%' character")
  (is (= (anvl-decode
          "test1: %25 unescape\ntest2: %0A unescape\ntest3: %0D unescape\ntest4: %3A unescape")
         {:test1 "% unescape"
          :test2 "\n unescape"
          :test3 "\r unescape"
          :test4 ": unescape"})
      "anvl-decode 4 keys, 1 escaped character each")
  (is (= (anvl-decode
"test1: %3A This %0D is %0A a %25 Test
test2: %25 This %0A is %0D a %3A Test
test3: %25test%3A This %0D%0A is a %2525 Test%0A")
         {:test1 ": This \r is \n a % Test"
          :test2 "% This \n is \r a : Test"
          :test3 "%test: This \r\n is a %25 Test\n"})
      "anvl-decode 3 keys, mixed escaped characters"))

(deftest anvl-encode-test
  (is (= (anvl-encode {}) "")
      "anvl-encode Empty ANVL string")
  (is (= (anvl-encode {:some "simple string"}) "some: simple string")
      "anvl-encode 1 key with a simple string")
  (is (= (anvl-encode {:test "% unescape"}) "test: %25 unescape")
      "anvl-encode 1 key with a '%' character")
  (is (= (anvl-encode {:test "\n unescape"}) "test: %0A unescape")
      "anvl-encode 1 key with a '\\n' character")
  (is (= (anvl-encode {:test "\r unescape"}) "test: %0D unescape")
      "anvl-encode 1 key with a '\\r' character")
  (is (= (anvl-encode {:test ": unescape"}) "test: %3A unescape")
      "anvl-encode 1 key with a ':' character")
  (is (= (anvl-encode
          {:test1 "% unescape"
           :test2 "\n unescape"
           :test3 "\r unescape"
           :test4 ": unescape"})
         "test1: %25 unescape\ntest2: %0A unescape\ntest3: %0D unescape\ntest4: %3A unescape")
      "anvl-encode 4 keys, 1 escaped character each")
  (is (= (anvl-encode
          {:test1 ": This \r is \n a % Test"
           :test2 "% This \n is \r a : Test"
           :test3 "%test: This \r\n is a %25 Test\n"})
"test1: %3A This %0D is %0A a %25 Test
test2: %25 This %0A is %0D a %3A Test
test3: %25test%3A This %0D%0A is a %2525 Test%0A")
      "anvl-encode 3 keys, mixed escaped characters"))
