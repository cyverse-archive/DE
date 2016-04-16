SET search_path = public, pg_catalog;

--
-- ontology_hierarchies table
--
CREATE TABLE ontology_hierarchies (
  ontology_version VARCHAR NOT NULL,
  class_iri VARCHAR NOT NULL,
  subclass_iri VARCHAR NOT NULL
);
