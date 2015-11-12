(ns facepalm.c230-2015111001
  (:use [korma.core]
        [kameleon.sql-reader :only [exec-sql-statement load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.3.0:20151110.01")

(defn- add-tool-delete-cascade
  []
  (println "\t* Adding container_settings DELETE CASCADE constraints...")
  (exec-sql-statement "ALTER TABLE ONLY container_settings DROP CONSTRAINT container_settings_tools_id_fkey")
  (load-sql-file "constraints/71_container_settings.sql")

  (println "\t* Adding container_devices DELETE CASCADE constraints...")
  (exec-sql-statement "ALTER TABLE ONLY container_devices DROP CONSTRAINT container_devices_container_settings_id_fkey")
  (load-sql-file "constraints/72_container_devices.sql")

  (println "\t* Adding container_volumes DELETE CASCADE constraints...")
  (exec-sql-statement "ALTER TABLE ONLY container_volumes DROP CONSTRAINT container_volumes_container_settings_id_fkey")
  (load-sql-file "constraints/73_container_volumes.sql")

  (println "\t* Adding container_volumes_from DELETE CASCADE constraints...")
  (exec-sql-statement "ALTER TABLE ONLY container_volumes_from DROP CONSTRAINT container_volumes_from_container_settings_id_fkey")
  (exec-sql-statement "ALTER TABLE ONLY container_volumes_from DROP CONSTRAINT container_volumes_from_data_containers_id_fkey")
  (load-sql-file "constraints/74_container_volumes_from.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-tool-delete-cascade))
