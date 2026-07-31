-- =====================================================================
-- 07_microservicios.sql
-- Migración para extraer el módulo de reserva a microservicios:
--   reservation-service (8081) y provider-service (8082)
-- =====================================================================

-- 1. Adaptar tabla reserva: soporte de respuesta del proveedor
ALTER TABLE reserva ADD COLUMN IF NOT EXISTS motivo_rechazo    TEXT;
ALTER TABLE reserva ADD COLUMN IF NOT EXISTS respuesta_en      TIMESTAMP;

-- 2. Proyección del provider-service: reservas solicitadas por servicio
CREATE TABLE IF NOT EXISTS solicitud_reserva (
    id                BIGSERIAL       PRIMARY KEY,
    reserva_id        BIGINT          NOT NULL,
    codigo            VARCHAR(20)     NOT NULL,
    cliente_id        BIGINT          NOT NULL,
    cliente_nombre    VARCHAR(255),
    proveedor_id      BIGINT          NOT NULL,
    proveedor_empresa VARCHAR(150),
    servicio_id       BIGINT          NOT NULL,
    servicio_nombre   VARCHAR(100),
    mascota_id        BIGINT,
    mascota_nombre    VARCHAR(100),
    fecha_inicio      TIMESTAMP,
    hora_inicio       TIME,
    precio_total      NUMERIC(10, 2),
    estado            VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo    TEXT,
    creada_en         TIMESTAMP       NOT NULL DEFAULT NOW(),
    respondida_en     TIMESTAMP,
    CONSTRAINT uk_solicitud_reserva_reserva_proveedor UNIQUE (reserva_id, proveedor_id)
);

CREATE INDEX IF NOT EXISTS idx_solicitud_reserva_proveedor_estado
    ON solicitud_reserva (proveedor_id, estado);
CREATE INDEX IF NOT EXISTS idx_solicitud_reserva_estado
    ON solicitud_reserva (estado);

-- 3. Idempotencia de consumidores Kafka (compartida por los microservicios)
CREATE TABLE IF NOT EXISTS evento_procesado (
    event_id     VARCHAR(64)   PRIMARY KEY,
    event_type   VARCHAR(100)  NOT NULL,
    service      VARCHAR(50)   NOT NULL,
    aggregate_id BIGINT,
    payload      TEXT,
    procesado_en TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- 4. Menú del rol PROVEEDOR: "Mis Reservas"
INSERT INTO menu (nombre, descripcion, icono, url, orden, activo, version)
SELECT 'Proveedor', 'Reservas del proveedor', 'briefcase', NULL, 30, TRUE, 1
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE nombre = 'Proveedor');

INSERT INTO submenu (menu_id, nombre, descripcion, icono, url, orden, activo, version)
SELECT m.id, 'Mis Reservas', 'Reservas solicitadas para tus servicios',
       'calendar-check', '/app/proveedor/reservas', 1, TRUE, 1
FROM menu m
WHERE m.nombre = 'Proveedor'
  AND NOT EXISTS (SELECT 1 FROM submenu s WHERE s.url = '/app/proveedor/reservas');

INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar, version)
SELECT 'Proveedor: Mis Reservas', 'Ver y responder reservas solicitadas', 3, m.id, s.id, TRUE, TRUE, TRUE, FALSE, 1
FROM menu m
JOIN submenu s ON s.menu_id = m.id AND s.url = '/app/proveedor/reservas'
WHERE m.nombre = 'Proveedor'
  AND NOT EXISTS (
      SELECT 1 FROM permiso p
      WHERE p.rol_id = 3 AND p.submenu_id = s.id
  );
