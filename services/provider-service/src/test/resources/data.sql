-- Datos semilla para tests de provider-service

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
