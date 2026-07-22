-- =============================================================================
-- PETCare Home Services - Script 04: Índices Optimizados
-- Base de Datos: petcaredb
-- Esquema: petcare
-- Optimización: Login, Reservas, Mascotas, Búsqueda proveedores,
--               Promociones, Disponibilidad, Historial, Pagos, Notificaciones
-- =============================================================================

\connect petcaredb
SET search_path TO petcare, public;

-- =============================================================================
-- Meta Indexing: Índices de Autenticación y Login
-- Optimización: Acceso rápido por username y validación de credenciales
-- =============================================================================
CREATE INDEX idx_usuario_username
    ON petcare.usuario (username)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_username_password
    ON petcare.usuario (username, password)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_activo
    ON petcare.usuario (activo)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_bloqueado
    ON petcare.usuario (bloqueado)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_persona_id
    ON petcare.usuario (persona_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_ultimo_acceso
    ON petcare.usuario (ultimo_acceso DESC NULLS LAST);

COMMENT ON INDEX petcare.idx_usuario_username IS 'Búsqueda de usuario por username para login';
COMMENT ON INDEX petcare.idx_usuario_username_password IS 'Validación completa de credenciales para login';

-- =============================================================================
-- Meta Indexing: Índices de Reservas
-- Optimización: Consultas por cliente, proveedor, estado, fechas y código
-- =============================================================================
CREATE INDEX idx_reserva_cliente_id
    ON petcare.reserva (cliente_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_proveedor_id
    ON petcare.reserva (proveedor_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_estado_reserva_id
    ON petcare.reserva (estado_reserva_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_servicio_id
    ON petcare.reserva (servicio_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_mascota_id
    ON petcare.reserva (mascota_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_codigo
    ON petcare.reserva (codigo)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_fecha_reserva
    ON petcare.reserva (fecha_reserva DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_fecha_inicio
    ON petcare.reserva (fecha_inicio DESC)
    WHERE deleted = FALSE;

-- Índices compuestos para consultas frecuentes
CREATE INDEX idx_reserva_estado_fecha
    ON petcare.reserva (estado_reserva_id, fecha_reserva DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_proveedor_fecha
    ON petcare.reserva (proveedor_id, fecha_inicio)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_cliente_fecha
    ON petcare.reserva (cliente_id, fecha_reserva DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_cliente_estado
    ON petcare.reserva (cliente_id, estado_reserva_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_proveedor_estado
    ON petcare.reserva (proveedor_id, estado_reserva_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_reserva_fecha_estado
    ON petcare.reserva (fecha_inicio, estado_reserva_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_reserva_cliente_id IS 'Historial de reservas por cliente';
COMMENT ON INDEX petcare.idx_reserva_proveedor_id IS 'Reservas asignadas a un proveedor';
COMMENT ON INDEX petcare.idx_reserva_estado_fecha IS 'Reservas por estado y fecha para dashboards';
COMMENT ON INDEX petcare.idx_reserva_proveedor_fecha IS 'Disponibilidad del proveedor por fechas';

-- =============================================================================
-- Meta Indexing: Índices de Mascotas
-- Optimización: Consultas por cliente, especie y raza
-- =============================================================================
CREATE INDEX idx_mascota_cliente_id
    ON petcare.mascota (cliente_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_mascota_especie_id
    ON petcare.mascota (especie_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_mascota_raza_id
    ON petcare.mascota (raza_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_mascota_especie_raza
    ON petcare.mascota (especie_id, raza_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_mascota_nombre
    ON petcare.mascota (nombre)
    WHERE deleted = FALSE;

CREATE INDEX idx_mascota_cliente_especie
    ON petcare.mascota (cliente_id, especie_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_mascota_cliente_id IS 'Mascotas registradas por cliente';
COMMENT ON INDEX petcare.idx_mascota_especie_raza IS 'Filtrado por especie y raza';

-- =============================================================================
-- Meta Indexing: Índices de Búsqueda de Proveedores
-- Optimización: Búsqueda geográfica, por verificación y especialidad
-- =============================================================================
CREATE INDEX idx_proveedor_latitud_longitud
    ON petcare.proveedor (latitud, longitud)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_verificado
    ON petcare.proveedor (verificado)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_calificacion
    ON petcare.proveedor (calificacion DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_radio_cobertura
    ON petcare.proveedor (radio_cobertura_km)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_persona_id
    ON petcare.proveedor (persona_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_usuario_id
    ON petcare.proveedor (usuario_id)
    WHERE deleted = FALSE;

CREATE INDEX idxProveedorEspecialidadProveedorId
    ON petcare.proveedor_especialidad (proveedor_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_especialidad_servicio_id
    ON petcare.proveedor_especialidad (servicio_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_proveedor_especialidad_proveedor_servicio
    ON petcare.proveedor_especialidad (proveedor_id, servicio_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_proveedor_latitud_longitud IS 'Búsqueda geográfica de proveedores por coordenadas';
COMMENT ON INDEX petcare.idxProveedorEspecialidadProveedorId IS 'Especialidades de un proveedor específico';

-- =============================================================================
-- Meta Indexing: Índices de Promociones
-- Optimización: Búsqueda por código, fechas y estado de actividad
-- =============================================================================
CREATE INDEX idx_promocion_codigo
    ON petcare.promocion (codigo)
    WHERE deleted = FALSE;

CREATE INDEX idx_promocion_fechas
    ON petcare.promocion (fecha_inicio, fecha_fin)
    WHERE deleted = FALSE;

CREATE INDEX idx_promocion_activa_fechas
    ON petcare.promocion (activa, fecha_inicio, fecha_fin)
    WHERE deleted = FALSE;

CREATE INDEX idx_promocion_tipo_descuento
    ON petcare.promocion (tipo_descuento)
    WHERE deleted = FALSE;

CREATE INDEX idx_promocion_activa
    ON petcare.promocion (activa)
    WHERE deleted = FALSE;

-- Promociones aplicadas a reservas
CREATE INDEX idx_promocion_local_promocion_id
    ON petcare.promocion_local (promocion_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_promocion_local_reserva_id
    ON petcare.promocion_local (reserva_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_promocion_codigo IS 'Búsqueda de promoción por código de descuento';
COMMENT ON INDEX petcare.idx_promocion_activa_fechas IS 'Promociones vigentes activas por rango de fechas';

-- =============================================================================
-- Meta Indexing: Índices de Disponibilidad
-- Optimización: Consulta de horarios por proveedor y servicio
-- =============================================================================
CREATE INDEX idx_disponibilidad_proveedor_servicio
    ON petcare.disponibilidad (proveedor_id, servicio_id)
    WHERE deleted = FALSE AND activo = TRUE;

CREATE INDEX idx_disponibilidad_proveedor_id
    ON petcare.disponibilidad (proveedor_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_disponibilidad_servicio_id
    ON petcare.disponibilidad (servicio_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_disponibilidad_dia_semana
    ON petcare.disponibilidad (dia_semana)
    WHERE deleted = FALSE AND activo = TRUE;

CREATE INDEX idx_disponibilidad_proveedor_dia
    ON petcare.disponibilidad (proveedor_id, dia_semana)
    WHERE deleted = FALSE AND activo = TRUE;

COMMENT ON INDEX petcare.idx_disponibilidad_proveedor_servicio IS 'Horarios de un proveedor para un servicio específico';

-- =============================================================================
-- Meta Indexing: Índices de Historial y Auditoría
-- Optimización: Consultas de historial de vacunación y bitácora
-- =============================================================================
CREATE INDEX idx_registro_vacunacion_mascota
    ON petcare.registro_vacunacion (mascota_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_registro_vacunacion_vacuna_id
    ON petcare.registro_vacunacion (vacuna_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_registro_vacunacion_fecha
    ON petcare.registro_vacunacion (fecha_aplicacion DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_registro_vacunacion_mascota_fecha
    ON petcare.registro_vacunacion (mascota_id, fecha_aplicacion DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_registro_vacunacion_vencimiento
    ON petcare.registro_vacunacion (fecha_vencimiento)
    WHERE deleted = FALSE;

-- Bitácora de auditoría
CREATE INDEX idx_bitacora_usuario_fecha
    ON petcare.bitacora (usuario_id, created_at DESC);

CREATE INDEX idx_bitacora_entidad
    ON petcare.bitacora (entidad, entidad_id);

CREATE INDEX idx_bitacora_entidad_fecha
    ON petcare.bitacora (entidad, created_at DESC);

CREATE INDEX idx_bitacora_usuario_id
    ON petcare.bitacora (usuario_id);

CREATE INDEX idx_bitacora_accion
    ON petcare.bitacora (accion);

CREATE INDEX idx_bitacora_created_at
    ON petcare.bitacora (created_at DESC);

COMMENT ON INDEX petcare.idx_registro_vacunacion_mascota IS 'Historial de vacunación por mascota';
COMMENT ON INDEX petcare.idx_bitacora_usuario_fecha IS 'Auditoría de acciones por usuario y fecha';
COMMENT ON INDEX petcare.idx_bitacora_entidad IS 'Historial de cambios por entidad específica';

-- =============================================================================
-- Meta Indexing: Índices de Pagos
-- Optimización: Consultas de estado de pago y historial financiero
-- =============================================================================
CREATE INDEX idx_pago_reserva_id
    ON petcare.pago (reserva_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_pago_estado
    ON petcare.pago (estado_pago)
    WHERE deleted = FALSE;

CREATE INDEX idx_pago_metodo_pago
    ON petcare.pago (metodo_pago)
    WHERE deleted = FALSE;

CREATE INDEX idx_pago_fecha_pago
    ON petcare.pago (fecha_pago DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_pago_reserva_estado
    ON petcare.pago (reserva_id, estado_pago)
    WHERE deleted = FALSE;

CREATE INDEX idx_pago_estado_fecha
    ON petcare.pago (estado_pago, fecha_pago DESC)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_pago_reserva_id IS 'Pago asociado a una reserva específica';
COMMENT ON INDEX petcare.idx_pago_estado IS 'Filtrado de pagos por estado';

-- =============================================================================
-- Meta Indexing: Índices de Notificaciones
-- Optimización: Consultas de notificaciones pendientes por usuario
-- =============================================================================
CREATE INDEX idx_notificacion_usuario_leida
    ON petcare.notificacion (usuario_id, leida)
    WHERE deleted = FALSE;

CREATE INDEX idx_notificacion_usuario_id
    ON petcare.notificacion (usuario_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_notificacion_fecha
    ON petcare.notificacion (created_at DESC)
    WHERE deleted = FALSE;

CREATE INDEX idx_notificacion_tipo
    ON petcare.notificacion (tipo)
    WHERE deleted = FALSE;

CREATE INDEX idx_notificacion_usuario_no_leidas
    ON petcare.notificacion (usuario_id, created_at DESC)
    WHERE deleted = FALSE AND leida = FALSE;

COMMENT ON INDEX petcare.idx_notificacion_usuario_leida IS 'Notificaciones por usuario y estado de lectura';
COMMENT ON INDEX petcare.idx_notificacion_usuario_no_leidas IS 'Notificaciones no leídas por usuario (inbox)';

-- =============================================================================
-- Meta Indexing: Índices de Correo Electrónico
-- Optimización: Historial de correos y seguimiento de envíos
-- =============================================================================
CREATE INDEX idx_correo_enviado_destino
    ON petcare.correo_enviado (destino)
    WHERE deleted = FALSE;

CREATE INDEX idx_correo_enviado_estado
    ON petcare.correo_enviado (estado)
    WHERE deleted = FALSE;

CREATE INDEX idx_correo_enviado_tipo
    ON petcare.correo_enviado (tipo)
    WHERE deleted = FALSE;

CREATE INDEX idx_correo_enviado_fecha_envio
    ON petcare.correo_enviado (fecha_envio DESC)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_correo_enviado_estado IS 'Seguimiento de correos pendientes de envío';

-- =============================================================================
-- Meta Indexing: Índices de Personas y Usuarios
-- Optimización: Búsqueda por CI, email y relaciones
-- =============================================================================
CREATE INDEX idx_persona_ci
    ON petcare.persona (ci)
    WHERE deleted = FALSE;

CREATE INDEX idx_persona_email
    ON petcare.persona (email)
    WHERE deleted = FALSE;

CREATE INDEX idx_persona_nombre
    ON petcare.persona (nombre, primer_apellido)
    WHERE deleted = FALSE;

CREATE INDEX idx_persona_telefono
    ON petcare.persona (telefono)
    WHERE deleted = FALSE;

CREATE INDEX idx_persona_direccion_id
    ON petcare.persona (direccion_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_persona_ci IS 'Búsqueda de persona por carnet de identidad';
COMMENT ON INDEX petcare.idx_persona_email IS 'Búsqueda de persona por email';

-- =============================================================================
-- Meta Indexing: Índices de Clientes
-- Optimización: Relación cliente-persona-usuario
-- =============================================================================
CREATE INDEX idx_cliente_persona_id
    ON petcare.cliente (persona_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_cliente_usuario_id
    ON petcare.cliente (usuario_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_cliente_persona_id IS 'Búsqueda de cliente por persona asociada';

-- =============================================================================
-- Meta Indexing: Índices de Catálogos
-- Optimización: Consultas frecuentes en formularios y filtros
-- =============================================================================

-- Razas por especie
CREATE INDEX idx_raza_especie_id
    ON petcare.raza (especie_id)
    WHERE deleted = FALSE;

-- Servicios activos
CREATE INDEX idx_servicio_activo
    ON petcare.servicio (activo)
    WHERE deleted = FALSE;

CREATE INDEX idx_servicio_categoria
    ON petcare.servicio (categoria)
    WHERE deleted = FALSE;

-- Estados de reserva por orden
CREATE INDEX idx_estado_reserva_orden
    ON petcare.estado_reserva (orden)
    WHERE deleted = FALSE;

-- Roles activos
CREATE INDEX idx_rol_activo
    ON petcare.rol (activo)
    WHERE deleted = FALSE;

-- Permisos por rol
CREATE INDEX idx_permiso_rol_id
    ON petcare.permiso (rol_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_permiso_menu_id
    ON petcare.permiso (menu_id)
    WHERE deleted = FALSE;

-- Menús activos por orden
CREATE INDEX idx_menu_activo_orden
    ON petcare.menu (activo, orden)
    WHERE deleted = FALSE;

CREATE INDEX idx_submenu_menu_id
    ON petcare.submenu (menu_id)
    WHERE deleted = FALSE;

-- Sucursales activas
CREATE INDEX idx_sucursal_activa
    ON petcare.sucursal (activa)
    WHERE deleted = FALSE;

-- Ciudades
CREATE INDEX idx_ciudad_codigo
    ON petcare.ciudad (codigo)
    WHERE deleted = FALSE;

-- Direcciones por ciudad
CREATE INDEX idx_direccion_ciudad_id
    ON petcare.direccion (ciudad_id)
    WHERE deleted = FALSE;

-- Usuario_rol
CREATE INDEX idx_usuario_rol_usuario_id
    ON petcare.usuario_rol (usuario_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_usuario_rol_rol_id
    ON petcare.usuario_rol (rol_id)
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_raza_especie_id IS 'Razas disponibles por especie';
COMMENT ON INDEX petcare.idx_servicio_activo IS 'Servicios activos para el catálogo';
COMMENT ON INDEX petcare.idx_permiso_rol_id IS 'Permisos asignados a un rol';

-- =============================================================================
-- Índices GIN para búsquedas de texto completo (opcional)
-- =============================================================================

-- Búsqueda de texto en descripción de servicios
CREATE INDEX idx_servicio_busqueda_texto
    ON petcare.servicio USING gin (
        to_tsvector('spanish', COALESCE(nombre, '') || ' ' || COALESCE(descripcion, ''))
    )
    WHERE deleted = FALSE;

-- Búsqueda de texto en notas de reserva
CREATE INDEX idx_reserva_busqueda_texto
    ON petcare.reserva USING gin (
        to_tsvector('spanish', COALESCE(notas, '') || ' ' || COALESCE(direccion_referencia, ''))
    )
    WHERE deleted = FALSE;

COMMENT ON INDEX petcare.idx_servicio_busqueda_texto IS 'Búsqueda de texto completo en nombre y descripción de servicios';
COMMENT ON INDEX petcare.idx_reserva_busqueda_texto IS 'Búsqueda de texto completo en notas de reserva';

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
DECLARE
    total_indices INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO total_indices
    FROM pg_indexes
    WHERE schemaname = 'petcare';

    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Índices creados exitosamente en esquema petcare';
    RAISE NOTICE 'Total de índices: %', total_indices;
    RAISE NOTICE '============================================================';
END $$;
