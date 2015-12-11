SET search_path = public, pg_catalog;

--
-- All Permanent ID request status code names should be unique.
--
CREATE UNIQUE INDEX permanent_id_request_status_codes_name_unique
    ON permanent_id_request_status_codes(name);
