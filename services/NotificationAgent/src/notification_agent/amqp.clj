(ns notification-agent.amqp
  (:use [slingshot.slingshot :only [try+]])
  (:require [cheshire.core :as cheshire]
            [clojure.tools.logging :as log]
            [langohr.basic :as lhb]
            [langohr.channel :as lch]
            [langohr.core :as rmq]
            [langohr.exchange :as lhe]
            [notification-agent.config :as cfg]
            [notification-agent.db :as db]))

(defn- connect
  []
  (rmq/connect {:host     (cfg/amqp-host)
                :port     (cfg/amqp-port)
                :username (cfg/amqp-user)
                :password (cfg/amqp-password)}))

(defn- declare-exchange
  [ch]
  (lhe/declare ch (cfg/amqp-exchange-name) "direct"
               {:durable     (cfg/amqp-exchange-durable)
                :auto-delete (cfg/amqp-exchange-auto-delete)})
  (cfg/amqp-exchange-name))

(defn- publish
  [routing-key body]
  (log/warn "publishing a message: " routing-key)
  (with-open [conn (connect)
              ch   (lch/open conn)]
    (let [exchange (declare-exchange ch)]
      (lhb/publish ch exchange routing-key (cheshire/encode body) {:content-type "application/json"}))))

(defn publish-msg
  [user msg]
  (try+
   (publish (str "notification." user)
            {:message msg
             :total   (db/count-matching-messages user {:seen false})})
   (catch Object _
     (log/error (:throwable &throw-context) "unable to publish message:" (cheshire/encode msg)))))

(defn publish-system-msg
  [msg]
  (try+
   (publish "system_message" msg)
   (catch Object _
     (log/error (:throwable &throw-context) "unable to publish system message:" (cheshire/encode msg)))))
