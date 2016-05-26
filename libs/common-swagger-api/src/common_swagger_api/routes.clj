(ns common-swagger-api.routes)

(defn get-endpoint-delegate-block
  [service endpoint]
  (str "

#### Delegates to " service " service
    " endpoint "
"))
