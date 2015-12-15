SET search_path = public, pg_catalog;

--
-- The statuses that have been applied to each Permanent ID request.
--
CREATE TABLE permanent_id_request_statuses (
    id UUID NOT NULL DEFAULT uuid_generate_v1(),
    permanent_id_request UUID NOT NULL,
    permanent_id_request_status_code UUID NOT NULL,
    date_assigned TIMESTAMP DEFAULT now() NOT NULL,
    updated_by varchar(512) NOT NULL,
    comments TEXT
);
