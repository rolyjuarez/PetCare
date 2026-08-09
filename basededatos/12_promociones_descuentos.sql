-- =====================================================================
-- 12_promociones_descuentos.sql
-- Descuentos publicados por proveedores (provider-service):
--   * promocion.servicio_id            -> descuento vinculado a un servicio del proveedor
--   * pago.descuento_*                 -> registro del descuento aplicado en el pago (payment-service)
--   * Seed: 5 servicios del proveedor + 10 descuentos de ejemplo
-- =====================================================================

-- =============================================================================
-- promocion: vincular el descuento a un servicio específico del proveedor
-- =============================================================================
ALTER TABLE promocion ADD COLUMN IF NOT EXISTS servicio_id BIGINT REFERENCES proveedor_servicio(id);

COMMENT ON COLUMN promocion.servicio_id IS 'proveedor_servicio.id al que se aplica el descuento (NULL = aplica a todos los servicios del proveedor)';

CREATE INDEX IF NOT EXISTS idx_promocion_proveedor_servicio ON promocion(proveedor_id, servicio_id) WHERE deleted = FALSE;

-- =============================================================================
-- pago: registrar el descuento concreto aplicado al procesar (payment-service)
-- =============================================================================
ALTER TABLE pago ADD COLUMN IF NOT EXISTS descuento_id BIGINT;
ALTER TABLE pago ADD COLUMN IF NOT EXISTS descuento_codigo VARCHAR(30);
ALTER TABLE pago ADD COLUMN IF NOT EXISTS descuento_nombre VARCHAR(100);
ALTER TABLE pago ADD COLUMN IF NOT EXISTS descuento_tipo VARCHAR(20);
ALTER TABLE pago ADD COLUMN IF NOT EXISTS descuento_servicio_id BIGINT;

COMMENT ON COLUMN pago.descuento_id IS 'promocion.id aplicada (NULL si no aplicó descuento)';
COMMENT ON COLUMN pago.descuento_servicio_id IS 'proveedor_servicio.id del servicio con descuento';

-- =============================================================================
-- SEED: servicios propios del proveedor 1 (proveedor de ejemplo "Roberto Mamani")
-- NOTA: para instalaciones nuevas proveedor_servicio está vacía. Si ya existieran
-- registros, ajustar los IDs o eliminar los INSERT de servicios duplicados.
-- =============================================================================
INSERT INTO proveedor_servicio (id, proveedor_id, nombre, descripcion, categoria, duracion_minutos, precio_base) VALUES
(1, 1, 'Peluquería Básica',     'Corte, baño y secado básico para mascotas',          'PELUQUERIA',   60,  80.00),
(2, 1, 'Peluquería Premium',    'Corte, baño, secado, corte de uñas y limpieza de oídos','PELUQUERIA', 90, 120.00),
(3, 1, 'Baño Terapéutico',      'Baño con productos medicinales para problemas de piel', 'PELUQUERIA', 45, 100.00),
(4, 1, 'Paseo Extendido',       'Paseo de 60 minutos con socialización',              'PASEO',        60,  50.00),
(5, 1, 'Vacunación',            'Aplicación de vacunas según calendario',             'VETERINARIA',  30,  80.00);

INSERT INTO proveedor_servicio_modalidad (proveedor_servicio_id, modalidad, costo_adicional) VALUES
(1, 'EN_ESTABLECIMIENTO', 0.00),
(1, 'DOMICILIO',         20.00),
(2, 'EN_ESTABLECIMIENTO', 0.00),
(2, 'DOMICILIO',         20.00),
(3, 'EN_ESTABLECIMIENTO', 0.00),
(3, 'DOMICILIO',         20.00),
(4, 'EN_ESTABLECIMIENTO', 0.00),
(4, 'RECOGIDA_ENTREGA',  10.00),
(5, 'EN_ESTABLECIMIENTO', 0.00),
(5, 'DOMICILIO',         20.00);

