--
-- Populates the permanent_id_request_status_codes table.
--
INSERT INTO permanent_id_request_status_codes (name, description) VALUES
  ('Submitted',
   'The request has been submitted and data moved into iDC staging, but not acted upon by the curators.'),
  ('Pending',
   'The curators are waiting for a response from the requesting user.'),
  ('Evaluation',
   'The curators are evaluating the metadata and data structure.'),
  ('Approved',
   'The curators have approved the data and metadata and have submitted it for a public ID.'),
  ('Completion',
   'The data has been successfully assigned a public ID and moved into the iDC main space.'),
  ('Failed',
   'The data could not be submitted for a public ID.');
