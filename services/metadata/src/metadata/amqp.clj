(ns metadata.amqp
  (:require [metadata.util.config :as config]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.exchange :as le]
            [langohr.basic :as lb]
            [cheshire.core :as json]))

(defn publish-metadata-update
  [user id]
  (let [conn  (rmq/connect {:host                (config/amqp-host)
                            :port                  (config/amqp-port)
                            :username              (config/amqp-user)
                            :password              (config/amqp-pass)})
        ch    (lch/open conn)
        ename (config/amqp-exchange)]
    (le/declare ch ename "direct" {:durable true})
    (lb/publish ch ename "metadata.update" (json/encode {:entity id :author user}) {:content-type "application/json" :persistent 1})
    (rmq/close ch)
    (rmq/close conn)))
