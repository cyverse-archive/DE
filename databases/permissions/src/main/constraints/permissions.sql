--
-- The foreign key into the subjects table.
--
ALTER TABLE permissions
    ADD CONSTRAINT permissions_subject_id_fkey
    FOREIGN KEY (subject_id)
    REFERENCES subjects(id) ON DELETE CASCADE;

--
-- The foreign key into the resources table.
--
ALTER TABLE permissions
    ADD CONSTRAINT permissions_resource_id_fkey
    FOREIGN KEY (resource_id)
    REFERENCES resources(id) ON DELETE CASCADE;

--
-- The foreign key into the permission levels table.
--
ALTER TABLE permissions
    ADD CONSTRAINT permissions_permission_level_id_fkey
    FOREIGN KEY (permission_level_id)
    REFERENCES permission_levels(id);
