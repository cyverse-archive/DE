(ns apps.clients.email
  (:require [apps.util.config :as config]
            [cemerick.url :as curl]
            [cheshire.core :as cheshire]
            [clj-http.client :as client]))

(defn send-email
  "Sends an e-mail message via the iPlant e-mail service."
  [& {:keys [to from-addr from-name subject template values]}]
  (client/post
    (config/iplant-email-base-url)
    {:content-type :json
     :body         (cheshire/encode {:to        to
                                     :from-addr from-addr
                                     :from-name from-name
                                     :subject   subject
                                     :template  template
                                     :values    values})}))

(defn send-app-deletion-notification
  "Sends an app deletion email message to the app integrator."
  [integrator-name integrator-email app-name app-id]
  (let [app-link (str (assoc (curl/url (config/ui-base-url)) :query {:type "apps" :app-id app-id}))
        template-values {:name     integrator-name
                         :app_name app-name
                         :app_link app-link}]
    (send-email
      :to        integrator-email
      :from-addr (config/app-deletion-notification-src-addr)
      :subject   (format (config/app-deletion-notification-subject) app-name)
      :template  "app_deletion_notification"
      :values    template-values)))
