--
-- The table for storing permission levels that can be applied to resources.
--
CREATE TABLE permission_levels (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    name varchar(64) UNIQUE NOT NULL,
    description text NOT NULL,
    precedence integer UNIQUE NOT NULL,
    PRIMARY KEY (id)
);
