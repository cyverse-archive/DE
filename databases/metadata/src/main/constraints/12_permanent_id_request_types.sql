SET search_path = public, pg_catalog;

--
-- permanent_id_request_types table primary key.
--
ALTER TABLE ONLY permanent_id_request_types
    ADD CONSTRAINT permanent_id_request_types_pkey
    PRIMARY KEY (id);

--
-- All Permanent ID types should be unique.
--
CREATE UNIQUE INDEX permanent_id_request_types_unique
    ON permanent_id_request_types(type);
