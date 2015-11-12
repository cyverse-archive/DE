SET search_path = public, pg_catalog;

--
-- app_categories table foreign keys.
--
ALTER TABLE ONLY app_categories
    ADD CONSTRAINT app_categories_workspace_id_fk
    FOREIGN KEY (workspace_id)
    REFERENCES workspace(id) ON DELETE CASCADE;
