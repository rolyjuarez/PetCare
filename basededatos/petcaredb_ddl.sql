-- =============================================================================
-- PETCare Home Services - DDL Completo (Unificado)
-- Base de Datos: petcaredb
-- Esquema: public
-- Descripcion: Script unificado con todas las CREATE TABLE, constraints inline
--              (PRIMARY KEY, FOREIGN KEY, UNIQUE, CHECK), y triggers.
-- Uso: Ejecutar este unico archivo para recrear el esquema completo.
-- =============================================================================

\connect petcaredb
SET search_path TO public;

-- =============================================================================
-- TABLA: estado_reserva
-- =============================================================================
CREATE TABLE estado_reserva (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    color           VARCHAR(7),
    icono           VARCHAR(50),
    orden           INTEGER         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT chk_estado_reserva_orden CHECK (orden >= 0)
);

COMMENT ON TABLE estado_reserva IS 'Catalogo de estados disponibles para las reservas del sistema';
COMMENT ON COLUMN estado_reserva.color IS 'Color hexadecimal para representacion visual del estado';
COMMENT ON COLUMN estado_reserva.orden IS 'Orden de apresentacion en listados y flujos';

-- =============================================================================
-- TABLA: ciudad
-- =============================================================================
CREATE TABLE ciudad (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    codigo          VARCHAR(10)     NOT NULL,
    latitud         NUMERIC(10, 7),
    longitud        NUMERIC(10, 7),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_ciudad_nombre UNIQUE (nombre),
    CONSTRAINT uk_ciudad_codigo UNIQUE (codigo)
);

COMMENT ON TABLE ciudad IS 'Catalogo de ciudades donde opera el sistema PETCare';

