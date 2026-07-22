-- =============================================================================
-- PETCare Home Services - Script 02: Creación de Tablas
-- Base de Datos: petcaredb
-- Esquema: petcare
-- =============================================================================

-- Conectar a la base de datos
\connect petcaredb

-- Establecer search_path
SET search_path TO petcare, public;

-- =============================================================================
-- TABLA: estado_reserva
-- Meta Indexing: Catálogo de estados del ciclo de vida de una reserva
-- =============================================================================
CREATE TABLE petcare.estado_reserva (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    color           VARCHAR(7),
    icono           VARCHAR(50),
    orden           INTEGER         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.estado_reserva IS 'Catálogo de estados disponibles para las reservas del sistema';
COMMENT ON COLUMN petcare.estado_reserva.color IS 'Color hexadecimal para representación visual del estado';
COMMENT ON COLUMN petcare.estado_reserva.orden IS 'Orden de apresentación en listados y flujos';

-- =============================================================================
-- TABLA: ciudad
-- Meta Indexing: Catálogo de ciudades del sistema
-- =============================================================================
CREATE TABLE petcare.ciudad (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    codigo          VARCHAR(10)     NOT NULL,
    latitud         NUMERIC(10, 7),
    longitud        NUMERIC(10, 7),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.ciudad IS 'Catálogo de ciudades donde opera el sistema PETCare';

-- =============================================================================
-- TABLA: estado
-- Meta Indexing: Catálogo de estados/departamentos por ciudad
-- =============================================================================
CREATE TABLE petcare.estado (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    codigo          VARCHAR(10)     NOT NULL,
    ciudad_id       BIGINT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.estado IS 'Estados o departamentos asociados a ciudades';

-- =============================================================================
-- TABLA: direccion
-- Meta Indexing: Direcciones geográficas del sistema
-- =============================================================================
CREATE TABLE petcare.direccion (
    id              BIGSERIAL       PRIMARY KEY,
    calle           VARCHAR(200)    NOT NULL,
    numero          VARCHAR(20),
    piso            VARCHAR(10),
    apartamento     VARCHAR(20),
    latitud         NUMERIC(10, 7),
    longitud        NUMERIC(10, 7),
    referencia      TEXT,
    ciudad_id       BIGINT          NOT NULL,
    estado_id       BIGINT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.direccion IS 'Direcciones físicas registradas en el sistema';
COMMENT ON COLUMN petcare.direccion.referencia IS 'Referencia adicional para ubicar el punto de entrega';

-- =============================================================================
-- TABLA: persona
-- Meta Indexing: Datos personales base del sistema
-- =============================================================================
CREATE TABLE petcare.persona (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    primer_apellido     VARCHAR(100)    NOT NULL,
    segundo_apellido    VARCHAR(100),
    ci                  VARCHAR(20)     NOT NULL,
    telefono            VARCHAR(20),
    email               VARCHAR(150),
    fecha_nacimiento    DATE,
    genero              CHAR(1)         NOT NULL DEFAULT 'O',
    direccion_id        BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.persona IS 'Registro central de datos personales de todos los actores del sistema';
COMMENT ON COLUMN petcare.persona.ci IS 'Carnet de identidad - único por persona';
COMMENT ON COLUMN petcare.persona.genero IS 'M=Masculino, F=Femenino, O=Otro';

-- =============================================================================
-- TABLA: rol
-- Meta Indexing: Catálogo de roles del sistema
-- =============================================================================
CREATE TABLE petcare.rol (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.rol IS 'Roles asignables a los usuarios del sistema';

-- =============================================================================
-- TABLA: usuario
-- Meta Indexing: Cuentas de acceso al sistema
-- =============================================================================
CREATE TABLE petcare.usuario (
    id                  BIGSERIAL       PRIMARY KEY,
    username            VARCHAR(50)     NOT NULL,
    password            VARCHAR(255)    NOT NULL,
    persona_id          BIGINT          NOT NULL,
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    ultimo_acceso       TIMESTAMP,
    intentos_fallidos   INTEGER         NOT NULL DEFAULT 0,
    bloqueado           BOOLEAN         NOT NULL DEFAULT FALSE,
    token_refresh       TEXT,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.usuario IS 'Cuentas de usuario para autenticación y acceso al sistema';
COMMENT ON COLUMN petcare.usuario.password IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN petcare.usuario.token_refresh IS 'Token JWT de refresco para sesiones';

-- =============================================================================
-- TABLA: usuario_rol
-- Meta Indexing: Asignación de roles a usuarios (N:M)
-- =============================================================================
CREATE TABLE petcare.usuario_rol (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL,
    rol_id          BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.usuario_rol IS 'Relación many-to-many entre usuarios y roles';

-- =============================================================================
-- TABLA: menu
-- Meta Indexing: Menús principales del sistema
-- =============================================================================
CREATE TABLE petcare.menu (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    descripcion     TEXT,
    icono           VARCHAR(50),
    url             VARCHAR(200),
    orden           INTEGER         NOT NULL DEFAULT 0,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.menu IS 'Menús principales de navegación del sistema';

-- =============================================================================
-- TABLA: submenu
-- Meta Indexing: Submenús del sistema
-- =============================================================================
CREATE TABLE petcare.submenu (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    descripcion     TEXT,
    icono           VARCHAR(50),
    url             VARCHAR(200),
    orden           INTEGER         NOT NULL DEFAULT 0,
    menu_id         BIGINT          NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.submenu IS 'Submenús dependientes de un menú principal';

-- =============================================================================
-- TABLA: permiso
-- Meta Indexing: Permisos de acceso por rol y menú
-- =============================================================================
CREATE TABLE petcare.permiso (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(150)    NOT NULL,
    descripcion     TEXT,
    rol_id          BIGINT          NOT NULL,
    menu_id         BIGINT          NOT NULL,
    submenu_id      BIGINT,
    crear           BOOLEAN         NOT NULL DEFAULT FALSE,
    leer            BOOLEAN         NOT NULL DEFAULT FALSE,
    actualizar      BOOLEAN         NOT NULL DEFAULT FALSE,
    eliminar        BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.permiso IS 'Permisos granulares CRUD por rol, menú y submenú';
COMMENT ON COLUMN petcare.permiso.crear IS 'Indica si el rol puede crear registros en esta sección';
COMMENT ON COLUMN petcare.permiso.leer IS 'Indica si el rol puede leer registros en esta sección';
COMMENT ON COLUMN petcare.permiso.actualizar IS 'Indica si el rol puede actualizar registros en esta sección';
COMMENT ON COLUMN petcare.permiso.eliminar IS 'Indica si el rol puede eliminar registros en esta sección';

-- =============================================================================
-- TABLA: especie
-- Meta Indexing: Catálogo de especies de mascotas
-- =============================================================================
CREATE TABLE petcare.especie (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.especie IS 'Catálogo de especies de mascotas (Perro, Gato, etc.)';

-- =============================================================================
-- TABLA: raza
-- Meta Indexing: Catálogo de razas por especie
-- =============================================================================
CREATE TABLE petcare.raza (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    especie_id      BIGINT          NOT NULL,
    descripcion     TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.raza IS 'Razas asociadas a cada especie de mascota';

-- =============================================================================
-- TABLA: mascota
-- Meta Indexing: Registro de mascotas de los clientes
-- =============================================================================
CREATE TABLE petcare.mascota (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    fecha_nacimiento    DATE,
    genero              CHAR(1)         NOT NULL DEFAULT 'O',
    peso                NUMERIC(6, 2),
    color               VARCHAR(50),
    imagen_url          VARCHAR(500),
    especie_id          BIGINT          NOT NULL,
    raza_id             BIGINT,
    cliente_id          BIGINT          NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.mascota IS 'Registro de mascotas asociadas a cada cliente';
COMMENT ON COLUMN petcare.mascota.genero IS 'M=Masculino, F=Femenino, O=Otro';
COMMENT ON COLUMN petcare.mascota.peso IS 'Peso en kilogramos';

-- =============================================================================
-- TABLA: cliente
-- Meta Indexing: Datos específicos del rol cliente
-- =============================================================================
CREATE TABLE petcare.cliente (
    id              BIGSERIAL       PRIMARY KEY,
    persona_id      BIGINT          NOT NULL,
    usuario_id      BIGINT          NOT NULL,
    notas           TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.cliente IS 'Información específica de clientes registrados en el sistema';

-- =============================================================================
-- TABLA: proveedor
-- Meta Indexing: Datos específicos del rol proveedor de servicios
-- =============================================================================
CREATE TABLE petcare.proveedor (
    id                      BIGSERIAL       PRIMARY KEY,
    persona_id              BIGINT          NOT NULL,
    usuario_id              BIGINT          NOT NULL,
    latitud                 NUMERIC(10, 7),
    longitud                NUMERIC(10, 7),
    radio_cobertura_km      NUMERIC(5, 2)   DEFAULT 10.00,
    descripcion             TEXT,
    verificado              BOOLEAN         NOT NULL DEFAULT FALSE,
    calificacion            NUMERIC(3, 2)   DEFAULT 0.00,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.proveedor IS 'Información de proveedores de servicios de cuidado de mascotas';
COMMENT ON COLUMN petcare.proveedor.radio_cobertura_km IS 'Radio de cobertura del proveedor en kilómetros';
COMMENT ON COLUMN petcare.proveedor.calificacion IS 'Calificación promedio de 0.00 a 5.00';

-- =============================================================================
-- TABLA: proveedor_especialidad
-- Meta Indexing: Servicios que ofrece cada proveedor
-- =============================================================================
CREATE TABLE petcare.proveedor_especialidad (
    id              BIGSERIAL       PRIMARY KEY,
    proveedor_id    BIGINT          NOT NULL,
    servicio_id     BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.proveedor_especialidad IS 'Relación N:M entre proveedores y servicios que ofrecen';

-- =============================================================================
-- TABLA: sucursal
-- Meta Indexing: Sucursales físicas del sistema
-- =============================================================================
CREATE TABLE petcare.sucursal (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    direccion_id        BIGINT,
    telefono            VARCHAR(20),
    email               VARCHAR(150),
    horario_apertura    TIME            NOT NULL DEFAULT '08:00:00',
    horario_cierre      TIME            NOT NULL DEFAULT '18:00:00',
    latitud             NUMERIC(10, 7),
    longitud            NUMERIC(10, 7),
    activa              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.sucursal IS 'Sucursales físicas del sistema PETCare';

-- =============================================================================
-- TABLA: servicio
-- Meta Indexing: Catálogo de servicios ofrecidos
-- =============================================================================
CREATE TABLE petcare.servicio (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    descripcion         TEXT,
    duracion_minutos    INTEGER         NOT NULL DEFAULT 60,
    precio_base         NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    imagen_url          VARCHAR(500),
    activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    categoria           VARCHAR(50),
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.servicio IS 'Catálogo de servicios disponibles para mascotas';
COMMENT ON COLUMN petcare.servicio.categoria IS 'Categoría del servicio: PELUQUERIA, PASEO, VETERINARIA, GUARDERIA, etc.';

-- =============================================================================
-- TABLA: disponibilidad
-- Meta Indexing: Horarios disponibles de proveedores por servicio
-- =============================================================================
CREATE TABLE petcare.disponibilidad (
    id              BIGSERIAL       PRIMARY KEY,
    proveedor_id    BIGINT          NOT NULL,
    servicio_id     BIGINT          NOT NULL,
    dia_semana      INTEGER         NOT NULL,
    hora_inicio     TIME            NOT NULL,
    hora_fin        TIME            NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.disponibilidad IS 'Horarios de disponibilidad de proveedores para cada servicio';
COMMENT ON COLUMN petcare.disponibilidad.dia_semana IS '0=Domingo, 1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado';

-- =============================================================================
-- TABLA: reserva
-- Meta Indexing: Reservaciones de servicios
-- =============================================================================
CREATE TABLE petcare.reserva (
    id                      BIGSERIAL       PRIMARY KEY,
    codigo                  VARCHAR(20)     NOT NULL,
    cliente_id              BIGINT          NOT NULL,
    proveedor_id            BIGINT,
    servicio_id             BIGINT          NOT NULL,
    mascota_id              BIGINT          NOT NULL,
    estado_reserva_id       BIGINT          NOT NULL,
    fecha_reserva           TIMESTAMP       NOT NULL DEFAULT NOW(),
    fecha_inicio            TIMESTAMP       NOT NULL,
    fecha_fin               TIMESTAMP,
    hora_inicio             TIME            NOT NULL,
    hora_fin                TIME,
    latitud                 NUMERIC(10, 7),
    longitud                NUMERIC(10, 7),
    direccion_referencia    TEXT,
    notas                   TEXT,
    precio_total            NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.reserva IS 'Registro central de reservas de servicios para mascotas';
COMMENT ON COLUMN petcare.reserva.codigo IS 'Código único de identificación de la reserva';
COMMENT ON COLUMN petcare.reserva.precio_total IS 'Precio total acordado para el servicio';

-- =============================================================================
-- TABLA: vacuna
-- Meta Indexing: Catálogo de vacunas disponibles
-- =============================================================================
CREATE TABLE petcare.vacuna (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    descripcion         TEXT,
    periodicidad_meses  INTEGER,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.vacuna IS 'Catálogo de vacunas para el control sanitario de mascotas';
COMMENT ON COLUMN petcare.vacuna.periodicidad_meses IS 'Frecuencia de aplicación en meses';

-- =============================================================================
-- TABLA: registro_vacunacion
-- Meta Indexing: Historial de vacunación de mascotas
-- =============================================================================
CREATE TABLE petcare.registro_vacunacion (
    id                  BIGSERIAL       PRIMARY KEY,
    mascota_id          BIGINT          NOT NULL,
    vacuna_id           BIGINT          NOT NULL,
    fecha_aplicacion    DATE            NOT NULL,
    fecha_vencimiento   DATE,
    lote                VARCHAR(50),
    veterinario         VARCHAR(150),
    observaciones       TEXT,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.registro_vacunacion IS 'Historial completo de vacunación de cada mascota';
COMMENT ON COLUMN petcare.registro_vacunacion.lote IS 'Número de lote de la vacuna aplicada';

-- =============================================================================
-- TABLA: promocion
-- Meta Indexing: Promociones y descuentos del sistema
-- =============================================================================
CREATE TABLE petcare.promocion (
    id                  BIGSERIAL       PRIMARY KEY,
    codigo              VARCHAR(30)     NOT NULL,
    nombre              VARCHAR(100)    NOT NULL,
    descripcion         TEXT,
    tipo_descuento      VARCHAR(20)     NOT NULL,
    valor_descuento     NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    fecha_inicio        TIMESTAMP       NOT NULL,
    fecha_fin           TIMESTAMP       NOT NULL,
    activa              BOOLEAN         NOT NULL DEFAULT TRUE,
    limite_usos         INTEGER,
    usos_actuales       INTEGER         NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.promocion IS 'Promociones y ofertas disponibles en el sistema';
COMMENT ON COLUMN petcare.promocion.tipo_descuento IS 'PERCENTAGE=Porcentaje, FIXED=Monto fijo';
COMMENT ON COLUMN petcare.promocion.limite_usos IS 'Número máximo de veces que se puede usar la promoción';

-- =============================================================================
-- TABLA: promocion_local
-- Meta Indexing: Aplicación de promociones a reservas específicas
-- =============================================================================
CREATE TABLE petcare.promocion_local (
    id                  BIGSERIAL       PRIMARY KEY,
    promocion_id        BIGINT          NOT NULL,
    reserva_id          BIGINT          NOT NULL,
    monto_descuento     NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    fecha_aplicacion    TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.promocion_local IS 'Registro de promociones aplicadas a reservas específicas';

-- =============================================================================
-- TABLA: pago
-- Meta Indexing: Registro de pagos de reservas
-- =============================================================================
CREATE TABLE petcare.pago (
    id                      BIGSERIAL       PRIMARY KEY,
    reserva_id              BIGINT          NOT NULL,
    monto                   NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    metodo_pago             VARCHAR(30)     NOT NULL,
    estado_pago             VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    referencia_transaccion  VARCHAR(100),
    fecha_pago              TIMESTAMP,
    comprobante_url         VARCHAR(500),
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.pago IS 'Registro de transacciones de pago asociadas a reservas';
COMMENT ON COLUMN petcare.pago.metodo_pago IS 'EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, QR, etc.';
COMMENT ON COLUMN petcare.pago.estado_pago IS 'PENDIENTE, PROCESADO, COMPLETADO, FALLIDO, REEMBOLSADO';

-- =============================================================================
-- TABLA: notificacion
-- Meta Indexing: Notificaciones para usuarios del sistema
-- =============================================================================
CREATE TABLE petcare.notificacion (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL,
    titulo          VARCHAR(200)    NOT NULL,
    mensaje         TEXT            NOT NULL,
    tipo            VARCHAR(30)     NOT NULL DEFAULT 'INFO',
    leida           BOOLEAN         NOT NULL DEFAULT FALSE,
    fecha_lectura   TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.notificacion IS 'Notificaciones enviadas a los usuarios del sistema';
COMMENT ON COLUMN petcare.notificacion.tipo IS 'INFO, ADVERTENCIA, ERROR, EXITO, SISTEMA';

-- =============================================================================
-- TABLA: bitacora
-- Meta Indexing: Registro de auditoría del sistema
-- =============================================================================
CREATE TABLE petcare.bitacora (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT,
    accion          VARCHAR(50)     NOT NULL,
    entidad         VARCHAR(100)    NOT NULL,
    entidad_id      BIGINT,
    datos_anteriores TEXT,
    datos_nuevos    TEXT,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE petcare.bitacora IS 'Bitácora de auditoría para trazabilidad de acciones en el sistema';
COMMENT ON COLUMN petcare.bitacora.accion IS 'INSERT, UPDATE, DELETE, LOGIN, LOGOUT, etc.';
COMMENT ON COLUMN petcare.bitacora.datos_anteriores IS 'JSON con los valores antes del cambio';
COMMENT ON COLUMN petcare.bitacora.datos_nuevos IS 'JSON con los valores después del cambio';

-- =============================================================================
-- TABLA: correo_enviado
-- Meta Indexing: Historial de correos electrónicos enviados
-- =============================================================================
CREATE TABLE petcare.correo_enviado (
    id              BIGSERIAL       PRIMARY KEY,
    destino         VARCHAR(150)    NOT NULL,
    asunto          VARCHAR(200)    NOT NULL,
    cuerpo          TEXT            NOT NULL,
    tipo            VARCHAR(30)     NOT NULL DEFAULT 'TRANSACCIONAL',
    estado          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    error_mensaje   TEXT,
    fecha_envio     TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE petcare.correo_enviado IS 'Historial de correos electrónicos enviados desde el sistema';
COMMENT ON COLUMN petcare.correo_enviado.tipo IS 'TRANSACCIONAL, MARKETING, NOTIFICACION, SISTEMA';
COMMENT ON COLUMN petcare.correo_enviado.estado IS 'PENDIENTE, ENVIADO, FALLIDO';

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
BEGIN
    RAISE NOTICE '============================================================';
    RAISE NOTICE '29 tablas creadas exitosamente en esquema petcare';
    RAISE NOTICE '============================================================';
END $$;
