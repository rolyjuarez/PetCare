-- Datos semilla para tests de payment-service

INSERT INTO persona (id, nombre, primer_apellido, ci, telefono, email) VALUES
    (1, 'Juan', 'Perez', '123456', '77711122', 'juan@petcare.bo'),
    (2, 'Ana', 'Gomez', '654321', '77722233', 'ana@petcare.bo');

INSERT INTO rol (id, nombre) VALUES (1, 'ADMINISTRADOR'), (2, 'CLIENTE'), (3, 'PROVEEDOR');

INSERT INTO usuario (id, username, password, persona_id) VALUES
    (1, 'juan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1),
    (2, 'ana', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 2);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1), (2, 2);

INSERT INTO reserva (id, codigo, cliente_id, proveedor_id, servicio_id, mascota_id,
    estado_reserva_id, modalidad_entrega, fecha_inicio, hora_inicio, precio_total) VALUES
    (1, 'RES-00000001', 1, 1, 1, 1, 2, 'DOMICILIO', DATEADD('DAY', 2, CURRENT_DATE), TIME '10:00:00', 100.00),
    (2, 'RES-00000002', 1, 1, 1, 1, 2, 'EN_ESTABLECIMIENTO', DATEADD('DAY', 2, CURRENT_DATE), TIME '11:00:00', 100.00);

INSERT INTO proveedor_servicio (id, proveedor_id, nombre, categoria, duracion_minutos, precio_base) VALUES
    (1, 1, 'Consulta general', 'VETERINARIA', 30, 80.00);

INSERT INTO proveedor_servicio_modalidad (proveedor_servicio_id, modalidad, costo_adicional) VALUES
    (1, 'DOMICILIO', 20.00),
    (1, 'EN_ESTABLECIMIENTO', 0.00);

INSERT INTO promocion (id, proveedor_id, codigo, nombre, tipo_descuento, valor_descuento,
    fecha_inicio, fecha_fin, activa) VALUES
    (1, 1, 'PROMO-10', 'Promo 10%', 'PERCENTAGE', 10.00,
     DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 30, CURRENT_TIMESTAMP), TRUE);
