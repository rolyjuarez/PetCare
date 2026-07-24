-- Migracion: Agregar columna empresa a la tabla proveedor
-- Fecha: 2026-07-24

ALTER TABLE proveedor ADD COLUMN empresa VARCHAR(150);

COMMENT ON COLUMN proveedor.empresa IS 'Nombre de la empresa o negocio del proveedor';

-- Actualizar DDL unificado
-- (Agregar empresa despues de usuario_id en la tabla proveedor)
