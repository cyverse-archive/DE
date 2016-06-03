SET search_path = public, pg_catalog;

--
-- A function that returns all of the ontology subclasses of the class with the given
-- ontology_version and IRI.
--
CREATE OR REPLACE FUNCTION ontology_class_hierarchy(VARCHAR, VARCHAR)
RETURNS
TABLE(
    parent_iri VARCHAR,
    iri VARCHAR,
    label VARCHAR
) AS $$
    WITH RECURSIVE subclasses AS
    (
      (SELECT h.class_iri AS parent_iri, c.iri, c.label
       FROM ontology_classes c
         LEFT JOIN ontology_hierarchies h ON h.ontology_version = $1 AND
                                             h.subclass_iri = c.iri
       WHERE c.ontology_version = $1 AND
             c.iri = $2
       LIMIT 1)
      UNION
      (SELECT h.class_iri AS parent_iri, c.iri, c.label
       FROM subclasses sc, ontology_classes c
         JOIN ontology_hierarchies h ON h.subclass_iri = c.iri
       WHERE c.ontology_version = $1 AND
             h.ontology_version = $1 AND
             h.class_iri = sc.iri)
    )
    SELECT * FROM subclasses
$$ LANGUAGE SQL;
