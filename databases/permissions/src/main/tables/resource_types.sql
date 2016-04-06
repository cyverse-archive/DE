--
-- Table for storing the types of resources to which permissions may be applied.
--
CREATE TABLE resource_types (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    name varchar(64) NOT NULL,
    description text NOT NULL,
    PRIMARY KEY (id)
);
