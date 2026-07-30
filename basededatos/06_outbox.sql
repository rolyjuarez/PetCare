-- =============================================
-- Migration: Outbox Table for Event-Driven Architecture
-- Version: 1.0
-- Description: Stores domain events for reliable
--              asynchronous delivery via Kafka
-- =============================================

CREATE TABLE IF NOT EXISTS outbox (
    id              BIGSERIAL PRIMARY KEY,
    event_id        VARCHAR(64)  NOT NULL,
    aggregate_type  VARCHAR(50)  NOT NULL,
    aggregate_id    BIGINT,
    event_type      VARCHAR(100) NOT NULL,
    topic           VARCHAR(100) NOT NULL,
    payload         TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count     INTEGER      NOT NULL DEFAULT 0,
    last_error      TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    processed_at    TIMESTAMP,
    next_retry_at   TIMESTAMP
);

-- Indexes for outbox processing
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox(status) WHERE status = 'PENDING';
CREATE INDEX IF NOT EXISTS idx_outbox_next_retry ON outbox(next_retry_at) WHERE next_retry_at IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_outbox_created ON outbox(created_at);
CREATE INDEX IF NOT EXISTS idx_outbox_event_id ON outbox(event_id);

COMMENT ON TABLE outbox IS 'Outbox pattern table for reliable event publishing to Kafka';
COMMENT ON COLUMN outbox.event_id IS 'Unique event identifier (UUID)';
COMMENT ON COLUMN outbox.aggregate_type IS 'Domain aggregate type (RESERVA, PAGO, USUARIO, MASCOTA)';
COMMENT ON COLUMN outbox.event_type IS 'Fully qualified event class name';
COMMENT ON COLUMN outbox.topic IS 'Target Kafka topic';
COMMENT ON COLUMN outbox.payload IS 'JSON-serialized event payload';
COMMENT ON COLUMN outbox.status IS 'PENDING | PROCESSING | PROCESSED | FAILED';
COMMENT ON COLUMN outbox.retry_count IS 'Number of delivery attempts';
COMMENT ON COLUMN outbox.last_error IS 'Last error message for debugging';
COMMENT ON COLUMN outbox.next_retry_at IS 'Timestamp for next scheduled retry';
