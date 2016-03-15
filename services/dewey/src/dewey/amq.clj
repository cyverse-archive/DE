(ns dewey.amq
  "This library mananges the connection to the AMQP queue."
  (:use [slingshot.slingshot :only [try+]])
  (:require [clojure.tools.logging :as log]
            [service-logging.thread-context :as tc]
            [cheshire.core :as json]
            [langohr.basic :as lb]
            [langohr.channel :as lch]
            [langohr.core :as rmq]
            [langohr.consumers :as lc]
            [langohr.exchange :as le]
            [langohr.queue :as lq]))


(defn- mk-handler
  [consume]
  (fn [channel {:keys [routing-key delivery-tag redelivery?]} ^bytes payload]
    (tc/with-logging-context {:amqp-delivery-tag delivery-tag}
      (try+
        (consume routing-key (json/parse-string (String. payload "UTF-8") true))
        (lb/ack channel delivery-tag)
        (catch Object _
          (lb/reject channel delivery-tag (not redelivery?))
          (log/error (:throwable &throw-context) (str "MESSAGE HANDLING ERROR, redelivery: " redelivery?)))))))


(defn- consume
  [connection queue exchange-name exchange-durable exchange-auto-delete qos topics delivery-fn]
  (let [channel  (lch/open connection)
        consumer (lc/create-default channel
                   {:handle-consume-ok-fn (fn [_] (log/info "Registered with AMQP broker"))
                    :handle-delivery-fn   delivery-fn
                    :handle-cancel-fn     (fn [_] (log/info "AMQP broker registration canceled")
                                                  (Thread/sleep 1000)
                                                  (consume connection
                                                           queue
                                                           exchange-name
                                                           exchange-durable
                                                           exchange-auto-delete
                                                           topics
                                                           delivery-fn))})]
    (lb/qos channel qos)
    (le/topic channel exchange-name {:durable exchange-durable :auto-delete exchange-auto-delete})
    (lq/declare channel queue {:durable true :auto-delete false :exclusive false})
    (doseq [topic topics] (lq/bind channel queue exchange-name {:routing-key topic}))
    (lb/consume channel queue consumer {:auto-ack false})))


(defn attach-to-exchange
  "Attaches a consumer function to a given queue on a given AMQP exchange. It listens only for the
   provided topics. If no topics are provided, it listens for all topics. It is assumed that the
   messages in the queue are JSON documents.

   Parameters:
     host                 - the host of the AMQP broker
     port                 - the port the AMQP broker listends on
     user                 - the AMQP user
     password             - the AMQP user password
     exchange-name        - the name of the exchange
     exchange-durable     - a flag indicating whether or not the exchange preserves messages
     exchange-auto-delete - a flag indicating whether or not the exchange is deleted when all queues
                            have been dettached
     qos                  - a number of messages to allow to be sent to this client without acknowledgement
     consumer-fn          - the function that will receive the JSON document
     topics               - Optionally, a list of topics to listen for

   Throws:
     It will throw an exception if it fails to connect to the AMQP broker, setup the exchange, or
     setup the queue."
  [host port user password queue-name exchange-name exchange-durable exchange-auto-delete qos consumer-fn & topics]
  (consume (rmq/connect {:host                  host
                         :port                  port
                         :username              user
                         :password              password
                         :automatically-recover true})
           queue-name
           exchange-name
           exchange-durable
           exchange-auto-delete
           qos
           (if (empty? topics) "#" topics)
           (mk-handler consumer-fn)))
