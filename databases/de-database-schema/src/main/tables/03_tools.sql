SET search_path = public, pg_catalog;

--
-- tools table
--
CREATE TABLE tools (
    id uuid NOT NULL DEFAULT uuid_generate_v1(),
    name character varying(255) NOT NULL,
    location character varying(255),
    tool_type_id uuid NOT NULL,
    description text,
    version character varying(255),
    attribution text,
    integration_data_id uuid NOT NULL,
    container_images_id uuid,
    time_limit_seconds integer NOT NULL DEFAULT 0,
    restricted boolean NOT NULL DEFAULT FALSE
);
