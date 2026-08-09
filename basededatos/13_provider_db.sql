-- =============================================================================
-- PETCare Home Services - Script 13: Base de Datos del Microservicio provider-service
-- Base de Datos: petcaredb_provider
-- Esquema: public
-- Motor: PostgreSQL
--
-- Contiene únicamente el dominio del proveedor más las copias de tablas
-- compartidas que provider-service necesita para autenticación (JWT) y para
-- resolver nombres de catálogo (persona/rol/usuario/usuario_rol, estado,
-- ciudad, direccion, servicio global):
--   * proveedor, proveedor_especialidad, proveedor_servicio,
--     proveedor_servicio_modalidad, disponibilidad, promocion,
--     solicitud_reserva, evento_procesado
--   * copias de solo-lectura: persona, rol, usuario, usuario_rol, estado,
--     ciudad, direccion, servicio
--
-- NOTA: las tablas del dominio cliente (cliente, mascota, registro_vacunacion,
-- reserva, pago) viven en petcaredb. provider-service las consulta por HTTP
-- interno hacia reservation-service/payment-service, nunca en esta base.
-- =============================================================================

-- Finalizar conexiones existentes a la base de datos
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'petcaredb_provider'
  AND pid <> pg_backend_pid();

-- Eliminar base de datos si existe
DROP DATABASE IF EXISTS petcaredb_provider;

-- Crear base de datos con codificación UTF-8
CREATE DATABASE petcaredb_provider
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'es_BO.UTF-8'
    LC_CTYPE = 'es_BO.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE petcaredb_provider IS 'Base de datos del microservicio provider-service - Dominio de proveedores PETCare';

-- Conectar a la base de datos petcaredb_provider
\connect petcaredb_provider

-- Establecer search_path por defecto
ALTER DATABASE petcaredb_provider SET search_path TO public;

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- COPIAS DE TABLAS COMPARTIDAS (solo lectura desde provider-service)
-- =============================================================================

-- TABLA: persona
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
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE persona IS 'Copiado desde petcaredb - Datos personales de los actores del sistema';
COMMENT ON COLUMN persona.ci IS 'Carnet de identidad - único por persona';

