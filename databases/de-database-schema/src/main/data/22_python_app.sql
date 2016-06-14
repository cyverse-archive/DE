SET search_path = public, pg_catalog;

--
-- The container image for Python 2.7
--
INSERT INTO container_images (id, "name", tag, url) VALUES
    ('bad7e301-4442-4e82-8cc4-8db681cae364',
     'python',
     '2.7',
     'https://hub.docker.com/_/python/');
--
-- The internal tool for Python 2.7
--
INSERT INTO tools
    (id,
     "name",
     location,
     description,
     version,
     tool_type_id,
     integration_data_id,
     time_limit_seconds,
     container_images_id)
  SELECT '4e3b1710-0f15-491f-aca9-812335356fdb',
         'python',
         '/usr/local/bin',
         'Python 2.7 with no networking, a 1GB RAM limit, and a 10% cpu share. Entrypoint is python.',
         '1.0.0',
         tool_types.id,
         integration_data.id,
         14400,
         'bad7e301-4442-4e82-8cc4-8db681cae364'
    FROM tool_types, integration_data
   WHERE tool_types."name" = 'executable'
     AND integration_data.integrator_name = 'Default DE Tools'
   LIMIT 1;

INSERT INTO container_settings (tools_id, network_mode, entrypoint, memory_limit, cpu_shares)
  VALUES ('4e3b1710-0f15-491f-aca9-812335356fdb', 'none', 'python', 1000000000, 102);

--
-- The app for Python 2.7
--
INSERT INTO apps
    (id,
     "name",
     description,
     integration_data_id,
     wiki_url,
     integration_date)
  SELECT '336bbfb3-7899-493a-b4a2-ed3bc353ead8',
         'Python 2.7',
         'Runs an arbitrary Python script with a time limit of 4 hours, a 1GB RAM limit, a 10% cpu share, and no networking. Accepts a script and a data file as inputs.',
         integration_data.id,
         '',
         now()
    FROM integration_data
   WHERE integrator_name = 'Default DE Tools'
   LIMIT 1;

INSERT INTO tasks (id, "name", description, label, tool_id) VALUES
    ('66b59035-6036-46c3-a30a-ee3bd4af47b6',
     'Run a Python 2.7 script',
     'Runs a Python 2.7 script against a data file',
     'Run a Python 2.7 script',
     '4e3b1710-0f15-491f-aca9-812335356fdb');

INSERT INTO parameter_groups (id, "name", description, label, task_id) VALUES
    ('f252f7b2-5c27-4a27-bbbb-f4f2f2acf407',
     'Parameters',
     'Python 2.7 parameters',
     'Parameters',
     '66b59035-6036-46c3-a30a-ee3bd4af47b6');

INSERT INTO parameters 
      (id,
     "name",
     description,
     label,
     ordering,
     parameter_group_id,
     parameter_type,
     display_order,
     required)
  SELECT '5e1339f0-e01a-4fa3-8546-f7f16af547bf',
         '',
         'The Python script to run',
         'Script',
         0,
         'f252f7b2-5c27-4a27-bbbb-f4f2f2acf407',
         pt.id,
         0,
         TRUE
    FROM parameter_types pt
   WHERE pt."name" = 'FileInput'
   LIMIT 1;

INSERT INTO parameters
    (id,
     "name",
     description,
     label,
     ordering,
     parameter_group_id,
     parameter_type,
     display_order,
     required)
  SELECT '41d1a467-17fa-4b25-ba5e-43c8cb88948b',
         '',
         'The data file to process',
         'Data file',
         1,
         'f252f7b2-5c27-4a27-bbbb-f4f2f2acf407',
         pt.id,
         1,
         TRUE
    FROM parameter_types pt
   WHERE pt."name" = 'FileInput'
   LIMIT 1;

INSERT INTO file_parameters (id, parameter_id, info_type, data_format, data_source_id, retain)
  SELECT '78244fb8-d5bb-479b-b73e-a12c20dbb774',
         '5e1339f0-e01a-4fa3-8546-f7f16af547bf',
         info_type.id,
         data_formats.id,
         data_source.id,
         TRUE
    FROM info_type, data_formats, data_source
   WHERE info_type."name" = 'File'
     AND data_formats."name" = 'Unspecified'
     AND data_source."name" = 'file'
   LIMIT 1;

INSERT INTO file_parameters (id, parameter_id, info_type, data_format, data_source_id, retain)
  SELECT '73ec6e74-d5e6-4977-b999-620b4e79ebda',
         '41d1a467-17fa-4b25-ba5e-43c8cb88948b',
         info_type.id,
         data_formats.id,
         data_source.id,
         TRUE
    FROM info_type, data_formats, data_source
   WHERE info_type."name" = 'File'
     AND data_formats."name" = 'Unspecified'
     AND data_source."name" = 'file'
   LIMIT 1;

INSERT INTO app_category_app (app_category_id, app_id) VALUES
    ('5401bd146c144470aedd57b47ea1b979',
     '336bbfb3-7899-493a-b4a2-ed3bc353ead8');

INSERT INTO app_steps (step, id, app_id, task_id) VALUES
    (0,
     'b34736a8-aa68-4845-803d-c0d1942ccdff',
     '336bbfb3-7899-493a-b4a2-ed3bc353ead8',
     '66b59035-6036-46c3-a30a-ee3bd4af47b6');