-- =============================================================================
-- TABLA: estado
-- =============================================================================
CREATE TABLE estado (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    codigo          VARCHAR(10)     NOT NULL,
    ciudad_id       BIGINT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_estado_ciudad
        FOREIGN KEY (ciudad_id) REFERENCES ciudad(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE estado IS 'Estados o departamentos asociados a ciudades';

-- =============================================================================
-- TABLA: direccion
-- =============================================================================
CREATE TABLE direccion (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_direccion_ciudad
        FOREIGN KEY (ciudad_id) REFERENCES ciudad(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_direccion_estado
        FOREIGN KEY (estado_id) REFERENCES estado(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE direccion IS 'Direcciones fisicas registradas en el sistema';
COMMENT ON COLUMN direccion.referencia IS 'Referencia adicional para ubicar el punto de entrega';

-- =============================================================================
-- TABLA: persona
-- =============================================================================
CREATE TABLE persona (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_persona_direccion
        FOREIGN KEY (direccion_id) REFERENCES direccion(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT uk_persona_ci UNIQUE (ci),
    CONSTRAINT uk_persona_email UNIQUE (email),
    CONSTRAINT chk_persona_genero CHECK (genero IN ('M', 'F', 'O'))
);

COMMENT ON TABLE persona IS 'Registro central de datos personales de todos los actores del sistema';
COMMENT ON COLUMN persona.ci IS 'Carnet de identidad - unico por persona';
COMMENT ON COLUMN persona.genero IS 'M=Masculino, F=Femenino, O=Otro';

-- =============================================================================
-- TABLA: rol
-- =============================================================================
CREATE TABLE rol (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

COMMENT ON TABLE rol IS 'Roles asignables a los usuarios del sistema';

-- =============================================================================
-- TABLA: usuario
-- =============================================================================
CREATE TABLE usuario (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_usuario_persona
        FOREIGN KEY (persona_id) REFERENCES persona(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT chk_usuario_intentos_fallidos CHECK (intentos_fallidos >= 0)
);

COMMENT ON TABLE usuario IS 'Cuentas de usuario para autenticacion y acceso al sistema';
COMMENT ON COLUMN usuario.password IS 'Contrasena hasheada con BCrypt';
COMMENT ON COLUMN usuario.token_refresh IS 'Token JWT de refresco para sesiones';

-- =============================================================================
-- TABLA: usuario_rol
-- =============================================================================
CREATE TABLE usuario_rol (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL,
    rol_id          BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (rol_id) REFERENCES rol(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_usuario_rol UNIQUE (usuario_id, rol_id)
);

COMMENT ON TABLE usuario_rol IS 'Relacion many-to-many entre usuarios y roles';

-- =============================================================================
-- TABLA: menu
-- =============================================================================
CREATE TABLE menu (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_menu_nombre UNIQUE (nombre),
    CONSTRAINT chk_menu_orden CHECK (orden >= 0)
);

COMMENT ON TABLE menu IS 'Menus principales de navegacion del sistema';

-- =============================================================================
-- TABLA: submenu
-- =============================================================================
CREATE TABLE submenu (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_submenu_menu
        FOREIGN KEY (menu_id) REFERENCES menu(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_submenu_nombre_menu UNIQUE (nombre, menu_id),
    CONSTRAINT chk_submenu_orden CHECK (orden >= 0)
);

COMMENT ON TABLE submenu IS 'Submenus dependientes de un menu principal';

-- =============================================================================
-- TABLA: permiso
-- =============================================================================
CREATE TABLE permiso (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_permiso_rol
        FOREIGN KEY (rol_id) REFERENCES rol(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_permiso_menu
        FOREIGN KEY (menu_id) REFERENCES menu(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_permiso_submenu
        FOREIGN KEY (submenu_id) REFERENCES submenu(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT uk_permiso_rol_menu_submenu UNIQUE (rol_id, menu_id, submenu_id)
);

COMMENT ON TABLE permiso IS 'Permisos granulares CRUD por rol, menu y submenu';
COMMENT ON COLUMN permiso.crear IS 'Indica si el rol puede crear registros en esta seccion';
COMMENT ON COLUMN permiso.leer IS 'Indica si el rol puede leer registros en esta seccion';
COMMENT ON COLUMN permiso.actualizar IS 'Indica si el rol puede actualizar registros en esta seccion';
COMMENT ON COLUMN permiso.eliminar IS 'Indica si el rol puede eliminar registros en esta seccion';

-- =============================================================================
-- TABLA: especie
-- =============================================================================
CREATE TABLE especie (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_especie_nombre UNIQUE (nombre)
);

COMMENT ON TABLE especie IS 'Catalogo de especies de mascotas (Perro, Gato, etc.)';

-- =============================================================================
-- TABLA: raza
-- =============================================================================
CREATE TABLE raza (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    especie_id      BIGINT          NOT NULL,
    descripcion     TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_raza_especie
        FOREIGN KEY (especie_id) REFERENCES especie(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE raza IS 'Razas asociadas a cada especie de mascota';

-- =============================================================================
-- TABLA: cliente
-- =============================================================================
CREATE TABLE cliente (
    id              BIGSERIAL       PRIMARY KEY,
    persona_id      BIGINT          NOT NULL,
    usuario_id      BIGINT          NOT NULL,
    notas           TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_cliente_persona
        FOREIGN KEY (persona_id) REFERENCES persona(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE cliente IS 'Informacion especifica de clientes registrados en el sistema';

-- =============================================================================
-- TABLA: mascota
-- =============================================================================
CREATE TABLE mascota (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_mascota_especie
        FOREIGN KEY (especie_id) REFERENCES especie(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_mascota_raza
        FOREIGN KEY (raza_id) REFERENCES raza(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_mascota_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_mascota_genero CHECK (genero IN ('M', 'F', 'O')),
    CONSTRAINT chk_mascota_peso CHECK (peso IS NULL OR peso >= 0)
);

COMMENT ON TABLE mascota IS 'Registro de mascotas asociadas a cada cliente';
COMMENT ON COLUMN mascota.genero IS 'M=Masculino, F=Femenino, O=Otro';
COMMENT ON COLUMN mascota.peso IS 'Peso en kilogramos';

-- =============================================================================
-- TABLA: proveedor
-- =============================================================================
CREATE TABLE proveedor (
    id                      BIGSERIAL       PRIMARY KEY,
    persona_id              BIGINT          NOT NULL,
    usuario_id              BIGINT          NOT NULL,
    empresa                 VARCHAR(150),
    latitud                 NUMERIC(10, 7),
    longitud                NUMERIC(10, 7),
    radio_cobertura_km      NUMERIC(5, 2)   DEFAULT 10.00,
    descripcion             TEXT,
    verificado              BOOLEAN         NOT NULL DEFAULT FALSE,
    calificacion            NUMERIC(3, 2)   DEFAULT 0.00,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_proveedor_persona
        FOREIGN KEY (persona_id) REFERENCES persona(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_proveedor_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_proveedor_calificacion CHECK (calificacion >= 0 AND calificacion <= 5),
    CONSTRAINT chk_proveedor_radio_cobertura CHECK (radio_cobertura_km >= 0)
);

COMMENT ON TABLE proveedor IS 'Informacion de proveedores de servicios de cuidado de mascotas';
COMMENT ON COLUMN proveedor.radio_cobertura_km IS 'Radio de cobertura del proveedor en kilometros';
COMMENT ON COLUMN proveedor.calificacion IS 'Calificacion promedio de 0.00 a 5.00';

-- =============================================================================
-- TABLA: servicio
-- =============================================================================
CREATE TABLE servicio (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_servicio_nombre UNIQUE (nombre),
    CONSTRAINT chk_servicio_duracion CHECK (duracion_minutos > 0),
    CONSTRAINT chk_servicio_precio_base CHECK (precio_base >= 0)
);

COMMENT ON TABLE servicio IS 'Catalogo de servicios disponibles para mascotas';
COMMENT ON COLUMN servicio.categoria IS 'Categoria del servicio: PELUQUERIA, PASEO, VETERINARIA, GUARDERIA, etc.';

-- =============================================================================
-- TABLA: proveedor_especialidad
-- =============================================================================
CREATE TABLE proveedor_especialidad (
    id              BIGSERIAL       PRIMARY KEY,
    proveedor_id    BIGINT          NOT NULL,
    servicio_id     BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_proveedor_especialidad_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_proveedor_especialidad_servicio
        FOREIGN KEY (servicio_id) REFERENCES servicio(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_proveedor_especialidad UNIQUE (proveedor_id, servicio_id)
);

COMMENT ON TABLE proveedor_especialidad IS 'Relacion N:M entre proveedores y servicios que ofrecen';

-- =============================================================================
-- TABLA: sucursal
-- =============================================================================
CREATE TABLE sucursal (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_sucursal_direccion
        FOREIGN KEY (direccion_id) REFERENCES direccion(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_sucursal_horario CHECK (horario_cierre > horario_apertura)
);

COMMENT ON TABLE sucursal IS 'Sucursales fisicas del sistema PETCare';

-- =============================================================================
-- TABLA: disponibilidad
-- =============================================================================
CREATE TABLE disponibilidad (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_disponibilidad_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_disponibilidad_servicio
        FOREIGN KEY (servicio_id) REFERENCES servicio(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_disponibilidad_dia_semana CHECK (dia_semana BETWEEN 0 AND 6),
    CONSTRAINT chk_disponibilidad_horario CHECK (hora_fin > hora_inicio)
);

COMMENT ON TABLE disponibilidad IS 'Horarios de disponibilidad de proveedores para cada servicio';
COMMENT ON COLUMN disponibilidad.dia_semana IS '0=Domingo, 1=Lunes, 2=Martes, 3=Miercoles, 4=Jueves, 5=Viernes, 6=Sabado';

-- =============================================================================
-- TABLA: reserva
-- =============================================================================
CREATE TABLE reserva (
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
    version                 INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_reserva_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_servicio
        FOREIGN KEY (servicio_id) REFERENCES servicio(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_estado_reserva
        FOREIGN KEY (estado_reserva_id) REFERENCES estado_reserva(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_reserva_codigo UNIQUE (codigo),
    CONSTRAINT chk_reserva_precio_total CHECK (precio_total >= 0)
);

COMMENT ON TABLE reserva IS 'Registro central de reservas de servicios para mascotas';
COMMENT ON COLUMN reserva.codigo IS 'Codigo unico de identificacion de la reserva';
COMMENT ON COLUMN reserva.precio_total IS 'Precio total acordado para el servicio';

-- =============================================================================
-- TABLA: vacuna
-- =============================================================================
CREATE TABLE vacuna (
    id                  BIGSERIAL       PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    descripcion         TEXT,
    periodicidad_meses  INTEGER,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_vacuna_nombre UNIQUE (nombre),
    CONSTRAINT chk_vacuna_periodicidad CHECK (periodicidad_meses IS NULL OR periodicidad_meses > 0)
);

COMMENT ON TABLE vacuna IS 'Catalogo de vacunas para el control sanitario de mascotas';
COMMENT ON COLUMN vacuna.periodicidad_meses IS 'Frecuencia de aplicacion en meses';

-- =============================================================================
-- TABLA: registro_vacunacion
-- =============================================================================
CREATE TABLE registro_vacunacion (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_registro_vacunacion_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_registro_vacunacion_vacuna
        FOREIGN KEY (vacuna_id) REFERENCES vacuna(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE registro_vacunacion IS 'Historial completo de vacunacion de cada mascota';
COMMENT ON COLUMN registro_vacunacion.lote IS 'Numero de lote de la vacuna aplicada';

-- =============================================================================
-- TABLA: promocion
-- =============================================================================
CREATE TABLE promocion (
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
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT uk_promocion_codigo UNIQUE (codigo),
    CONSTRAINT chk_promocion_tipo_descuento CHECK (tipo_descuento IN ('PERCENTAGE', 'FIXED')),
    CONSTRAINT chk_promocion_valor_descuento CHECK (valor_descuento >= 0),
    CONSTRAINT chk_promocion_usos_actuales CHECK (usos_actuales >= 0),
    CONSTRAINT chk_promocion_limite_usos CHECK (limite_usos IS NULL OR limite_usos >= 0),
    CONSTRAINT chk_promocion_fechas CHECK (fecha_fin > fecha_inicio)
);

COMMENT ON TABLE promocion IS 'Promociones y ofertas disponibles en el sistema';
COMMENT ON COLUMN promocion.tipo_descuento IS 'PERCENTAGE=Porcentaje, FIXED=Monto fijo';
COMMENT ON COLUMN promocion.limite_usos IS 'Numero maximo de veces que se puede usar la promocion';

-- =============================================================================
-- TABLA: promocion_local
-- =============================================================================
CREATE TABLE promocion_local (
    id                  BIGSERIAL       PRIMARY KEY,
    promocion_id        BIGINT          NOT NULL,
    reserva_id          BIGINT          NOT NULL,
    monto_descuento     NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    fecha_aplicacion    TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_promocion_local_promocion
        FOREIGN KEY (promocion_id) REFERENCES promocion(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_promocion_local_reserva
        FOREIGN KEY (reserva_id) REFERENCES reserva(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_promocion_local_monto CHECK (monto_descuento >= 0)
);

COMMENT ON TABLE promocion_local IS 'Registro de promociones aplicadas a reservas especificas';

-- =============================================================================
-- TABLA: pago
-- =============================================================================
CREATE TABLE pago (
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
    version                 INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_pago_reserva
        FOREIGN KEY (reserva_id) REFERENCES reserva(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_pago_metodo_pago CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'QR', 'OTRO')),
    CONSTRAINT chk_pago_estado_pago CHECK (estado_pago IN ('PENDIENTE', 'PROCESADO', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO')),
    CONSTRAINT chk_pago_monto CHECK (monto >= 0)
);

COMMENT ON TABLE pago IS 'Registro de transacciones de pago asociadas a reservas';
COMMENT ON COLUMN pago.metodo_pago IS 'EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, QR, etc.';
COMMENT ON COLUMN pago.estado_pago IS 'PENDIENTE, PROCESADO, COMPLETADO, FALLIDO, REEMBOLSADO';

-- =============================================================================
-- TABLA: notificacion
-- =============================================================================
CREATE TABLE notificacion (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT fk_notificacion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_notificacion_tipo CHECK (tipo IN ('INFO', 'ADVERTENCIA', 'ERROR', 'EXITO', 'SISTEMA'))
);

COMMENT ON TABLE notificacion IS 'Notificaciones enviadas a los usuarios del sistema';
COMMENT ON COLUMN notificacion.tipo IS 'INFO, ADVERTENCIA, ERROR, EXITO, SISTEMA';

-- =============================================================================
-- TABLA: bitacora
-- =============================================================================
CREATE TABLE bitacora (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT,
    accion          VARCHAR(50)     NOT NULL,
    entidad         VARCHAR(100)    NOT NULL,
    entidad_id      BIGINT,
    datos_anteriores TEXT,
    datos_nuevos    TEXT,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_bitacora_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE bitacora IS 'Bitacora de auditoria para trazabilidad de acciones en el sistema';
COMMENT ON COLUMN bitacora.accion IS 'INSERT, UPDATE, DELETE, LOGIN, LOGOUT, etc.';
COMMENT ON COLUMN bitacora.datos_anteriores IS 'JSON con los valores antes del cambio';
COMMENT ON COLUMN bitacora.datos_nuevos IS 'JSON con los valores despues del cambio';

-- =============================================================================
-- TABLA: correo_enviado
-- =============================================================================
CREATE TABLE correo_enviado (
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
    version         INTEGER         NOT NULL DEFAULT 1,

    CONSTRAINT chk_correo_enviado_tipo CHECK (tipo IN ('TRANSACCIONAL', 'MARKETING', 'NOTIFICACION', 'SISTEMA')),
    CONSTRAINT chk_correo_enviado_estado CHECK (estado IN ('PENDIENTE', 'ENVIADO', 'FALLIDO'))
);

COMMENT ON TABLE correo_enviado IS 'Historial de correos electronicos enviados desde el sistema';
COMMENT ON COLUMN correo_enviado.tipo IS 'TRANSACCIONAL, MARKETING, NOTIFICACION, SISTEMA';
COMMENT ON COLUMN correo_enviado.estado IS 'PENDIENTE, ENVIADO, FALLIDO';

-- =============================================================================
-- TRIGGER: updated_at automatico
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_actualizar_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION fn_actualizar_updated_at() IS 'Funcion trigger que actualiza el campo updated_at automaticamente';

-- Aplicar trigger a todas las tablas con updated_at (excepto bitacora)
DO $$
DECLARE
    tabla TEXT;
BEGIN
    FOR tabla IN
        SELECT t.tablename
        FROM pg_tables t
        WHERE t.schemaname = 'public'
          AND t.tablename != 'bitacora'
    LOOP
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = tabla
              AND column_name = 'updated_at'
        ) THEN
            EXECUTE format(
                'CREATE TRIGGER trg_%s_updated_at
                 BEFORE UPDATE ON %I
                 FOR EACH ROW
                 EXECUTE FUNCTION fn_actualizar_updated_at()',
                tabla, tabla
            );
            RAISE NOTICE 'Trigger trg_%_updated_at creado', tabla;
        END IF;
    END LOOP;
END $$;

-- =============================================================================
-- Confirmacion final
-- =============================================================================
DO $$
BEGIN
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'DDL completo de PETCare creado exitosamente en esquema public';
    RAISE NOTICE '  - 29 tablas con constraints inline';
    RAISE NOTICE '  - 35+ FOREIGN KEYs';
    RAISE NOTICE '  - 14+ UNIQUE constraints';
    RAISE NOTICE '  - 20+ CHECK constraints';
    RAISE NOTICE '  - TRIGGERS updated_at automatico';
    RAISE NOTICE '============================================================';
END $$;
