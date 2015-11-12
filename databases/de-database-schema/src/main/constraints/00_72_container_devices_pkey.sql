SET search_path = public, pg_catalog;

--
-- Primary key constraint for the container_devices table.
--
ALTER TABLE ONLY container_devices
    ADD CONSTRAINT container_devices_pkey
    PRIMARY KEY(id);
