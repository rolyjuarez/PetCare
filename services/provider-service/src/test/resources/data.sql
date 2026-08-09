-- Datos semilla para tests de provider-service
-- Idempotente: limpia primero (dos contextos de test comparten la misma H2)

DELETE FROM evento_procesado;
DELETE FROM solicitud_reserva;
DELETE FROM promocion;
DELETE FROM proveedor_servicio;
DELETE FROM proveedor_especialidad;
DELETE FROM proveedor;
DELETE FROM servicio;
DELETE FROM usuario_rol;
DELETE FROM usuario;
DELETE FROM rol;
DELETE FROM persona;

INSERT INTO persona (id, nombre, primer_apellido, ci, telefono, email) VALUES
    (1, 'Juan', 'Perez', '123456', '77711122', 'juan@petcare.bo'),
    (2, 'Ana', 'Gomez', '654321', '77722233', 'ana@petcare.bo'),
    (3, 'Maria', 'Lopez', '111222', '77733344', 'maria@petcare.bo'),
    (4, 'Carlos', 'Torres', '333444', '77755566', 'carlos@petcare.bo');

INSERT INTO rol (id, nombre) VALUES (1, 'ADMINISTRADOR'), (2, 'CLIENTE'), (3, 'PROVEEDOR');

INSERT INTO usuario (id, username, password, persona_id) VALUES
    (1, 'juan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1),
    (2, 'ana', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 2),
    (3, 'maria', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 3),
    (4, 'carlos', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 4);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1), (2, 2), (3, 3), (4, 3);

INSERT INTO proveedor (id, persona_id, usuario_id, empresa) VALUES
    (1, 3, 3, 'VetPet SRL'),
    (2, 4, 4, 'PetCare Clínica');

INSERT INTO servicio (id, nombre, duracion_minutos, precio_base) VALUES
    (1, 'Consulta general', 30, 80.00),
    (2, 'Peluqueria', 60, 150.00);

INSERT INTO proveedor_especialidad (proveedor_id, servicio_id) VALUES
    (1, 1), (1, 2), (2, 1);

INSERT INTO proveedor_servicio (id, proveedor_id, nombre, categoria, precio_base) VALUES
    (1, 1, 'Consulta general', 'VETERINARIA', 80.00),
    (2, 1, 'Peluqueria', 'PELUQUERIA', 150.00);

INSERT INTO promocion (id, proveedor_id, servicio_id, codigo, nombre, descripcion, tipo_descuento, valor_descuento,
                       fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales) VALUES
    (1, 1, 1, 'TEST-10', 'Consulta 10%', '10% en consulta general', 'PERCENTAGE', 10.00,
     DATEADD('DAY', -1, NOW()), DATEADD('DAY', 30, NOW()), TRUE, 100, 0),
    (2, 1, 2, 'TEST-FIX', 'Peluqueria 20 Bs', '20 Bs en peluqueria', 'FIXED', 20.00,
     DATEADD('DAY', -1, NOW()), DATEADD('DAY', 30, NOW()), TRUE, NULL, 0),
    (3, 2, 1, 'TEST-EXP', 'Expirada', 'Promocion expirada', 'PERCENTAGE', 5.00,
     DATEADD('DAY', -30, NOW()), DATEADD('DAY', -1, NOW()), TRUE, NULL, 0);

ALTER TABLE promocion ALTER COLUMN id RESTART WITH 100;
