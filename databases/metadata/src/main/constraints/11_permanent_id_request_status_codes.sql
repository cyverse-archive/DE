SET search_path = public, pg_catalog;

--
-- permanent_id_request_status_codes table primary key.
--
ALTER TABLE ONLY permanent_id_request_status_codes
    ADD CONSTRAINT permanent_id_request_status_codes_pkey
    PRIMARY KEY (id);

--
-- All Permanent ID request status code names should be unique.
--
CREATE UNIQUE INDEX permanent_id_request_status_codes_name_unique
    ON permanent_id_request_status_codes(name);
