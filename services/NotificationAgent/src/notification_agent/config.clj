(ns notification-agent.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.config :as cc]))

(def ^:private props
  "A ref for storing the configuration properties."
  (ref nil))

(def ^:private config-valid
  "A ref for storing a configuration validity flag."
  (ref true))

(def ^:private configs
  "A ref for storing the symbols used to get configuration settings."
  (ref []))

(cc/defprop-str db-driver-class
  "The name of the JDBC driver to use."
  [props config-valid configs]
  "notificationagent.db.driver" )

(cc/defprop-str db-subprotocol
  "The subprotocol to use when connecting to the database (e.g. postgresql)."
  [props config-valid configs]
  "notificationagent.db.subprotocol")

(cc/defprop-str db-host
  "The host name or IP address to use when connecting to the database."
  [props config-valid configs]
  "notificationagent.db.host")

(cc/defprop-str db-port
  "The port number to use when connecting to the database."
  [props config-valid configs]
  "notificationagent.db.port")

(cc/defprop-str db-name
  "The name of the database to connect to."
  [props config-valid configs]
  "notificationagent.db.name")

(cc/defprop-str db-user
  "The username to use when authenticating to the database."
  [props config-valid configs]
  "notificationagent.db.user")

(cc/defprop-str db-password
  "The password to use when authenticating to the database."
  [props config-valid configs]
  "notificationagent.db.password")

(cc/defprop-boolean email-enabled
  "True if e-mail notifications are enabled."
  [props config-valid configs]
  "notificationagent.enable-email")

(cc/defprop-str email-url
  "The URL used to connect to the mail service."
  [props config-valid configs]
  "notificationagent.email-url")

(cc/defprop-str amqp-host
  "The name of the host where the AMQP broker is running."
  [props config-valid configs]
  "notificationagent.amqp.host")

(cc/defprop-int amqp-port
  "The port to use when connecting to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.port")

(cc/defprop-str amqp-user
  "The username to use when authenticating to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.user")

(cc/defprop-str amqp-password
  "The password to use when authenticating to the AMQP broker."
  [props config-valid configs]
  "notificationagent.amqp.password")

(cc/defprop-str amqp-exchange-name
  "The name of the AMQP exchange."
  [props config-valid configs]
  "notificationagent.amqp.exchange.name")

(cc/defprop-boolean amqp-exchange-durable
  "Indicates whether or not the AMQP exchange should be declared as durable."
  [props config-valid configs]
  "notificationagent.amqp.exchange.durable")

(cc/defprop-boolean amqp-exchange-auto-delete
  "Indicates whether or not the AMQP exchange should be declared as auto-delete."
  [props config-valid configs]
  "notificationagent.amqp.exchange.auto-delete")

(cc/defprop-int listen-port
  "The port to listen to for incoming connections."
  [props config-valid configs]
  "notificationagent.listen-port")

(defn- validate-config
  "Validates the configuration settings after they've been loaded."
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:type :clojure-commons.exception/invalid-cfg})))

(defn load-config-from-file
  "Loads the configuration settings from a file."
  [cfg-path]
  (cc/load-config-from-file cfg-path props)
  (cc/log-config props)
  (validate-config))
