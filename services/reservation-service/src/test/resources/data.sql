-- Datos semilla para tests de reservation-service

INSERT INTO estado_reserva (id, nombre, color, orden) VALUES
    (1, 'PENDIENTE', '#f59e0b', 1),
    (2, 'CONFIRMADA', '#10b981', 2),
    (3, 'RECHAZADA', '#ef4444', 3),
    (4, 'CANCELADA', '#6b7280', 4);

INSERT INTO persona (id, nombre, primer_apellido, ci, telefono, email) VALUES
    (1, 'Juan', 'Perez', '123456', '77711122', 'juan@petcare.bo'),
    (2, 'Ana', 'Gomez', '654321', '77722233', 'ana@petcare.bo'),
    (3, 'Maria', 'Lopez', '111222', '77733344', 'maria@petcare.bo');

INSERT INTO rol (id, nombre) VALUES (1, 'ADMINISTRADOR'), (2, 'CLIENTE'), (3, 'PROVEEDOR');

INSERT INTO usuario (id, username, password, persona_id) VALUES
    (1, 'juan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1),
    (2, 'ana', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 2),
    (3, 'maria', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 3);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1), (2, 2), (3, 3);

INSERT INTO cliente (id, persona_id, usuario_id) VALUES (1, 2, 2);

INSERT INTO proveedor (id, persona_id, usuario_id, empresa) VALUES (1, 3, 3, 'VetPet SRL');

INSERT INTO servicio (id, nombre, duracion_minutos, precio_base) VALUES
    (1, 'Consulta general', 30, 80.00),
    (2, 'Peluqueria', 60, 150.00);

INSERT INTO especie (id, nombre) VALUES (1, 'Perro'), (2, 'Gato');

INSERT INTO mascota (id, nombre, especie_id, cliente_id) VALUES (1, 'Rex', 1, 1), (2, 'Michi', 2, 1);