-- =============================================================================
-- SEED: 10 descuentos publicados por el proveedor 1
-- Fechas relativas a NOW() para que siempre estén vigentes al inicializar
-- =============================================================================
INSERT INTO promocion (proveedor_id, servicio_id, codigo, nombre, descripcion, tipo_descuento, valor_descuento,
                       fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales) VALUES
-- Peluquería Básica (servicio 1)
(1, 1, 'PELU-BASICO-10', 'Peluquería Básica 10%',     '10% de descuento en peluquería básica',                    'PERCENTAGE', 10.00, NOW() - INTERVAL '5 days',  NOW() + INTERVAL '90 days', TRUE, 100, 0),
(1, 1, 'PELU-BASICO-15', 'Peluquería Básica 15 Bs',   '15 Bs de descuento en peluquería básica',                  'FIXED',      15.00, NOW() - INTERVAL '5 days',  NOW() + INTERVAL '60 days', TRUE,  50, 0),
-- Peluquería Premium (servicio 2)
(1, 2, 'PELU-PREMIUM-15', 'Peluquería Premium 15%',   '15% de descuento en peluquería premium',                   'PERCENTAGE', 15.00, NOW() - INTERVAL '3 days',  NOW() + INTERVAL '120 days', TRUE, 100, 0),
(1, 2, 'PELU-PREMIUM-20', 'Peluquería Premium 20 Bs', '20 Bs de descuento en peluquería premium',                 'FIXED',      20.00, NOW() - INTERVAL '3 days',  NOW() + INTERVAL '90 days', TRUE,  60, 0),
-- Baño Terapéutico (servicio 3)
(1, 3, 'BANO-20',          'Baño Terapéutico 20%',    '20% de descuento en baño terapéutico',                     'PERCENTAGE', 20.00, NOW() - INTERVAL '10 days', NOW() + INTERVAL '150 days', TRUE, 80, 0),
(1, 3, 'BANO-15',          'Baño Terapéutico 15 Bs',  '15 Bs de descuento en baño terapéutico',                   'FIXED',      15.00, NOW() - INTERVAL '10 days', NOW() + INTERVAL '45 days',  TRUE, 40, 0),
-- Paseo Extendido (servicio 4)
(1, 4, 'PASEO-25',          'Paseo Extendido 25%',     '25% de descuento en paseo extendido',                      'PERCENTAGE', 25.00, NOW() - INTERVAL '2 days',  NOW() + INTERVAL '60 days',  TRUE, 90, 0),
(1, 4, 'PASEO-10',          'Paseo Extendido 10 Bs',   '10 Bs de descuento en paseo extendido',                    'FIXED',      10.00, NOW() - INTERVAL '2 days',  NOW() + INTERVAL '30 days',  TRUE, 70, 0),
-- Vacunación (servicio 5)
(1, 5, 'VACUNA-30',         'Vacunación 30%',          '30% de descuento en vacunación',                           'PERCENTAGE', 30.00, NOW() - INTERVAL '7 days',  NOW() + INTERVAL '90 days',  TRUE, 120, 0),
(1, 5, 'VACUNA-20',         'Vacunación 20 Bs',        '20 Bs de descuento en vacunación',                         'FIXED',      20.00, NOW() - INTERVAL '7 days',  NOW() + INTERVAL '60 days',  TRUE, 60, 0);

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
DECLARE
    total_descuentos INTEGER;
    total_servicios  INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_descuentos FROM promocion WHERE proveedor_id = 1 AND deleted = FALSE;
    SELECT COUNT(*) INTO total_servicios  FROM proveedor_servicio WHERE proveedor_id = 1 AND deleted = FALSE;
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Seed de descuentos insertado:';
    RAISE NOTICE '  - % servicios del proveedor 1', total_servicios;
    RAISE NOTICE '  - % descuentos del proveedor 1', total_descuentos;
    RAISE NOTICE '============================================================';
END $$;
