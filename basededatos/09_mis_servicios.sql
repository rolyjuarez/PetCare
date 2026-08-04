-- =====================================================================
-- 09_mis_servicios.sql
-- Menú "Mis Servicios" para el rol PROVEEDOR:
--   configuración de servicios, horarios y requisito de certificado
-- =====================================================================

INSERT INTO submenu (menu_id, nombre, descripcion, icono, url, orden, activo, version)
SELECT m.id, 'Mis Servicios', 'Configura tus servicios, horarios y requisitos de certificado',
       'miscellaneous_services', '/app/proveedor/servicios', 2, TRUE, 1
FROM menu m
WHERE m.nombre = 'Proveedor'
  AND NOT EXISTS (SELECT 1 FROM submenu s WHERE s.url = '/app/proveedor/servicios');

INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar, version)
SELECT 'Proveedor: Mis Servicios', 'Configurar servicios y disponibilidad', 3, m.id, s.id, TRUE, TRUE, TRUE, TRUE, 1
FROM menu m
JOIN submenu s ON s.menu_id = m.id AND s.url = '/app/proveedor/servicios'
WHERE m.nombre = 'Proveedor'
  AND NOT EXISTS (
      SELECT 1 FROM permiso p
      WHERE p.rol_id = 3 AND p.submenu_id = s.id
  );
