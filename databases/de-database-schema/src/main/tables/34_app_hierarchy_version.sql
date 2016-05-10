SET search_path = public, pg_catalog;

--
-- app_hierarchy_version table
--
CREATE TABLE app_hierarchy_version (
    version VARCHAR NOT NULL,
    applied_by UUID NOT NULL,
    applied timestamp DEFAULT now()
);

--
-- Creates an index on the applied timestamp
--
CREATE INDEX app_hierarchy_version_applied
    ON app_hierarchy_version(applied);
