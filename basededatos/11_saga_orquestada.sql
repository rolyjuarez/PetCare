-- =====================================================================
-- 11_saga_orquestada.sql
-- Migración para el patrón saga orquestada:
--   saga-orchestrator (8085) coordina reserva + proveedor + pago
-- =====================================================================

-- 1. Estado global de cada saga de reserva (controlado por el orquestador)
CREATE TABLE IF NOT EXISTS saga_estado (
    id               BIGSERIAL       PRIMARY KEY,
    reserva_id       BIGINT          NOT NULL,
    codigo_reserva   VARCHAR(20)     NOT NULL,
    estado           VARCHAR(30)     NOT NULL,
    paso_actual      VARCHAR(255),
    motivo           TEXT,
    ultimo_evento_id VARCHAR(64),
    creada_en        TIMESTAMP       NOT NULL DEFAULT NOW(),
    actualizada_en   TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_saga_estado_reserva_id UNIQUE (reserva_id)
);

CREATE INDEX IF NOT EXISTS idx_saga_estado_estado
    ON saga_estado (estado);

-- 2. La tabla evento_procesado (idempotencia) ya existe desde 07_microservicios.sql;
--    el saga-orchestrator la comparte escribiendo con service = 'saga-orchestrator'.
