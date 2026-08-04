-- =====================================================================
-- 10_servicios_proveedor.sql
-- Servicios propios por proveedor:
--   * proveedor_servicio           (catálogo del proveedor con su costo)
--   * proveedor_servicio_modalidad (modalidades de entrega por servicio)
--   * reserva.modalidad_entrega    (modalidad elegida en la reserva)
--   * promocion.proveedor_id       (promociones creadas por proveedor)
--   * pago.*                       (columnas gestionadas por payment-service)
-- =====================================================================

-- =============================================================================
-- TABLA: proveedor_servicio
-- Servicios que el proveedor crea y gestiona (con su propio precio y duración)
-- =============================================================================
CREATE TABLE proveedor_servicio (
    id                      BIGSERIAL       PRIMARY KEY,
    proveedor_id            BIGINT          NOT NULL REFERENCES proveedor(id),
    nombre                  VARCHAR(100)    NOT NULL,
    descripcion             TEXT,
    categoria               VARCHAR(30)     NOT NULL,
    duracion_minutos        INTEGER         NOT NULL DEFAULT 60,
    precio_base             NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    requiere_certificado    BOOLEAN         NOT NULL DEFAULT FALSE,
    activo                  BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor_servicio IS 'Servicios propios que ofrece cada proveedor';
COMMENT ON COLUMN proveedor_servicio.categoria IS 'PELUQUERIA, PASEO, ALOJAMIENTO, VETERINARIA';

CREATE INDEX idx_proveedor_servicio_proveedor ON proveedor_servicio(proveedor_id) WHERE deleted = FALSE;
CREATE INDEX idx_proveedor_servicio_categoria ON proveedor_servicio(categoria) WHERE deleted = FALSE;

-- =============================================================================
-- TABLA: proveedor_servicio_modalidad
-- Modalidades de entrega soportadas por cada servicio del proveedor
-- =============================================================================
CREATE TABLE proveedor_servicio_modalidad (
    id                      BIGSERIAL       PRIMARY KEY,
    proveedor_servicio_id   BIGINT          NOT NULL REFERENCES proveedor_servicio(id),
    modalidad               VARCHAR(30)     NOT NULL,
    costo_adicional         NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    activo                  BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor_servicio_modalidad IS 'Modalidades por servicio: EN_ESTABLECIMIENTO, RECOGIDA_ENTREGA, DOMICILIO';
COMMENT ON COLUMN proveedor_servicio_modalidad.costo_adicional IS 'Costo adicional de la modalidad (0 en establecimiento)';

CREATE INDEX idx_proveedor_servicio_modalidad_servicio ON proveedor_servicio_modalidad(proveedor_servicio_id) WHERE deleted = FALSE;

-- =============================================================================
-- reserva: modalidad de entrega elegida por el cliente
-- =============================================================================
ALTER TABLE reserva ADD COLUMN modalidad_entrega VARCHAR(30);

COMMENT ON COLUMN reserva.modalidad_entrega IS 'Modalidad elegida: EN_ESTABLECIMIENTO, RECOGIDA_ENTREGA, DOMICILIO';

-- =============================================================================
-- solicitud_reserva: proyección del proveedor con la modalidad
-- =============================================================================
ALTER TABLE solicitud_reserva ADD COLUMN modalidad_entrega VARCHAR(30);

-- =============================================================================
-- promocion: promociones creadas por proveedores
-- =============================================================================
ALTER TABLE promocion ADD COLUMN proveedor_id BIGINT REFERENCES proveedor(id);

CREATE INDEX idx_promocion_proveedor ON promocion(proveedor_id) WHERE deleted = FALSE;

-- =============================================================================
-- pago: columnas gestionadas por el microservicio payment-service
-- =============================================================================
ALTER TABLE pago ADD COLUMN modalidad_pago VARCHAR(20) NOT NULL DEFAULT 'EN_ESTABLECIMIENTO';
ALTER TABLE pago ADD COLUMN intencion_id VARCHAR(100);
ALTER TABLE pago ADD COLUMN monto_original NUMERIC(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE pago ADD COLUMN descuento_total NUMERIC(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE pago ADD COLUMN estado_sync VARCHAR(20);

COMMENT ON COLUMN pago.modalidad_pago IS 'EN_LINEA (pasarela) o EN_ESTABLECIMIENTO';
COMMENT ON COLUMN pago.intencion_id IS 'ID de intención de la pasarela simulada';
COMMENT ON COLUMN pago.monto_original IS 'Monto antes de aplicar rebajas (patrón PIPE)';
COMMENT ON COLUMN pago.descuento_total IS 'Suma de rebajas aplicadas por la tubería de descuentos';
COMMENT ON COLUMN pago.estado_sync IS 'Estado interno del payment-service (PENDIENTE/EN_PROCESO/COMPLETADO/FALLIDO/REEMBOLSADO)';
