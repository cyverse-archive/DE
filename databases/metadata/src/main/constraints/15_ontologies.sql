SET search_path = public, pg_catalog;

--
-- ontologies table primary key.
--
ALTER TABLE ontologies
    ADD CONSTRAINT ontologies_pkey
    PRIMARY KEY (version);
