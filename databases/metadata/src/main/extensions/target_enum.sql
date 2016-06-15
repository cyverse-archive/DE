--
-- target type enumeration
-- Currently, only analyses, apps, other AVUs, files, folders, and users may be targets of metadata.
--
CREATE TYPE target_enum AS ENUM ('analysis', 'app', 'avu', 'file', 'folder', 'avu', 'user');
