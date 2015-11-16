SET search_path = public, pg_catalog;

ALTER TABLE ONLY container_settings
    ADD CONSTRAINT container_settings_tools_id_fkey
    FOREIGN KEY(tools_id)
    REFERENCES tools(id) ON DELETE CASCADE;
