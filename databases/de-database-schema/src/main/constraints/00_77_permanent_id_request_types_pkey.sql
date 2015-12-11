SET search_path = public, pg_catalog;

--
-- permanent_id_requests table primary key.
--
ALTER TABLE ONLY permanent_id_request_types
    ADD CONSTRAINT permanent_id_request_types_pkey
    PRIMARY KEY (id);
