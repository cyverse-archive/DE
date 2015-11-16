SET search_path = public, pg_catalog;

--
-- tools table foreign keys.
--
ALTER TABLE ONLY tools
    ADD CONSTRAINT deployed_comp_integration_data_id_fk
    FOREIGN KEY (integration_data_id)
    REFERENCES integration_data(id);

ALTER TABLE ONLY tools
    ADD CONSTRAINT tools_tool_type_id_fkey
    FOREIGN KEY (tool_type_id)
    REFERENCES tool_types(id);

ALTER TABLE ONLY tools
    ADD CONSTRAINT tools_container_image_fkey
    FOREIGN KEY(container_images_id)
    REFERENCES container_images(id);
