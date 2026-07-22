-- =============================================================================
-- PETCare Home Services - Script 03: Restricciones (Constraints)
-- Base de Datos: petcaredb
-- Esquema: petcare
-- Contenido: PRIMARY KEYs, FOREIGN KEYs, UNIQUEs, CHECKs, NOT NULLs
-- =============================================================================

\connect petcaredb
SET search_path TO petcare, public;

-- =============================================================================
-- PRIMARY KEYS (ya definidas en CREATE TABLE, incluidas por referencia)
-- =============================================================================

-- Las PRIMARY KEYs ya fueron definidas en el script 02_tables.sql
-- Se incluyen aquí solo como referencia documental:
-- estado_reserva(id), ciudad(id), estado(id), direccion(id), persona(id),
-- rol(id), usuario(id), usuario_rol(id), menu(id), submenu(id), permiso(id),
-- especie(id), raza(id), mascota(id), cliente(id), proveedor(id),
-- proveedor_especialidad(id), sucursal(id), servicio(id), disponibilidad(id),
-- reserva(id), vacuna(id), registro_vacunacion(id), promocion(id),
-- promocion_local(id), pago(id), notificacion(id), bitacora(id), correo_enviado(id)

-- =============================================================================
-- FOREIGN KEYS
-- =============================================================================

