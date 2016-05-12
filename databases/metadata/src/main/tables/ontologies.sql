SET search_path = public, pg_catalog;

--
-- ontologies table
--
CREATE TABLE ontologies (
  version VARCHAR NOT NULL,
  iri VARCHAR,
  created_by VARCHAR(512) NOT NULL,
  created_on TIMESTAMP DEFAULT now() NOT NULL,
  xml TEXT NOT NULL
);
