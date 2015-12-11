SET search_path = public, pg_catalog;

--
-- All Permanent ID types should be unique.
--
CREATE UNIQUE INDEX permanent_id_request_types_unique
    ON permanent_id_request_types(type);
