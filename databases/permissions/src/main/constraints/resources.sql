--
-- The foreign key into the resource_types table.
--
ALTER TABLE resources
    ADD CONSTRAINT resources_resource_type_id_fkey
    FOREIGN KEY (resource_type_id)
    REFERENCES resource_types(id);
