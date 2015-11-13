SET search_path = public, pg_catalog;

--
-- workspace table foreign keys.
--
ALTER TABLE ONLY workspace
    ADD CONSTRAINT workspace_root_category_id_fkey
    FOREIGN KEY (root_category_id)
    REFERENCES app_categories(id) ON DELETE CASCADE;

ALTER TABLE ONLY workspace
    ADD CONSTRAINT workspace_users_fk
    FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE;
