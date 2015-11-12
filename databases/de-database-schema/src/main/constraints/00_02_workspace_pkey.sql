SET search_path = public, pg_catalog;

--
-- workspace table primary key.
--
ALTER TABLE ONLY workspace
    ADD CONSTRAINT workspace_pkey
    PRIMARY KEY (id);
