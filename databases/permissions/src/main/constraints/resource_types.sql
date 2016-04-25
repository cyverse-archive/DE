--
-- Uniqueness constraint on the resource type name.
--
CREATE UNIQUE INDEX IF NOT EXISTS resource_types_name_unique
    ON resource_types (lower(trim(regexp_replace(name, '\s+', ' ', 'g'))));
