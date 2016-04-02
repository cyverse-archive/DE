--
-- Table for storing permissions that have been granted.
--
CREATE TABLE permissions (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    subject_id uuid NOT NULL,
    resource_id uuid NOT NULL,
    permission_level_id uuid NOT NULL
);
