(ns dewey.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.config :as cc]
            [clojure-commons.error-codes :as ce]))

(def ^:private props (ref nil))
(def ^:private config-valid (ref true))
(def ^:private configs (ref []))

(cc/defprop-str environment-name
  "The name of the deployment environment this is part of."
  [props config-valid configs]
  "dewey.environment-name")

(cc/defprop-str amqp-host
  "The hostname for the AMQP server"
  [props config-valid configs]
  "dewey.amqp.host")

(cc/defprop-int amqp-port
  "The port number for the AMQP server"
  [props config-valid configs]
  "dewey.amqp.port")

(cc/defprop-str amqp-user
  "The username for the AMQP server"
  [props config-valid configs]
  "dewey.amqp.user")

(cc/defprop-str amqp-pass
  "The password for the AMQP user"
  [props config-valid configs]
  "dewey.amqp.password")

(cc/defprop-str amqp-exchange
  "The exchange name for the AMQP server"
  [props config-valid configs]
  "dewey.amqp.exchange.name")

(cc/defprop-boolean amqp-exchange-durable
  "Whether the AMQP exchange is durable"
  [props config-valid configs]
  "dewey.amqp.exchange.durable")

(cc/defprop-boolean amqp-exchange-autodelete
  "Whether the AMQP exchange is auto-delete"
  [props config-valid configs]
  "dewey.amqp.exchange.auto-delete")

(cc/defprop-int amqp-qos
  "How many messages to prefetch from the AMQP queue."
  [props config-valid configs]
  "dewey.amqp.qos")

(cc/defprop-str es-host
  "The hostname for the Elasticsearch server"
  [props config-valid configs]
  "dewey.es.host")

(cc/defprop-int es-port
  "The port number for the Elasticsearch server"
  [props config-valid configs]
  "dewey.es.port")

(cc/defprop-str irods-host
  "The hostname for the iRODS server"
  [props config-valid configs]
  "dewey.irods.host")

(cc/defprop-int irods-port
  "The port number for the iRODS server"
  [props config-valid configs]
  "dewey.irods.port")

(cc/defprop-str irods-zone
  "The zone name for the iRODS server"
  [props config-valid configs]
  "dewey.irods.zone")

(cc/defprop-str irods-user
  "The username for the iRODS server"
  [props config-valid configs]
  "dewey.irods.user")

(cc/defprop-str irods-pass
  "The password for the iRODS user"
  [props config-valid configs]
  "dewey.irods.password")

(cc/defprop-optstr irods-default-resource
  "The default resource to use with the iRODS server. Probably blank."
  [props config-valid configs]
  "dewey.irods.default-resource"
  "")

(cc/defprop-str irods-home
  "The base home directory for the iRODS server."
  [props config-valid configs]
  "dewey.irods.home")

(cc/defprop-int listen-port
  "The port number to listen on for status requests."
  [props config-valid configs]
  "dewey.status.listen-port")

(defn- validate-config
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:error_code ce/ERR_CONFIG_INVALID})))

(defn load-config-from-file
  [cfg-path]
  (cc/load-config-from-file cfg-path props)
  (cc/log-config props :filters [#"(irods|amqp)\.(user|pass)"])
  (validate-config))
