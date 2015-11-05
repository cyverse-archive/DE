(ns apps.routes.reference-genomes
  (:use [common-swagger-api.schema]
        [apps.metadata.reference-genomes :only [get-reference-genome list-reference-genomes]]
        [apps.routes.domain.reference-genome]
        [apps.routes.params]
        [ring.util.http-response :only [ok]]))

(defroutes* reference-genomes
  (GET* "/" []
        :query [params ReferenceGenomeListingParams]
        :return ReferenceGenomesList
        :summary "List Reference Genomes."
        :description "This endpoint may be used to obtain lists of all available Reference Genomes."
        (ok (list-reference-genomes params)))

  (GET* "/:reference-genome-id" []
        :path-params [reference-genome-id :- ReferenceGenomeIdParam]
        :query [params SecuredQueryParams]
        :return ReferenceGenome
        :summary "Get a Reference Genome."
        :description "This endpoint may be used to obtain a Reference Genome by its UUID."
        (ok (get-reference-genome reference-genome-id))))
