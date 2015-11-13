SET search_path = public, pg_catalog;

--
-- Primary key constraint for the container_volumes table.
--
ALTER TABLE ONLY container_volumes
    ADD CONSTRAINT container_volumes_pkey
    PRIMARY KEY(id);
