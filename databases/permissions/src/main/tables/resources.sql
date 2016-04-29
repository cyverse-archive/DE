--
-- Table for storing specific resources to which permissions may be applied.
--
CREATE TABLE resources (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    name varchar(64) NOT NULL,
    resource_type_id uuid NOT NULL,
    PRIMARY KEY (id)
);
