--
-- Table for storing subjects to whom permissions can be granted.
--
CREATE TABLE subjects (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    subject_id varchar(64) NOT NULL,
    subject_type subject_type_enum NOT NULL
);
