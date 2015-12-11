SET search_path = public, pg_catalog;

--
-- The statuses that have been applied to each Permanent ID request.
--
CREATE TABLE permanent_id_request_status_codes (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    name VARCHAR(64) NOT NULL,
    description TEXT NOT NULL
);

--
-- All Permanent ID request status code names should be unique.
--
CREATE UNIQUE INDEX permanent_id_request_status_codes_name_index
    ON permanent_id_request_status_codes(name);
