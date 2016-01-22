SET search_path = public, pg_catalog;

--
-- permanent_id_requests table primary key.
--
ALTER TABLE ONLY permanent_id_requests
    ADD CONSTRAINT permanent_id_requests_pkey
    PRIMARY KEY (id);

ALTER TABLE ONLY permanent_id_requests
    ADD CONSTRAINT permanent_id_requests_type_fkey
    FOREIGN KEY (type)
    REFERENCES permanent_id_request_types(id);

--
-- All Permanent ID requests should be for unique target/type combinations.
--
CREATE UNIQUE INDEX permanent_id_requests_unique
    ON permanent_id_requests(target_id, type);
