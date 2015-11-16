SET search_path = public, pg_catalog;

--
-- users table unique constraint.
--
ALTER TABLE ONLY users
ADD CONSTRAINT username_unique
UNIQUE (username);
