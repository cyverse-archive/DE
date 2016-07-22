--
-- Foreign key from the integration_data table to the users table.
--
ALTER TABLE ONLY integration_data
    ADD CONSTRAINT integration_data_user_id_fk
    FOREIGN KEY (user_id)
    REFERENCES users (id);
