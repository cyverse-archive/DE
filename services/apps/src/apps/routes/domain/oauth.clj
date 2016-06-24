(ns apps.routes.domain.oauth
  (:use [common-swagger-api.schema :only [describe]])
  (:require [schema.core :as s]))

(s/defschema OAuthCallbackResponse
  {:state_info (describe String "Arbitrary state information required by the UI.")})

(s/defschema TokenInfo
  {:expires_at (describe Long "The token expiration time as milliseconds since the epoch.")
   :webapp     (describe String "The name of the external web application.")})
