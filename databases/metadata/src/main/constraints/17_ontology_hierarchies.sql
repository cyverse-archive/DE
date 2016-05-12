SET search_path = public, pg_catalog;

--
-- ontology_hierarchies table primary key.
--
ALTER TABLE ontology_hierarchies
    ADD CONSTRAINT ontology_hierarchies_pkey
    PRIMARY KEY (ontology_version, class_iri, subclass_iri);

--
-- ontology_hierarchies table foreign key to the ontologies table.
--
ALTER TABLE ontology_hierarchies
    ADD CONSTRAINT ontology_hierarchies_version_fkey
    FOREIGN KEY (ontology_version)
    REFERENCES ontologies(version)
    ON DELETE CASCADE;

--
-- ontology_hierarchies table foreign key to the ontology_classes table.
--
ALTER TABLE ontology_hierarchies
    ADD CONSTRAINT ontology_hierarchies_class_iri_fkey
    FOREIGN KEY (ontology_version, class_iri)
    REFERENCES ontology_classes(ontology_version, iri)
    ON DELETE CASCADE;

--
-- ontology_hierarchies table foreign key to the ontology_classes table.
--
ALTER TABLE ontology_hierarchies
    ADD CONSTRAINT ontology_hierarchies_subclass_iri_fkey
    FOREIGN KEY (ontology_version, subclass_iri)
    REFERENCES ontology_classes(ontology_version, iri)
    ON DELETE CASCADE;
