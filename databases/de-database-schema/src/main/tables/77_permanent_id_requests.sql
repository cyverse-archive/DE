SET search_path = public, pg_catalog;

--
-- The Permanent ID requests table.
--
CREATE TABLE permanent_id_requests (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    requestor_id UUID NOT NULL,
    data_id UUID
);
