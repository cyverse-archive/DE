SET search_path = public, pg_catalog;

--
-- The possible Permanent ID types of each request.
--
CREATE TABLE permanent_id_request_types (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    type VARCHAR NOT NULL,
    description TEXT NOT NULL
);
