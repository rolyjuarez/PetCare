-- =============================================================================
-- PETCare Home Services - Script 01: Creación de Base de Datos
-- Base de Datos: petcaredb
-- Esquema: petcare
-- Motor: PostgreSQL
-- =============================================================================

-- Finalizar conexiones existentes a la base de datos
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'petcaredb'
  AND pid <> pg_backend_pid();

-- Eliminar base de datos si existe
DROP DATABASE IF EXISTS petcaredb;

-- Crear base de datos con codificación UTF-8
CREATE DATABASE petcaredb
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'es_BO.UTF-8'
    LC_CTYPE = 'es_BO.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Comentario en la base de datos
COMMENT ON DATABASE petcaredb IS 'Base de datos del sistema PETCare Home Services - Gestión de servicios de cuidado de mascotas a domicilio';

-- Conectar a la base de datos petcaredb
\connect petcaredb

-- Crear esquema principal
CREATE SCHEMA IF NOT EXISTS petcare
    AUTHORIZATION postgres;

COMMENT ON SCHEMA petcare IS 'Esquema principal del sistema PETCare Home Services';

-- Establecer search_path por defecto
ALTER DATABASE petcaredb SET search_path TO petcare, public;

-- Otorgar permisos al esquema
GRANT ALL ON SCHEMA petcare TO postgres;
GRANT USAGE ON SCHEMA petcare TO public;

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Base de datos petcaredb creada exitosamente';
    RAISE NOTICE 'Esquema: petcare';
    RAISE NOTICE 'Propietario: postgres';
    RAISE NOTICE '============================================================';
END $$;
