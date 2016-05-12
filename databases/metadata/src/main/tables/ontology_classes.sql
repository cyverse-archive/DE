SET search_path = public, pg_catalog;

--
-- ontology_classes table
--
CREATE TABLE ontology_classes (
  ontology_version VARCHAR NOT NULL,
  iri VARCHAR NOT NULL,
  label VARCHAR,
  description TEXT
);
