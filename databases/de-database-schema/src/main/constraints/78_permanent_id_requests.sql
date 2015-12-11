SET search_path = public, pg_catalog;

--
-- Foreign key constraint for the requestor_id field of the permanent_id_requests table.
--
ALTER TABLE ONLY permanent_id_requests
    ADD CONSTRAINT permanent_id_requests_requestor_id_fkey
    FOREIGN KEY (requestor_id)
    REFERENCES users(id);

ALTER TABLE ONLY permanent_id_requests
    ADD CONSTRAINT permanent_id_requests_type_fkey
    FOREIGN KEY (type)
    REFERENCES permanent_id_request_types(id);
