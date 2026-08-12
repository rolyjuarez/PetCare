-- =============================================================
-- 15_menu_mis_servicios_proveedor.sql
-- Corrige la navegacion del rol PROVEEDOR:
--   "Proveedor: Lista Servicios" (permiso 65) apuntaba a
--   /app/servicios (catalogo admin con botones sin handlers).
--   Ahora apunta a un menu propio "Mis Servicios" ->
--   /app/proveedor/servicios (MisServiciosComponent con CRUD via
--   provider-service).
-- =============================================================

BEGIN;

-- 1) Crear menu "Mis Servicios" para el proveedor (idempotente)
INSERT INTO menu (nombre, descripcion, icono, url, orden, activo)
SELECT 'Mis Servicios',
       'Gestion de servicios del proveedor (provider-service)',
       'build',
       '/app/proveedor/servicios',
       32,
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM menu
    WHERE url = '/app/proveedor/servicios' AND deleted = FALSE
);

-- 2) Reapuntar el permiso 65 (Proveedor: Lista Servicios) al nuevo menu
UPDATE permiso
SET menu_id = (SELECT id FROM menu WHERE url = '/app/proveedor/servicios' AND deleted = FALSE),
    updated_at = NOW(),
    version = version + 1
WHERE id = 65
  AND deleted = FALSE
  AND (SELECT id FROM menu WHERE url = '/app/proveedor/servicios' AND deleted = FALSE) IS NOT NULL;

-- 3) Ajustar secuencia
SELECT setval('menu_id_seq', (SELECT MAX(id) FROM menu), TRUE);

COMMIT;

-- Verificacion
SELECT m.id, m.nombre, m.url, p.id AS permiso_id, p.nombre AS permiso_nombre
FROM permiso p
JOIN menu m ON m.id = p.menu_id
WHERE p.id = 65 AND p.deleted = FALSE;