-- FK: estado -> ciudad
ALTER TABLE petcare.estado
    ADD CONSTRAINT fk_estado_ciudad
    FOREIGN KEY (ciudad_id)
    REFERENCES petcare.ciudad(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: direccion -> ciudad
ALTER TABLE petcare.direccion
    ADD CONSTRAINT fk_direccion_ciudad
    FOREIGN KEY (ciudad_id)
    REFERENCES petcare.ciudad(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: direccion -> estado
ALTER TABLE petcare.direccion
    ADD CONSTRAINT fk_direccion_estado
    FOREIGN KEY (estado_id)
    REFERENCES petcare.estado(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: persona -> direccion
ALTER TABLE petcare.persona
    ADD CONSTRAINT fk_persona_direccion
    FOREIGN KEY (direccion_id)
    REFERENCES petcare.direccion(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: usuario -> persona
ALTER TABLE petcare.usuario
    ADD CONSTRAINT fk_usuario_persona
    FOREIGN KEY (persona_id)
    REFERENCES petcare.persona(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: usuario_rol -> usuario
ALTER TABLE petcare.usuario_rol
    ADD CONSTRAINT fk_usuario_rol_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES petcare.usuario(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: usuario_rol -> rol
ALTER TABLE petcare.usuario_rol
    ADD CONSTRAINT fk_usuario_rol_rol
    FOREIGN KEY (rol_id)
    REFERENCES petcare.rol(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: submenu -> menu
ALTER TABLE petcare.submenu
    ADD CONSTRAINT fk_submenu_menu
    FOREIGN KEY (menu_id)
    REFERENCES petcare.menu(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: permiso -> rol
ALTER TABLE petcare.permiso
    ADD CONSTRAINT fk_permiso_rol
    FOREIGN KEY (rol_id)
    REFERENCES petcare.rol(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: permiso -> menu
ALTER TABLE petcare.permiso
    ADD CONSTRAINT fk_permiso_menu
    FOREIGN KEY (menu_id)
    REFERENCES petcare.menu(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: permiso -> submenu
ALTER TABLE petcare.permiso
    ADD CONSTRAINT fk_permiso_submenu
    FOREIGN KEY (submenu_id)
    REFERENCES petcare.submenu(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: raza -> especie
ALTER TABLE petcare.raza
    ADD CONSTRAINT fk_raza_especie
    FOREIGN KEY (especie_id)
    REFERENCES petcare.especie(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: mascota -> especie
ALTER TABLE petcare.mascota
    ADD CONSTRAINT fk_mascota_especie
    FOREIGN KEY (especie_id)
    REFERENCES petcare.especie(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: mascota -> raza
ALTER TABLE petcare.mascota
    ADD CONSTRAINT fk_mascota_raza
    FOREIGN KEY (raza_id)
    REFERENCES petcare.raza(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: mascota -> cliente
ALTER TABLE petcare.mascota
    ADD CONSTRAINT fk_mascota_cliente
    FOREIGN KEY (cliente_id)
    REFERENCES petcare.cliente(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: cliente -> persona
ALTER TABLE petcare.cliente
    ADD CONSTRAINT fk_cliente_persona
    FOREIGN KEY (persona_id)
    REFERENCES petcare.persona(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: cliente -> usuario
ALTER TABLE petcare.cliente
    ADD CONSTRAINT fk_cliente_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES petcare.usuario(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: proveedor -> persona
ALTER TABLE petcare.proveedor
    ADD CONSTRAINT fk_proveedor_persona
    FOREIGN KEY (persona_id)
    REFERENCES petcare.persona(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: proveedor -> usuario
ALTER TABLE petcare.proveedor
    ADD CONSTRAINT fk_proveedor_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES petcare.usuario(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: proveedor_especialidad -> proveedor
ALTER TABLE petcare.proveedor_especialidad
    ADD CONSTRAINT fk_proveedor_especialidad_proveedor
    FOREIGN KEY (proveedor_id)
    REFERENCES petcare.proveedor(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: proveedor_especialidad -> servicio
ALTER TABLE petcare.proveedor_especialidad
    ADD CONSTRAINT fk_proveedor_especialidad_servicio
    FOREIGN KEY (servicio_id)
    REFERENCES petcare.servicio(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: sucursal -> direccion
ALTER TABLE petcare.sucursal
    ADD CONSTRAINT fk_sucursal_direccion
    FOREIGN KEY (direccion_id)
    REFERENCES petcare.direccion(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: disponibilidad -> proveedor
ALTER TABLE petcare.disponibilidad
    ADD CONSTRAINT fk_disponibilidad_proveedor
    FOREIGN KEY (proveedor_id)
    REFERENCES petcare.proveedor(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: disponibilidad -> servicio
ALTER TABLE petcare.disponibilidad
    ADD CONSTRAINT fk_disponibilidad_servicio
    FOREIGN KEY (servicio_id)
    REFERENCES petcare.servicio(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: reserva -> cliente
ALTER TABLE petcare.reserva
    ADD CONSTRAINT fk_reserva_cliente
    FOREIGN KEY (cliente_id)
    REFERENCES petcare.cliente(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: reserva -> proveedor
ALTER TABLE petcare.reserva
    ADD CONSTRAINT fk_reserva_proveedor
    FOREIGN KEY (proveedor_id)
    REFERENCES petcare.proveedor(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- FK: reserva -> servicio
ALTER TABLE petcare.reserva
    ADD CONSTRAINT fk_reserva_servicio
    FOREIGN KEY (servicio_id)
    REFERENCES petcare.servicio(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: reserva -> mascota
ALTER TABLE petcare.reserva
    ADD CONSTRAINT fk_reserva_mascota
    FOREIGN KEY (mascota_id)
    REFERENCES petcare.mascota(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: reserva -> estado_reserva
ALTER TABLE petcare.reserva
    ADD CONSTRAINT fk_reserva_estado_reserva
    FOREIGN KEY (estado_reserva_id)
    REFERENCES petcare.estado_reserva(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: registro_vacunacion -> mascota
ALTER TABLE petcare.registro_vacunacion
    ADD CONSTRAINT fk_registro_vacunacion_mascota
    FOREIGN KEY (mascota_id)
    REFERENCES petcare.mascota(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: registro_vacunacion -> vacuna
ALTER TABLE petcare.registro_vacunacion
    ADD CONSTRAINT fk_registro_vacunacion_vacuna
    FOREIGN KEY (vacuna_id)
    REFERENCES petcare.vacuna(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: promocion_local -> promocion
ALTER TABLE petcare.promocion_local
    ADD CONSTRAINT fk_promocion_local_promocion
    FOREIGN KEY (promocion_id)
    REFERENCES petcare.promocion(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: promocion_local -> reserva
ALTER TABLE petcare.promocion_local
    ADD CONSTRAINT fk_promocion_local_reserva
    FOREIGN KEY (reserva_id)
    REFERENCES petcare.reserva(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: pago -> reserva
ALTER TABLE petcare.pago
    ADD CONSTRAINT fk_pago_reserva
    FOREIGN KEY (reserva_id)
    REFERENCES petcare.reserva(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- FK: notificacion -> usuario
ALTER TABLE petcare.notificacion
    ADD CONSTRAINT fk_notificacion_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES petcare.usuario(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: bitacora -> usuario
ALTER TABLE petcare.bitacora
    ADD CONSTRAINT fk_bitacora_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES petcare.usuario(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- =============================================================================
-- UNIQUE CONSTRAINTS
-- =============================================================================

-- Unicidad de username en usuario
ALTER TABLE petcare.usuario
    ADD CONSTRAINT uk_usuario_username
    UNIQUE (username);

-- Unicidad de CI en persona
ALTER TABLE petcare.persona
    ADD CONSTRAINT uk_persona_ci
    UNIQUE (ci);

-- Unicidad de email en persona (solo si no es nulo)
ALTER TABLE petcare.persona
    ADD CONSTRAINT uk_persona_email
    UNIQUE (email);

-- Unicidad de usuario y rol en usuario_rol
ALTER TABLE petcare.usuario_rol
    ADD CONSTRAINT uk_usuario_rol
    UNIQUE (usuario_id, rol_id);

-- Unicidad de código de reserva
ALTER TABLE petcare.reserva
    ADD CONSTRAINT uk_reserva_codigo
    UNIQUE (codigo);

-- Unicidad de código de promoción
ALTER TABLE petcare.promocion
    ADD CONSTRAINT uk_promocion_codigo
    UNIQUE (codigo);

-- Unicidad de nombre de rol
ALTER TABLE petcare.rol
    ADD CONSTRAINT uk_rol_nombre
    UNIQUE (nombre);

-- Unicidad de nombre de especie
ALTER TABLE petcare.especie
    ADD CONSTRAINT uk_especie_nombre
    UNIQUE (nombre);

-- Unicidad de nombre de ciudad
ALTER TABLE petcare.ciudad
    ADD CONSTRAINT uk_ciudad_nombre
    UNIQUE (nombre);

-- Unicidad de código de ciudad
ALTER TABLE petcare.ciudad
    ADD CONSTRAINT uk_ciudad_codigo
    UNIQUE (codigo);

-- Unicidad de proveedor y servicio en proveedor_especialidad
ALTER TABLE petcare.proveedor_especialidad
    ADD CONSTRAINT uk_proveedor_especialidad
    UNIQUE (proveedor_id, servicio_id);

-- Unicidad de nombre de servicio
ALTER TABLE petcare.servicio
    ADD CONSTRAINT uk_servicio_nombre
    UNIQUE (nombre);

-- Unicidad de nombre de vacuna
ALTER TABLE petcare.vacuna
    ADD CONSTRAINT uk_vacuna_nombre
    UNIQUE (nombre);

-- Unicidad de nombre de menú
ALTER TABLE petcare.menu
    ADD CONSTRAINT uk_menu_nombre
    UNIQUE (nombre);

-- Unicidad de nombre de submenú dentro de un menú
ALTER TABLE petcare.submenu
    ADD CONSTRAINT uk_submenu_nombre_menu
    UNIQUE (nombre, menu_id);

-- Unicidad de permiso por rol, menú y submenú
ALTER TABLE petcare.permiso
    ADD CONSTRAINT uk_permiso_rol_menu_submenu
    UNIQUE (rol_id, menu_id, submenu_id);

-- =============================================================================
-- CHECK CONSTRAINTS
-- =============================================================================

-- Check: género válido en persona
ALTER TABLE petcare.persona
    ADD CONSTRAINT chk_persona_genero
    CHECK (genero IN ('M', 'F', 'O'));

-- Check: género válido en mascota
ALTER TABLE petcare.mascota
    ADD CONSTRAINT chk_mascota_genero
    CHECK (genero IN ('M', 'F', 'O'));

-- Check: tipo de descuento válido en promoción
ALTER TABLE petcare.promocion
    ADD CONSTRAINT chk_promocion_tipo_descuento
    CHECK (tipo_descuento IN ('PERCENTAGE', 'FIXED'));

-- Check: valor de descuento no negativo
ALTER TABLE petcare.promocion
    ADD CONSTRAINT chk_promocion_valor_descuento
    CHECK (valor_descuento >= 0);

-- Check: usos actuales no negativo
ALTER TABLE petcare.promocion
    ADD CONSTRAINT chk_promocion_usos_actuales
    CHECK (usos_actuales >= 0);

-- Check: límite de usos no negativo (si se define)
ALTER TABLE petcare.promocion
    ADD CONSTRAINT chk_promocion_limite_usos
    CHECK (limite_usos IS NULL OR limite_usos >= 0);

-- Check: fecha fin mayor que fecha inicio en promoción
ALTER TABLE petcare.promocion
    ADD CONSTRAINT chk_promocion_fechas
    CHECK (fecha_fin > fecha_inicio);

-- Check: método de pago válido
ALTER TABLE petcare.pago
    ADD CONSTRAINT chk_pago_metodo_pago
    CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'QR', 'OTRO'));

-- Check: estado de pago válido
ALTER TABLE petcare.pago
    ADD CONSTRAINT chk_pago_estado_pago
    CHECK (estado_pago IN ('PENDIENTE', 'PROCESADO', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO'));

-- Check: monto de pago no negativo
ALTER TABLE petcare.pago
    ADD CONSTRAINT chk_pago_monto
    CHECK (monto >= 0);

-- Check: precio total de reserva no negativo
ALTER TABLE petcare.reserva
    ADD CONSTRAINT chk_reserva_precio_total
    CHECK (precio_total >= 0);

-- Check: día de semana válido en disponibilidad (0-6)
ALTER TABLE petcare.disponibilidad
    ADD CONSTRAINT chk_disponibilidad_dia_semana
    CHECK (dia_semana BETWEEN 0 AND 6);

-- Check: hora fin mayor que hora inicio en disponibilidad
ALTER TABLE petcare.disponibilidad
    ADD CONSTRAINT chk_disponibilidad_horario
    CHECK (hora_fin > hora_inicio);

-- Check: duración del servicio no negativa
ALTER TABLE petcare.servicio
    ADD CONSTRAINT chk_servicio_duracion
    CHECK (duracion_minutos > 0);

-- Check: precio base no negativo
ALTER TABLE petcare.servicio
    ADD CONSTRAINT chk_servicio_precio_base
    CHECK (precio_base >= 0);

-- Check: calificación del proveedor entre 0 y 5
ALTER TABLE petcare.proveedor
    ADD CONSTRAINT chk_proveedor_calificacion
    CHECK (calificacion >= 0 AND calificacion <= 5);

-- Check: radio de cobertura no negativo
ALTER TABLE petcare.proveedor
    ADD CONSTRAINT chk_proveedor_radio_cobertura
    CHECK (radio_cobertura_km >= 0);

-- Check: intentos fallidos no negativo
ALTER TABLE petcare.usuario
    ADD CONSTRAINT chk_usuario_intentos_fallidos
    CHECK (intentos_fallidos >= 0);

-- Check: peso de mascota no negativo
ALTER TABLE petcare.mascota
    ADD CONSTRAINT chk_mascota_peso
    CHECK (peso IS NULL OR peso >= 0);

-- Check: tipo de notificación válido
ALTER TABLE petcare.notificacion
    ADD CONSTRAINT chk_notificacion_tipo
    CHECK (tipo IN ('INFO', 'ADVERTENCIA', 'ERROR', 'EXITO', 'SISTEMA'));

-- Check: tipo de correo válido
ALTER TABLE petcare.correo_enviado
    ADD CONSTRAINT chk_correo_enviado_tipo
    CHECK (tipo IN ('TRANSACCIONAL', 'MARKETING', 'NOTIFICACION', 'SISTEMA'));

-- Check: estado de correo válido
ALTER TABLE petcare.correo_enviado
    ADD CONSTRAINT chk_correo_enviado_estado
    CHECK (estado IN ('PENDIENTE', 'ENVIADO', 'FALLIDO'));

-- Check: periodicidad de vacuna no negativa
ALTER TABLE petcare.vacuna
    ADD CONSTRAINT chk_vacuna_periodicidad
    CHECK (periodicidad_meses IS NULL OR periodicidad_meses > 0);

-- Check: horario de sucursal (apertura antes que cierre)
ALTER TABLE petcare.sucursal
    ADD CONSTRAINT chk_sucursal_horario
    CHECK (horario_cierre > horario_apertura);

-- Check: orden de menú no negativo
ALTER TABLE petcare.menu
    ADD CONSTRAINT chk_menu_orden
    CHECK (orden >= 0);

-- Check: orden de submenú no negativo
ALTER TABLE petcare.submenu
    ADD CONSTRAINT chk_submenu_orden
    CHECK (orden >= 0);

-- Check: orden de estado_reserva no negativo
ALTER TABLE petcare.estado_reserva
    ADD CONSTRAINT chk_estado_reserva_orden
    CHECK (orden >= 0);

-- Check: monto de descuento no negativo
ALTER TABLE petcare.promocion_local
    ADD CONSTRAINT chk_promocion_local_monto
    CHECK (monto_descuento >= 0);

-- =============================================================================
-- NOT NULL CONSTRAINTS (columnas adicionales que requieren NOT NULL)
-- =============================================================================

-- persona: nombre, primer_apellido, ci, genero ya son NOT NULL en CREATE TABLE
-- Se agregan validaciones adicionales:

ALTER TABLE petcare.estado_reserva
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN orden SET NOT NULL;

ALTER TABLE petcare.ciudad
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN codigo SET NOT NULL;

ALTER TABLE petcare.estado
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN codigo SET NOT NULL;

ALTER TABLE petcare.direccion
    ALTER COLUMN calle SET NOT NULL;

ALTER TABLE petcare.rol
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.usuario
    ALTER COLUMN username SET NOT NULL,
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE petcare.menu
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN orden SET NOT NULL;

ALTER TABLE petcare.submenu
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN orden SET NOT NULL;

ALTER TABLE petcare.permiso
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.especie
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.raza
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.mascota
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.servicio
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN duracion_minutos SET NOT NULL,
    ALTER COLUMN precio_base SET NOT NULL;

ALTER TABLE petcare.reserva
    ALTER COLUMN codigo SET NOT NULL,
    ALTER COLUMN fecha_reserva SET NOT NULL,
    ALTER COLUMN fecha_inicio SET NOT NULL,
    ALTER COLUMN hora_inicio SET NOT NULL,
    ALTER COLUMN precio_total SET NOT NULL;

ALTER TABLE petcare.vacuna
    ALTER COLUMN nombre SET NOT NULL;

ALTER TABLE petcare.registro_vacunacion
    ALTER COLUMN fecha_aplicacion SET NOT NULL;

ALTER TABLE petcare.promocion
    ALTER COLUMN codigo SET NOT NULL,
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN tipo_descuento SET NOT NULL,
    ALTER COLUMN valor_descuento SET NOT NULL,
    ALTER COLUMN fecha_inicio SET NOT NULL,
    ALTER COLUMN fecha_fin SET NOT NULL;

ALTER TABLE petcare.promocion_local
    ALTER COLUMN monto_descuento SET NOT NULL;

ALTER TABLE petcare.pago
    ALTER COLUMN monto SET NOT NULL,
    ALTER COLUMN metodo_pago SET NOT NULL,
    ALTER COLUMN estado_pago SET NOT NULL;

ALTER TABLE petcare.notificacion
    ALTER COLUMN titulo SET NOT NULL,
    ALTER COLUMN mensaje SET NOT NULL;

ALTER TABLE petcare.bitacora
    ALTER COLUMN accion SET NOT NULL,
    ALTER COLUMN entidad SET NOT NULL;

ALTER TABLE petcare.correo_enviado
    ALTER COLUMN destino SET NOT NULL,
    ALTER COLUMN asunto SET NOT NULL,
    ALTER COLUMN cuerpo SET NOT NULL;

-- =============================================================================
-- TRIGGER para updated_at automático
-- =============================================================================

-- Función para actualizar updated_at
CREATE OR REPLACE FUNCTION petcare.fn_actualizar_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION petcare.fn_actualizar_updated_at() IS 'Función trigger que actualiza el campo updated_at automáticamente';

-- Aplicar trigger a todas las tablas con updated_at
DO $$
DECLARE
    tabla TEXT;
BEGIN
    FOR tabla IN
        SELECT t.tablename
        FROM pg_tables t
        WHERE t.schemaname = 'petcare'
          AND t.tablename != 'bitacora'
    LOOP
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'petcare'
              AND table_name = tabla
              AND column_name = 'updated_at'
        ) THEN
            EXECUTE format(
                'CREATE TRIGGER trg_%s_updated_at
                 BEFORE UPDATE ON petcare.%I
                 FOR EACH ROW
                 EXECUTE FUNCTION petcare.fn_actualizar_updated_at()',
                tabla, tabla
            );
            RAISE NOTICE 'Trigger trg_%_updated_at creado', tabla;
        END IF;
    END LOOP;
END $$;

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
BEGIN
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Todas las restricciones creadas exitosamente';
    RAISE NOTICE '  - FOREIGN KEYs: 35+';
    RAISE NOTICE '  - UNIQUE: 14+';
    RAISE NOTICE '  - CHECK: 20+';
    RAISE NOTICE '  - NOT NULL: Aplicados';
    RAISE NOTICE '  - TRIGGERS: updated_at automático';
    RAISE NOTICE '============================================================';
END $$;
