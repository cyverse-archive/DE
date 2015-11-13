SET search_path = public, pg_catalog;

--
-- Foreign key constraint on the container_volumes table against the
-- container_settings table.
--
ALTER TABLE ONLY container_volumes
    ADD CONSTRAINT container_volumes_container_settings_id_fkey
    FOREIGN KEY(container_settings_id)
    REFERENCES container_settings(id) ON DELETE CASCADE;
