SET search_path = public, pg_catalog;

--
-- The Permanent ID requests table.
--
CREATE TABLE permanent_id_requests (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    requested_by varchar(512) NOT NULL,
    type UUID,
    target_id UUID NOT NULL,
    target_type target_enum NOT NULL,
    original_path TEXT,
    permanent_id TEXT
);
