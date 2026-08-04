-- 08_certificados.sql
-- Certificado de vacunacion obligatorio por proveedor+servicio, y adjunto en registro_vacunacion.

ALTER TABLE proveedor_especialidad ADD COLUMN requiere_certificado BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE registro_vacunacion ADD COLUMN certificado_url VARCHAR(500);

ALTER TABLE reserva ADD COLUMN registro_vacunacion_id BIGINT REFERENCES registro_vacunacion(id);

ALTER TABLE solicitud_reserva ADD COLUMN registro_vacunacion_id BIGINT REFERENCES registro_vacunacion(id);
