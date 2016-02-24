(ns terrain.clients.ezid
  (:use [ring.util.http-response :only [charset]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [cemerick.url :as curl]
            [clj-http.client :as http]
            [clojure.string :as string]
            [terrain.util.config :as config]))

(defn- auth-params
  []
  [(config/ezid-username) (config/ezid-password)])

(defn- ezid-uri
  [& components]
  (str (apply curl/url (config/ezid-base-url) components)))

(defn- anvl-escape
  [value]
  (-> value
      (string/replace #"%"  "%25")
      (string/replace #"\n" "%0A")
      (string/replace #"\r" "%0D")
      (string/replace #":"  "%3A")))

(defn- anvl-unescape
  [value]
  (-> value
      (string/replace #"%3A" ":")
      (string/replace #"%0D" "\r")
      (string/replace #"%0A" "\n")
      (string/replace #"%25" "%")))

(defn- anvl-decode
  "Decodes an ANVL string into a map."
  [anvl]
  (let [anvl-lines (when (string? anvl) (->> anvl string/split-lines (remove string/blank?)))
        keywordize-fn #(update-in % [0] keyword)]
    (if (and anvl-lines (every? #(.contains % ":") anvl-lines))
      (into {}
        (map (fn [kv] (keywordize-fn (mapv anvl-unescape (string/split kv #": *" 2))))
             anvl-lines))
      anvl)))

(defn- anvl-encode
  "Encodes the given metadata map as an ANVL string."
  [metadata]
  (->> metadata
       (map (fn [[k v]] (str (anvl-escape (name k)) ": " (anvl-escape v))))
       (string/join "\n")))

(defn- ezid-post
  "Posts a request to the EZID API and parses its ANVL response."
  [body & uri-parts]
  (->> (charset {:body       (anvl-encode body)
                 :basic-auth (auth-params)}
                "utf-8")
       (http/post (apply ezid-uri uri-parts))
       :body
       anvl-decode))

(defn- format-anvl-error
  "Adds the ANVL message to the given error map."
  [error-map anvl]
  (let [decoded-anvl (anvl-decode anvl)]
    (if (map? decoded-anvl)
      (merge error-map decoded-anvl)
      (assoc error-map :error decoded-anvl))))

(defn mint-id
  "Mints a new permanent ID under the given shoulder with the given metadata.
   http://ezid.cdlib.org/doc/apidoc.html#operation-mint-identifier"
  [shoulder metadata]
  (try+
    (let [response (ezid-post metadata "shoulder" shoulder)
          keywordize-fn #(update-in % [0] (comp keyword string/upper-case))]
      (when-not (and (map? response) (contains? response :success))
        (throw+ {:type :clojure-commons.exception/request-failed
                 :error "Could not parse EZID response."
                 :response response}))
      (into {}
        (mapv #(keywordize-fn (string/split % #" *: *"))
              (string/split (:success response) #" *\| *"))))
    (catch [:status 400] {:keys [body] :as bad-request}
      (throw+ (format-anvl-error {:type :clojure-commons.exception/bad-request} body)))))
