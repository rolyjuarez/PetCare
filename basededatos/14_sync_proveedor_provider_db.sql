-- =============================================================================
-- 14_sync_proveedor_provider_db.sql
-- Sincroniza el proveedor "prov1" (TODO'S PET) desde petcaredb hacia
-- petcaredb_provider para que el provider-service pueda gestionar sus
-- servicios y promociones.
-- Idempotente: si el usuario prov1 ya existe, no ejecuta nada.
--
-- Uso: psql -h localhost -U postgres -d petcaredb_provider -f 14_sync_proveedor_provider_db.sql
-- =============================================================================

\connect petcaredb_provider

DO $$
DECLARE
    v_persona_id    BIGINT;
    v_direccion_id  BIGINT;
    v_usuario_id    BIGINT;
    v_proveedor_id  BIGINT;
    v_ps_id         BIGINT;
BEGIN
    IF EXISTS (SELECT 1 FROM usuario WHERE username = 'prov1' AND deleted = FALSE) THEN
        RAISE NOTICE 'prov1 ya existe en petcaredb_provider; no se ejecuta nada';
        RETURN;
    END IF;

    -- Avanzar secuencias al siguiente id libre (el seed usa ids explícitos sin setval)
    PERFORM setval('direccion_id_seq',               GREATEST((SELECT COALESCE(MAX(id),0) FROM direccion),               1));
    PERFORM setval('persona_id_seq',                 GREATEST((SELECT COALESCE(MAX(id),0) FROM persona),                 1));
    PERFORM setval('usuario_id_seq',                 GREATEST((SELECT COALESCE(MAX(id),0) FROM usuario),                 1));
    PERFORM setval('usuario_rol_id_seq',             GREATEST((SELECT COALESCE(MAX(id),0) FROM usuario_rol),             1));
    PERFORM setval('proveedor_id_seq',               GREATEST((SELECT COALESCE(MAX(id),0) FROM proveedor),               1));
    PERFORM setval('proveedor_especialidad_id_seq',  GREATEST((SELECT COALESCE(MAX(id),0) FROM proveedor_especialidad),  1));
    PERFORM setval('proveedor_servicio_id_seq',      GREATEST((SELECT COALESCE(MAX(id),0) FROM proveedor_servicio),      1));
    PERFORM setval('proveedor_servicio_modalidad_id_seq', GREATEST((SELECT COALESCE(MAX(id),0) FROM proveedor_servicio_modalidad), 1));
    PERFORM setval('promocion_id_seq',               GREATEST((SELECT COALESCE(MAX(id),0) FROM promocion),               1));

    -- Dirección (ciudad 6 = Potosí en el catálogo copiado)
    INSERT INTO direccion (calle, numero, piso, apartamento, latitud, longitud, referencia, ciudad_id, estado_id)
    VALUES ('murillo', '1332', NULL, NULL, -19.5968654, -65.7478213, 'entre calle a y b', 6, NULL)
    RETURNING id INTO v_direccion_id;

    -- Persona (mismos datos que petcaredb.persona id=6)
    INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero, direccion_id)
    VALUES ('JUAN', 'PINTO', 'MAMANI', '124124412', '7425876', 'juarez.roly@gmail.com', '2026-07-10', 'M', v_direccion_id)
    RETURNING id INTO v_persona_id;

    -- Usuario (mismo username y password que petcaredb.usuario id=6)
    INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado)
    VALUES ('prov1', '$2a$10$T6QmV3JFq.boVviiYGgBfOzG3vMT0ScEVhKrt55YZqYddVFe0rnU.', v_persona_id, TRUE, 0, FALSE)
    RETURNING id INTO v_usuario_id;

    -- Rol PROVEEDOR (rol_id=3)
    INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (v_usuario_id, 3);

    -- Perfil de proveedor
    INSERT INTO proveedor (persona_id, usuario_id, empresa, latitud, longitud, radio_cobertura_km, descripcion, verificado, calificacion)
    VALUES (v_persona_id, v_usuario_id, 'TODO''S PET', -19.5968654, -65.7478213, 10.00, NULL, FALSE, 0.00)
    RETURNING id INTO v_proveedor_id;

    -- Especialidades (referencias al catálogo global servicio: 1,2,3,15)
    INSERT INTO proveedor_especialidad (proveedor_id, servicio_id, requiere_certificado)
    VALUES
        (v_proveedor_id, 1,  FALSE),
        (v_proveedor_id, 2,  FALSE),
        (v_proveedor_id, 3,  FALSE),
        (v_proveedor_id, 15, FALSE);

    -- Servicios propios (copiados de petcaredb.proveedor_servicio de proveedor 3)
    INSERT INTO proveedor_servicio (proveedor_id, nombre, descripcion, categoria, duracion_minutos, precio_base, requiere_certificado, activo)
    VALUES
        (v_proveedor_id, 'Peluquería Básica',     'Corte y baño básico',                  'PELUQUERIA', 60, 80.00,  FALSE, TRUE),
        (v_proveedor_id, 'Peluquería Premium',    'Corte, baño y spa',                    'PELUQUERIA', 90, 120.00, FALSE, TRUE),
        (v_proveedor_id, 'Baño Terapéutico',      'Baño con productos especiales',        'PELUQUERIA', 45, 100.00, FALSE, TRUE),
        (v_proveedor_id, 'Estética Canina',       'Servicio completo de estética',        'PELUQUERIA', 45, 90.00,  FALSE, TRUE);

    -- Modalidades para cada servicio nuevo (EN_ESTABLECIMIENTO 0, RECOGIDA_ENTREGA 20, DOMICILIO 30)
    FOR v_ps_id IN SELECT id FROM proveedor_servicio WHERE proveedor_id = v_proveedor_id LOOP
        INSERT INTO proveedor_servicio_modalidad (proveedor_servicio_id, modalidad, costo_adicional, activo)
        VALUES
            (v_ps_id, 'EN_ESTABLECIMIENTO', 0.00,  TRUE),
            (v_ps_id, 'RECOGIDA_ENTREGA',   20.00, TRUE),
            (v_ps_id, 'DOMICILIO',          30.00, TRUE);
    END LOOP;

    -- Promociones de ejemplo (una por servicio propio)
    INSERT INTO promocion (codigo, nombre, descripcion, tipo_descuento, valor_descuento, fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales, proveedor_id, servicio_id)
    SELECT 'TODO-PELU-10', 'Peluquería Básica 10%', 'Descuento de bienvenida en el servicio básico', 'PERCENTAGE', 10.00, NOW(), NOW() + INTERVAL '60 days', TRUE, 50, 0, v_proveedor_id, id
    FROM proveedor_servicio WHERE proveedor_id = v_proveedor_id AND nombre = 'Peluquería Básica';

    INSERT INTO promocion (codigo, nombre, descripcion, tipo_descuento, valor_descuento, fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales, proveedor_id, servicio_id)
    SELECT 'TODO-PREMIUM-15', 'Peluquería Premium 15%', 'Descuento en el servicio premium', 'PERCENTAGE', 15.00, NOW(), NOW() + INTERVAL '60 days', TRUE, 50, 0, v_proveedor_id, id
    FROM proveedor_servicio WHERE proveedor_id = v_proveedor_id AND nombre = 'Peluquería Premium';

    RAISE NOTICE 'prov1 sincronizado: proveedor_id=% usuario_id=% persona_id=%', v_proveedor_id, v_usuario_id, v_persona_id;
END $$;