-- TABLA: rol
CREATE TABLE rol (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(50)     NOT NULL,
    descripcion     TEXT,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE rol IS 'Copiado desde petcaredb - Roles asignables a los usuarios';

-- TABLA: usuario
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
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE usuario IS 'Copiado desde petcaredb - Cuentas de acceso al sistema (autenticación JWT del provider-service)';

-- TABLA: usuario_rol
CREATE TABLE usuario_rol (
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      BIGINT          NOT NULL,
    rol_id          BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE usuario_rol IS 'Copiado desde petcaredb - Relación N:M entre usuarios y roles';

-- TABLA: ciudad
CREATE TABLE ciudad (
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

COMMENT ON TABLE ciudad IS 'Copiado desde petcaredb - Catálogo de ciudades';

-- TABLA: estado
CREATE TABLE estado (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    codigo          VARCHAR(10)     NOT NULL,
    ciudad_id       BIGINT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE estado IS 'Copiado desde petcaredb - Estados o departamentos asociados a ciudades';

-- TABLA: direccion
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
    version         INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE direccion IS 'Copiado desde petcaredb - Direcciones físicas de los proveedores';

-- TABLA: servicio (catálogo global, usado para nombres de especialidad)
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
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE servicio IS 'Copiado desde petcaredb - Catálogo global de servicios (nombres de especialidad)';

-- =============================================================================
-- DOMINIO PROVEEDOR
-- =============================================================================

-- TABLA: proveedor
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
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor IS 'Información de proveedores de servicios de cuidado de mascotas';
COMMENT ON COLUMN proveedor.radio_cobertura_km IS 'Radio de cobertura del proveedor en kilómetros';
COMMENT ON COLUMN proveedor.calificacion IS 'Calificación promedio de 0.00 a 5.00';

-- TABLA: proveedor_especialidad
CREATE TABLE proveedor_especialidad (
    id                      BIGSERIAL       PRIMARY KEY,
    proveedor_id            BIGINT          NOT NULL REFERENCES proveedor(id),
    servicio_id             BIGINT          NOT NULL,
    requiere_certificado    BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor_especialidad IS 'Relación N:M entre proveedores y servicios (especialidades) que ofrecen';
COMMENT ON COLUMN proveedor_especialidad.servicio_id IS 'Referencia a servicio.id del catálogo global copiado';

CREATE INDEX idx_proveedor_especialidad_proveedor ON proveedor_especialidad(proveedor_id) WHERE deleted = FALSE;
CREATE INDEX idx_proveedor_especialidad_servicio ON proveedor_especialidad(servicio_id) WHERE deleted = FALSE;

-- TABLA: proveedor_servicio
CREATE TABLE proveedor_servicio (
    id                      BIGSERIAL       PRIMARY KEY,
    proveedor_id            BIGINT          NOT NULL REFERENCES proveedor(id),
    nombre                  VARCHAR(100)    NOT NULL,
    descripcion             TEXT,
    categoria               VARCHAR(30)     NOT NULL,
    duracion_minutos        INTEGER         NOT NULL DEFAULT 60,
    precio_base             NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    requiere_certificado    BOOLEAN         NOT NULL DEFAULT FALSE,
    activo                  BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor_servicio IS 'Servicios propios que ofrece cada proveedor';
COMMENT ON COLUMN proveedor_servicio.categoria IS 'PELUQUERIA, PASEO, ALOJAMIENTO, VETERINARIA';

CREATE INDEX idx_proveedor_servicio_proveedor ON proveedor_servicio(proveedor_id) WHERE deleted = FALSE;
CREATE INDEX idx_proveedor_servicio_categoria ON proveedor_servicio(categoria) WHERE deleted = FALSE;

-- TABLA: proveedor_servicio_modalidad
CREATE TABLE proveedor_servicio_modalidad (
    id                      BIGSERIAL       PRIMARY KEY,
    proveedor_servicio_id   BIGINT          NOT NULL REFERENCES proveedor_servicio(id),
    modalidad               VARCHAR(30)     NOT NULL,
    costo_adicional         NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    activo                  BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN         NOT NULL DEFAULT FALSE,
    version                 INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE proveedor_servicio_modalidad IS 'Modalidades por servicio: EN_ESTABLECIMIENTO, RECOGIDA_ENTREGA, DOMICILIO';
COMMENT ON COLUMN proveedor_servicio_modalidad.costo_adicional IS 'Costo adicional de la modalidad (0 en establecimiento)';

CREATE INDEX idx_proveedor_servicio_modalidad_servicio ON proveedor_servicio_modalidad(proveedor_servicio_id) WHERE deleted = FALSE;

-- TABLA: disponibilidad
CREATE TABLE disponibilidad (
    id              BIGSERIAL       PRIMARY KEY,
    proveedor_id    BIGINT          NOT NULL REFERENCES proveedor(id),
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

COMMENT ON TABLE disponibilidad IS 'Horarios de disponibilidad de proveedores para cada servicio';
COMMENT ON COLUMN disponibilidad.dia_semana IS '0=Domingo, 1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado';
COMMENT ON COLUMN disponibilidad.servicio_id IS 'Referencia a proveedor_servicio.id del propio proveedor';

CREATE INDEX idx_disponibilidad_proveedor_servicio ON disponibilidad(proveedor_id, servicio_id) WHERE deleted = FALSE AND activo = TRUE;

-- TABLA: promocion
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
    proveedor_id        BIGINT          REFERENCES proveedor(id),
    servicio_id         BIGINT          REFERENCES proveedor_servicio(id),
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    version             INTEGER         NOT NULL DEFAULT 1
);

COMMENT ON TABLE promocion IS 'Promociones y descuentos publicados por proveedores';
COMMENT ON COLUMN promocion.tipo_descuento IS 'PERCENTAGE=Porcentaje, FIXED=Monto fijo';
COMMENT ON COLUMN promocion.servicio_id IS 'proveedor_servicio.id al que se aplica el descuento (NULL = aplica a todos los servicios del proveedor)';

CREATE INDEX idx_promocion_proveedor ON promocion(proveedor_id) WHERE deleted = FALSE;
CREATE INDEX idx_promocion_proveedor_servicio ON promocion(proveedor_id, servicio_id) WHERE deleted = FALSE;

-- TABLA: solicitud_reserva
-- Proyección del provider-service con datos denormalizados del cliente/reserva
-- (latitud, longitud y dirección de referencia) obtenidos por HTTP interno
-- hacia reservation-service.
CREATE TABLE solicitud_reserva (
    id                BIGSERIAL       PRIMARY KEY,
    reserva_id        BIGINT          NOT NULL,
    codigo            VARCHAR(20)     NOT NULL,
    cliente_id        BIGINT          NOT NULL,
    cliente_nombre    VARCHAR(255),
    proveedor_id      BIGINT          NOT NULL,
    proveedor_empresa VARCHAR(150),
    servicio_id       BIGINT          NOT NULL,
    servicio_nombre   VARCHAR(100),
    mascota_id        BIGINT,
    mascota_nombre    VARCHAR(100),
    fecha_inicio      TIMESTAMP,
    hora_inicio       TIME,
    precio_total      NUMERIC(10, 2),
    modalidad_entrega VARCHAR(30),
    estado            VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo    TEXT,
    latitud           NUMERIC(10, 7),
    longitud          NUMERIC(10, 7),
    direccion_referencia TEXT,
    registro_vacunacion_id BIGINT,
    creada_en         TIMESTAMP       NOT NULL DEFAULT NOW(),
    respondida_en     TIMESTAMP,
    CONSTRAINT uk_solicitud_reserva_reserva_proveedor UNIQUE (reserva_id, proveedor_id)
);

COMMENT ON TABLE solicitud_reserva IS 'Reservas solicitadas a los proveedores (proyección denormalizada del dominio de reserva)';
COMMENT ON COLUMN solicitud_reserva.latitud IS 'Denormalizado desde reserva (petcaredb) vía API interna';
COMMENT ON COLUMN solicitud_reserva.registro_vacunacion_id IS 'Referencia a registro_vacunacion.id (vive en petcaredb)';

CREATE INDEX idx_solicitud_reserva_proveedor_estado ON solicitud_reserva (proveedor_id, estado);
CREATE INDEX idx_solicitud_reserva_estado ON solicitud_reserva (estado);

-- TABLA: evento_procesado (idempotencia de consumidores Kafka)
CREATE TABLE evento_procesado (
    event_id     VARCHAR(64)   PRIMARY KEY,
    event_type   VARCHAR(100)  NOT NULL,
    service      VARCHAR(50)   NOT NULL,
    aggregate_id BIGINT,
    payload      TEXT,
    procesado_en TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- SEED
-- =============================================================================

-- ROLES
INSERT INTO rol (nombre, descripcion, activo) VALUES
('ADMIN',        'Administrador del sistema con acceso total',            TRUE),
('CLIENTE',      'Cliente que solicita servicios de cuidado de mascotas', TRUE),
('PROVEEDOR',    'Proveedor que ofrece servicios de cuidado de mascotas', TRUE),
('RECEPCION',    'Personal de recepción que gestiona reservas',           TRUE),
('VETERINARIO',  'Profesional veterinario que atiende mascotas',          TRUE),
('PELUQUERO',    'Especialista en peluquería y estética canina/felina',    TRUE),
('PASEADOR',     'Paseador profesional de mascotas',                      TRUE);

-- CIUDADES
INSERT INTO ciudad (nombre, codigo, latitud, longitud) VALUES
('La Paz',       'LP', -16.5000000, -68.1500000),
('Santa Cruz',   'SC', -17.7833333, -63.1821289),
('Cochabamba',   'CB', -17.4139700, -66.1470100),
('Sucre',        'SU', -19.0195860, -65.2619000),
('Oruro',        'OR', -17.9647000, -67.1108000),
('Potosi',       'PT', -19.6247780, -65.7534800),
('Tarija',       'TJ', -21.5326800, -64.7326100),
('Trinidad',     'TR', -14.8333300, -64.9000000),
('Cobija',       'CJ', -11.0333300, -68.7333300),
('Riberalta',    'RB', -10.9833300, -66.0833300);

-- ESTADOS/DEPARTAMENTOS
INSERT INTO estado (nombre, codigo, ciudad_id) VALUES
('La Paz',           'LP', 1),
('Santa Cruz',       'SC', 2),
('Cochabamba',       'CB', 3),
('Chuquisaca',       'CH', 4),
('Oruro',            'OR', 5),
('Potosí',           'PT', 6),
('Tarija',           'TJ', 7),
('Beni',             'BE', 8),
('Pando',            'PD', 9),
('El Beni',          'EB', 10);

-- CATÁLOGO GLOBAL DE SERVICIOS (para nombres de especialidad)
INSERT INTO servicio (nombre, descripcion, duracion_minutos, precio_base, imagen_url, activo, categoria) VALUES
('Peluquería Básica',       'Corte, baño y secado básico para mascotas',                   60,  80.00,  '/img/servicios/peluqueria_basica.jpg',     TRUE, 'PELUQUERIA'),
('Peluquería Premium',      'Corte, baño, secado, corte de uñas y limpieza de oídos',       90,  120.00, '/img/servicios/peluqueria_premium.jpg',    TRUE, 'PELUQUERIA'),
('Baño Terapéutico',        'Baño con productos medicinales para problemas de piel',        45,  100.00, '/img/servicios/bano_terapeutico.jpg',      TRUE, 'PELUQUERIA'),
('Paseo Básico',            'Paseo de 30 minutos por zonas seguras',                        30,  30.00,  '/img/servicios/paseo_basico.jpg',          TRUE, 'PASEO'),
('Paseo Extendido',         'Paseo de 60 minutos con socialización',                        60,  50.00,  '/img/servicios/paseo_extendido.jpg',       TRUE, 'PASEO'),
('Paseo Grupal',            'Paseo grupal para socialización de mascotas',                  90,  40.00,  '/img/servicios/paseo_grupal.jpg',          TRUE, 'PASEO'),
('Consulta Veterinaria',    'Consulta veterinaria general en domicilio',                    45,  150.00, '/img/servicios/consulta_veterinaria.jpg',  TRUE, 'VETERINARIA'),
('Vacunación',              'Aplicación de vacunas según calendario',                       30,  80.00,  '/img/servicios/vacunacion.jpg',            TRUE, 'VETERINARIA'),
('Desparasitación',         'Tratamiento antiparasitario interno y externo',                30,  70.00,  '/img/servicios/desparasitacion.jpg',       TRUE, 'VETERINARIA'),
('Emergencia Veterinaria',  'Atención de emergencia veterinaria a domicilio',               60,  250.00, '/img/servicios/emergencia.jpg',            TRUE, 'VETERINARIA'),
('Guardería Día Completo',  'Cuidado de mascota durante todo el día',                      480, 120.00, '/img/servicios/guarderia_dia.jpg',         TRUE, 'GUARDERIA'),
('Guardería Media Jornada', 'Cuidado de mascota por media jornada',                        240, 70.00,  '/img/servicios/guarderia_media.jpg',       TRUE, 'GUARDERIA'),
('Adiestramiento Básico',   'Enseñanza de comandos básicos: sentado, quieto, ven',          60,  200.00, '/img/servicios/adiestramiento_basico.jpg',  TRUE, 'ADIESTRAMIENTO'),
('Adiestramiento Avanzado', 'Entrenamiento avanzado y corrección de conducta',              90,  300.00, '/img/servicios/adiestramiento_avanzado.jpg',TRUE, 'ADIESTRAMIENTO'),
('Estética Canina',         'Corte de uñas, limpieza de oídos y corte de pelo',            45,  90.00,  '/img/servicios/estetica_canina.jpg',       TRUE, 'PELUQUERIA'),
('Hotel para Mascotas',     'Hospedaje de mascota con cuidados completos',                  1440,200.00, '/img/servicios/hotel_mascotas.jpg',        TRUE, 'GUARDERIA'),
('Transporte de Mascotas',  'Servicio de transporte seguro para mascotas',                  60,  60.00,  '/img/servicios/transporte.jpg',            TRUE, 'TRANSPORTE'),
('Entrenamiento Comportamiento', 'Modificación de conducta y hábitos',                      90,  250.00, '/img/servicios/comportamiento.jpg',        TRUE, 'ADIESTRAMIENTO');

-- USUARIOS DE EJEMPLO (misma contraseña "admin123" que en petcaredb)
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- ADMINISTRADOR
INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('Carlos', 'Administrador', 'Sistema', '1234567', '70123456', 'admin@com', '1985-01-15', 'M');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, TRUE, 0, FALSE);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1);

-- CLIENTE DE EJEMPLO
INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('María', 'González', 'López', '7654321', '71234567', 'maria.gonzalez@email.com', '1990-05-20', 'F');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('mgonzalez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, TRUE, 0, FALSE);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(2, 2);

-- PROVEEDOR DE EJEMPLO
INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('Roberto', 'Mamani', 'Quispe', '8765432', '72345678', 'roberto.mamani@email.com', '1988-03-10', 'M');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('rmamani', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, TRUE, 0, FALSE);

INSERT INTO proveedor (persona_id, usuario_id, latitud, longitud, radio_cobertura_km, descripcion, verificado, calificacion) VALUES
(3, 3, -16.5000000, -68.1500000, 15.00, 'Peluquero profesional con 10 años de experiencia', TRUE, 4.80);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(3, 3);

-- ESPECIALIDADES DEL PROVEEDOR (referencian el catálogo global servicio)
INSERT INTO proveedor_especialidad (proveedor_id, servicio_id, requiere_certificado) VALUES
(1, 1,  FALSE),  -- Peluquería Básica
(1, 2,  FALSE),  -- Peluquería Premium
(1, 3,  FALSE),  -- Baño Terapéutico
(1, 15, FALSE);  -- Estética Canina

-- DISPONIBILIDAD DEL PROVEEDOR
INSERT INTO disponibilidad (proveedor_id, servicio_id, dia_semana, hora_inicio, hora_fin, activo) VALUES
(1, 1, 1, '08:00:00', '17:00:00', TRUE),  -- Lunes Peluquería Básica
(1, 1, 2, '08:00:00', '17:00:00', TRUE),  -- Martes Peluquería Básica
(1, 1, 3, '08:00:00', '17:00:00', TRUE),  -- Miércoles Peluquería Básica
(1, 1, 4, '08:00:00', '17:00:00', TRUE),  -- Jueves Peluquería Básica
(1, 1, 5, '08:00:00', '17:00:00', TRUE),  -- Viernes Peluquería Básica
(1, 1, 6, '09:00:00', '14:00:00', TRUE),  -- Sábado Peluquería Básica
(1, 2, 1, '08:00:00', '17:00:00', TRUE),  -- Lunes Peluquería Premium
(1, 2, 2, '08:00:00', '17:00:00', TRUE),  -- Martes Peluquería Premium
(1, 2, 3, '08:00:00', '17:00:00', TRUE),  -- Miércoles Peluquería Premium
(1, 2, 4, '08:00:00', '17:00:00', TRUE),  -- Jueves Peluquería Premium
(1, 2, 5, '08:00:00', '17:00:00', TRUE),  -- Viernes Peluquería Premium
(1, 2, 6, '09:00:00', '14:00:00', TRUE);  -- Sábado Peluquería Premium

-- SERVICIOS PROPIOS DEL PROVEEDOR 1 (proveedor_servicio)
INSERT INTO proveedor_servicio (id, proveedor_id, nombre, descripcion, categoria, duracion_minutos, precio_base) VALUES
(1, 1, 'Peluquería Básica',     'Corte, baño y secado básico para mascotas',          'PELUQUERIA',   60,  80.00),
(2, 1, 'Peluquería Premium',    'Corte, baño, secado, corte de uñas y limpieza de oídos','PELUQUERIA', 90, 120.00),
(3, 1, 'Baño Terapéutico',      'Baño con productos medicinales para problemas de piel', 'PELUQUERIA', 45, 100.00),
(4, 1, 'Paseo Extendido',       'Paseo de 60 minutos con socialización',              'PASEO',        60,  50.00),
(5, 1, 'Vacunación',            'Aplicación de vacunas según calendario',             'VETERINARIA',  30,  80.00);

INSERT INTO proveedor_servicio_modalidad (proveedor_servicio_id, modalidad, costo_adicional) VALUES
(1, 'EN_ESTABLECIMIENTO', 0.00),
(1, 'DOMICILIO',         20.00),
(2, 'EN_ESTABLECIMIENTO', 0.00),
(2, 'DOMICILIO',         20.00),
(3, 'EN_ESTABLECIMIENTO', 0.00),
(3, 'DOMICILIO',         20.00),
(4, 'EN_ESTABLECIMIENTO', 0.00),
(4, 'RECOGIDA_ENTREGA',  10.00),
(5, 'EN_ESTABLECIMIENTO', 0.00),
(5, 'DOMICILIO',         20.00);

-- PROMOCIONES DEL PROVEEDOR 1 (fechas relativas a NOW())
INSERT INTO promocion (proveedor_id, servicio_id, codigo, nombre, descripcion, tipo_descuento, valor_descuento,
                       fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales) VALUES
(1, 1, 'PELU-BASICO-10', 'Peluquería Básica 10%',     '10% de descuento en peluquería básica',                    'PERCENTAGE', 10.00, NOW() - INTERVAL '5 days',  NOW() + INTERVAL '90 days', TRUE, 100, 0),
(1, 1, 'PELU-BASICO-15', 'Peluquería Básica 15 Bs',   '15 Bs de descuento en peluquería básica',                  'FIXED',      15.00, NOW() - INTERVAL '5 days',  NOW() + INTERVAL '60 days', TRUE,  50, 0),
(1, 2, 'PELU-PREMIUM-15', 'Peluquería Premium 15%',   '15% de descuento en peluquería premium',                   'PERCENTAGE', 15.00, NOW() - INTERVAL '3 days',  NOW() + INTERVAL '120 days', TRUE, 100, 0),
(1, 2, 'PELU-PREMIUM-20', 'Peluquería Premium 20 Bs', '20 Bs de descuento en peluquería premium',                 'FIXED',      20.00, NOW() - INTERVAL '3 days',  NOW() + INTERVAL '90 days', TRUE,  60, 0),
(1, 3, 'BANO-20',          'Baño Terapéutico 20%',    '20% de descuento en baño terapéutico',                     'PERCENTAGE', 20.00, NOW() - INTERVAL '10 days', NOW() + INTERVAL '150 days', TRUE, 80, 0),
(1, 3, 'BANO-15',          'Baño Terapéutico 15 Bs',  '15 Bs de descuento en baño terapéutico',                   'FIXED',      15.00, NOW() - INTERVAL '10 days', NOW() + INTERVAL '45 days',  TRUE, 40, 0),
(1, 4, 'PASEO-25',          'Paseo Extendido 25%',     '25% de descuento en paseo extendido',                      'PERCENTAGE', 25.00, NOW() - INTERVAL '2 days',  NOW() + INTERVAL '60 days',  TRUE, 90, 0),
(1, 4, 'PASEO-10',          'Paseo Extendido 10 Bs',   '10 Bs de descuento en paseo extendido',                    'FIXED',      10.00, NOW() - INTERVAL '2 days',  NOW() + INTERVAL '30 days',  TRUE, 70, 0),
(1, 5, 'VACUNA-30',         'Vacunación 30%',          '30% de descuento en vacunación',                           'PERCENTAGE', 30.00, NOW() - INTERVAL '7 days',  NOW() + INTERVAL '90 days',  TRUE, 120, 0),
(1, 5, 'VACUNA-20',         'Vacunación 20 Bs',        '20 Bs de descuento en vacunación',                         'FIXED',      20.00, NOW() - INTERVAL '7 days',  NOW() + INTERVAL '60 days',  TRUE, 60, 0);

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
DECLARE
    total_proveedores INTEGER;
    total_servicios   INTEGER;
    total_promociones INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_proveedores FROM proveedor WHERE deleted = FALSE;
    SELECT COUNT(*) INTO total_servicios  FROM proveedor_servicio WHERE deleted = FALSE;
    SELECT COUNT(*) INTO total_promociones FROM promocion WHERE deleted = FALSE;
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Base de datos petcaredb_provider creada exitosamente';
    RAISE NOTICE 'Esquema: public';
    RAISE NOTICE '  - % proveedores', total_proveedores;
    RAISE NOTICE '  - % servicios propios del proveedor', total_servicios;
    RAISE NOTICE '  - % promociones', total_promociones;
    RAISE NOTICE '============================================================';
END $$;
