SET search_path = public, pg_catalog;

ALTER TABLE ONLY app_hierarchy_version
    ADD CONSTRAINT app_hierarchy_version_applied_by_fkey
    FOREIGN KEY (applied_by)
    REFERENCES users(id);
