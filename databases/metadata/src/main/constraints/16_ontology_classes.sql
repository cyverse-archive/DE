SET search_path = public, pg_catalog;

--
-- ontology_classes table primary key.
--
ALTER TABLE ontology_classes
    ADD CONSTRAINT ontology_classes_pkey
    PRIMARY KEY (ontology_version, iri);

--
-- ontology_classes table foreign key to the ontologies table.
--
ALTER TABLE ontology_classes
    ADD CONSTRAINT ontology_classes_version_fkey
    FOREIGN KEY (ontology_version)
    REFERENCES ontologies(version)
    ON DELETE CASCADE;
