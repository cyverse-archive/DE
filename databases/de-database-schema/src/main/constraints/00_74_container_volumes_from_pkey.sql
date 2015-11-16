SET search_path = public, pg_catalog;

--
-- Primary key constraint for the container_volumes_from table.
--
ALTER TABLE ONLY container_volumes_from
    ADD CONSTRAINT container_volumes_from_pkey
    PRIMARY KEY(id);
