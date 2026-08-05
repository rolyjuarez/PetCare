-- =====================================================================
-- 11_mis_servicios_seed.sql
-- Datos de ejemplo de "Mis Servicios" para el proveedor TODO'S PET (id=3).
--
-- El flujo de reserva (frontend + reservation-service) resuelve el
-- servicio, la duración, las modalidades y requiere_certificado desde
-- proveedor_servicio / proveedor_servicio_modalidad. Esta migración crea
-- esos registros con ids alineados al catálogo de `servicio` que el
-- proveedor ofrece (según proveedor_especialidad), de modo que la reserva
-- funciona de punta a punta.
-- =====================================================================

INSERT INTO proveedor_servicio (id, proveedor_id, nombre, descripcion, categoria,
                                duracion_minutos, precio_base, requiere_certificado, activo)
VALUES
    (1,  3, 'Peluquería Básica',  'Corte y baño básico',        'PELUQUERIA', 60, 80.00,  FALSE, TRUE),
    (2,  3, 'Peluquería Premium', 'Corte, baño y spa',          'PELUQUERIA', 90, 120.00, FALSE, TRUE),
    (3,  3, 'Baño Terapéutico',   'Baño con productos especiales', 'PELUQUERIA', 45, 100.00, FALSE, TRUE),
    (15, 3, 'Estética Canina',    'Servicio completo de estética', 'PELUQUERIA', 45, 90.00,  FALSE, TRUE)
ON CONFLICT (id) DO UPDATE SET
    proveedor_id = EXCLUDED.proveedor_id,
    nombre = EXCLUDED.nombre,
    categoria = EXCLUDED.categoria,
    activo = TRUE,
    deleted = FALSE;

INSERT INTO proveedor_servicio_modalidad (proveedor_servicio_id, modalidad, costo_adicional, activo)
VALUES
    (1,  'EN_ESTABLECIMIENTO', 0.00,  TRUE),
    (1,  'RECOGIDA_ENTREGA',   20.00, TRUE),
    (1,  'DOMICILIO',          30.00, TRUE),
    (2,  'EN_ESTABLECIMIENTO', 0.00,  TRUE),
    (2,  'RECOGIDA_ENTREGA',   20.00, TRUE),
    (2,  'DOMICILIO',          30.00, TRUE),
    (3,  'EN_ESTABLECIMIENTO', 0.00,  TRUE),
    (3,  'RECOGIDA_ENTREGA',   20.00, TRUE),
    (3,  'DOMICILIO',          30.00, TRUE),
    (15, 'EN_ESTABLECIMIENTO', 0.00,  TRUE),
    (15, 'RECOGIDA_ENTREGA',   20.00, TRUE),
    (15, 'DOMICILIO',          30.00, TRUE)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('proveedor_servicio', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM proveedor_servicio), 1));
SELECT setval(pg_get_serial_sequence('proveedor_servicio_modalidad', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM proveedor_servicio_modalidad), 1));
