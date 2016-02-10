SET search_path = public, pg_catalog;

--
-- Uniqueness constraint on reference genome name.
--
ALTER TABLE ONLY genome_reference
ADD CONSTRAINT genome_ref_name_unique
UNIQUE (name);

--
-- Uniqueness constraint on reference genome path.
--
ALTER TABLE ONLY genome_reference
ADD CONSTRAINT genome_ref_path_unique
UNIQUE (path);
