-- =============================================================================
-- PETCare Home Services - Script 05: Datos Iniciales
-- Base de Datos: petcaredb
-- Esquema: petcare
-- Contenido: Roles, Estados, Menús, Permisos, Especies, Razas,
--            Vacunas, Servicios, Ciudades, Usuario Admin
-- =============================================================================

\connect petcaredb
SET search_path TO petcare, public;

-- =============================================================================
-- Meta Indexing: Datos Iniciales - Catálogos Base
-- =============================================================================

-- ---------------------------------------------------------------------------
-- ROLES
-- ---------------------------------------------------------------------------
INSERT INTO rol (nombre, descripcion, activo) VALUES
('ADMIN',        'Administrador del sistema con acceso total',           TRUE),
('CLIENTE',      'Cliente que solicita servicios de cuidado de mascotas', TRUE),
('PROVEEDOR',    'Proveedor que ofrece servicios de cuidado de mascotas', TRUE),
('RECEPCION',    'Personal de recepción que gestiona reservas',         TRUE),
('VETERINARIO',  'Profesional veterinario que atiende mascotas',        TRUE),
('PELUQUERO',    'Especialista en peluquería y estética canina/felina',  TRUE),
('PASEADOR',     'Paseador profesional de mascotas',                     TRUE);

-- ---------------------------------------------------------------------------
-- ESTADOS DE RESERVA
-- ---------------------------------------------------------------------------
INSERT INTO estado_reserva (nombre, descripcion, color, icono, orden) VALUES
('PENDIENTE',   'Reserva creada, esperando confirmación del proveedor', '#FFA500', 'clock',         1),
('CONFIRMADA',  'Reserva confirmada por el proveedor',                  '#4CAF50', 'check-circle',  2),
('EN_CURSO',    'Servicio en progreso',                                  '#2196F3', 'play-circle',   3),
('COMPLETADA',  'Servicio finalizado exitosamente',                     '#8BC34A', 'check-double',  4),
('CANCELADA',   'Reserva cancelada por el cliente o proveedor',         '#F44336', 'times-circle',  5),
('RECHAZADA',   'Reserva rechazada por el proveedor',                   '#9E9E9E', 'ban',           6);

-- ---------------------------------------------------------------------------
-- CIUDADES
-- ---------------------------------------------------------------------------
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

-- ---------------------------------------------------------------------------
-- ESTADOS/DEPARTAMENTOS
-- ---------------------------------------------------------------------------
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

-- ---------------------------------------------------------------------------
-- ESPECIES
-- ---------------------------------------------------------------------------
INSERT INTO especie (nombre, descripcion) VALUES
('Perro',    'Canino domesticado, el más popular como mascota'),
('Gato',     'Felino doméstico, independiente y cariñoso'),
('Ave',      'Aves domésticas como loros, canarios, periquitos'),
('Reptil',   'Reptiles domésticos como tortugas, iguanas, lagartos'),
('Conejo',   'Lagomorfo doméstico, ideal para espacios interiores');

-- ---------------------------------------------------------------------------
-- RAZAS POR ESPECIE
-- ---------------------------------------------------------------------------

-- Razas de Perro
INSERT INTO raza (nombre, especie_id, descripcion) VALUES
('Labrador Retriever',    1, 'Perro amigable, activo y devoto, ideal para familias'),
('Golden Retriever',      1, 'Perro inteligente y amigable, excelente compañía'),
('Pastor Alemán',         1, 'Perro leal y valiente, usado en trabajo y vigilancia'),
('Bulldog Francés',       1, 'Perro pequeño, juguetón y de temperamento amigable'),
('Poodle',                1, 'Perro elegante e inteligente, hypoallergenic'),
('Chihuahua',             1, 'Perro pequeño de gran personalidad y lealtad'),
('Rottweiler',            1, 'Perro fuerte y protector, excelente guardián'),
('Beagle',                1, 'Perro de caza amigable y curioso'),
('Yorkshire Terrier',     1, 'Perro pequeño elegante y valiente'),
('Dachshund',             1, 'Perro largo y valiente, excelente cazador'),
('Husky Siberiano',       1, 'Perro atlético y amigable, resistente al frío'),
('Boxer',                 1, 'Perro enérgico y leal, excelente compañero familiar'),
('Mestizo',               1, 'Perro de raza mixta, único y especial');

-- Razas de Gato
INSERT INTO raza (nombre, especie_id, descripcion) VALUES
('Siamés',                2, 'Gato elegante y vocal, extremidades oscuras'),
('Persa',                 2, 'Gato de pelaje largo y temperamento tranquilo'),
('Maine Coon',            2, 'Gato grande y sociable, pelaje abundante'),
('Ragdoll',               2, 'Gato grande y dócil, ojos azules'),
('Bengalí',               2, 'Gato activo con patrón de leopardo'),
('Abisinio',              2, 'Gato ágil y curioso, pelaje corto'),
('Británico de Pelo Corto', 2, 'Gato robusto y tranquilo, pelaje denso'),
('Sphynx',                2, 'Gato sin pelo, cariñoso y energético'),
('Mestizo',               2, 'Gato de raza mixta, único y especial');

-- Razas de Ave
INSERT INTO raza (nombre, especie_id, descripcion) VALUES
('Loro Gris Africano',    3, 'Ave muy inteligente, excelente para hablar'),
('Canario',               3, 'Ave pequeña y cantora, colorida'),
('Periquito',             3, 'Ave social y juguetón, fácil de cuidar'),
('Cockatiel',             3, 'Ave cariñosa y musical, cresta característica'),
('Ninfo',                 3, 'Ave tropical colorida y sociable');

-- Razas de Reptil
INSERT INTO raza (nombre, especie_id, descripcion) VALUES
('Tortuga Marina',        4, 'Reptil acuático, requiere cuidados especiales'),
('Iguana Verde',          4, 'Reptil arbóreo, herbívoro y territorial'),
('Gecko Leopardo',        4, 'Lagarto pequeño y nocturno, fácil de manejar'),
('Dragón Barbilludo',     4, 'Lagarto amigable y manso, herbívoro'),
('Pitón Real',            4, 'Serpiente no venenosa, dócil y grande');

-- Razas de Conejo
INSERT INTO raza (nombre, especie_id, descripcion) VALUES
('Conejo Holandés',       5, 'Conejo pequeño y amigable, ideal para interiores'),
('Conejo Angora',         5, 'Conejo de pelaje largo, suave y elegante'),
('Conejo Rex',            5, 'Conejo de pelaje corto y denso, dócil'),
('Conejo Gigante Belga',  5, 'Conejo grande y tranquilo, peso hasta 8 kg'),
('Conejo Mini Lop',       5, 'Conejo pequeño con orejas caídas, cariñoso');

-- ---------------------------------------------------------------------------
-- VACUNAS
-- ---------------------------------------------------------------------------
INSERT INTO vacuna (nombre, descripcion, periodicidad_meses) VALUES
('Rabia',                'Vacuna contra la rabia, obligatoria por ley',         12),
('Moquillo Canino',      'Vacuna contra el moquillo, enfermedad viral grave',   12),
('Parvovirus Canino',    'Vacuna contra el parvovirus, altamente contagioso',   12),
('Leucemia Felina',      'Vacuna contra el virus de leucemia felina (FeLV)',    12),
('Triple Felina',         'Vacuna combinada: panleucopenia, calicivirus, herpes', 12),
('Bordetella Bronquística', 'Vacuna contra la tos de las perreras',             12),
('Leptospirosis',        'Vacuna contra leptospirosis, enfermedad bacteriana',  12),
('Peritonitis Infecciosa Felina (PIF)', 'Vacuna contra PIF en gatos',           12),
('Hepatitis Infecciosa Canina', 'Vacuna contra hepatitis canina infecciosa',    12),
('Parainfluenza',        'Vacuna contra parainfluenza canina',                  12),
('Giardia',              'Vacuna contra giardia en perros y gatos',             12),
('Fiebre Mareka',        'Vacuna contra la fiebre maculosa por garrapatas',     12);

-- ---------------------------------------------------------------------------
-- MENÚS PRINCIPALES
-- ---------------------------------------------------------------------------
INSERT INTO menu (nombre, descripcion, icono, url, orden, activo) VALUES
('Dashboard',        'Panel principal del sistema',          'dashboard',        '/dashboard',       1,  TRUE),
('Administracion',   'Gestión administrativa del sistema',   'settings',         '/admin',           2,  TRUE),
('Mascotas',         'Gestión de mascotas registradas',      'pets',             '/mascotas',        3,  TRUE),
('Clientes',         'Gestión de clientes',                  'people',           '/clientes',        4,  TRUE),
('Proveedores',      'Gestión de proveedores de servicios',  'business',         '/proveedores',     5,  TRUE),
('Reservas',         'Gestión de reservas de servicios',     'event',            '/reservas',        6,  TRUE),
('Servicios',        'Catálogo de servicios disponibles',    'build',            '/servicios',       7,  TRUE),
('Promociones',      'Gestión de promociones y descuentos',  'local_offer',      '/promociones',     8,  TRUE),
('Vacunas',          'Control de vacunación de mascotas',    'vaccines',         '/vacunas',         9,  TRUE),
('Pagos',            'Gestión de pagos y facturación',       'payment',          '/pagos',           10, TRUE),
('Sucursales',       'Gestión de sucursales físicas',        'store',            '/sucursales',      11, TRUE),
('Bitacora',         'Auditoría y registro de acciones',     'history',          '/bitacora',        12, TRUE),
('Reportes',         'Generación de reportes del sistema',   'assessment',       '/reportes',        13, TRUE),
('Configuraciones',  'Configuraciones generales del sistema','tune',             '/configuraciones', 14, TRUE);

-- ---------------------------------------------------------------------------
-- SUBMENÚS
-- ---------------------------------------------------------------------------

-- Submenús de Administración
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Personas',    'Gestión de personas registradas',    'person',       '/admin/personas',      1, 2, TRUE),
('Usuarios',    'Gestión de usuarios del sistema',    'people',       '/admin/usuarios',      2, 2, TRUE),
('Roles',       'Gestión de roles del sistema',       'admin_panel',  '/admin/roles',         3, 2, TRUE),
('Permisos',    'Gestión de permisos por rol',        'lock',         '/admin/permisos',      4, 2, TRUE),
('Menus',       'Gestión de menús del sistema',       'menu',         '/admin/menus',         5, 2, TRUE);

-- Submenús de Mascotas
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Mascotas',    'Listado de todas las mascotas',     'list',         '/mascotas/lista',      1, 3, TRUE),
('Nueva Mascota',        'Registrar nueva mascota',           'add',          '/mascotas/nueva',      2, 3, TRUE),
('Especies y Razas',     'Catálogo de especies y razas',      'category',     '/mascotas/especies',   3, 3, TRUE),
('Historial Vacunación', 'Historial de vacunación',          'vaccines',     '/mascotas/vacunas',    4, 3, TRUE);

-- Submenús de Clientes
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Clientes',    'Listado de clientes registrados',   'people',       '/clientes/lista',      1, 4, TRUE),
('Nuevo Cliente',        'Registrar nuevo cliente',           'person_add',   '/clientes/nuevo',      2, 4, TRUE),
('Detalle Cliente',      'Información detallada del cliente', 'person',       '/clientes/detalle',    3, 4, TRUE);

-- Submenús de Proveedores
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Proveedores',    'Listado de proveedores',              'business',     '/proveedores/lista',       1, 5, TRUE),
('Nuevo Proveedor',         'Registrar nuevo proveedor',           'person_add',   '/proveedores/nuevo',       2, 5, TRUE),
('Especialidades',           'Gestión de especialidades',           'star',         '/proveedores/especialidades', 3, 5, TRUE),
('Disponibilidad',           'Horarios de disponibilidad',          'schedule',     '/proveedores/disponibilidad', 4, 5, TRUE),
('Verificar Proveedor',     'Proceso de verificación',            'verified',     '/proveedores/verificar',   5, 5, TRUE);

-- Submenús de Reservas
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Reservas',   'Listado de todas las reservas',      'event',        '/reservas/lista',       1, 6, TRUE),
('Nueva Reserva',       'Crear nueva reserva',                'add_task',     '/reservas/nueva',       2, 6, TRUE),
('Calendario',          'Vista de calendario de reservas',    'calendar',     '/reservas/calendario',  3, 6, TRUE),
('Gestión Estados',     'Gestión de estados de reserva',      'swap_horiz',   '/reservas/estados',     4, 6, TRUE);

-- Submenús de Servicios
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Servicios',  'Catálogo de servicios',              'build',        '/servicios/lista',      1, 7, TRUE),
('Nuevo Servicio',      'Agregar nuevo servicio',             'add_circle',   '/servicios/nuevo',      2, 7, TRUE),
('Categorías',          'Gestión de categorías',              'category',     '/servicios/categorias', 3, 7, TRUE);

-- Submenús de Promociones
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Promociones',  'Listado de promociones',            'local_offer',  '/promociones/lista',    1, 8, TRUE),
('Nueva Promoción',       'Crear nueva promoción',            'add',          '/promociones/nueva',    2, 8, TRUE),
('Historial Uso',         'Historial de uso de promociones',  'history',      '/promociones/historial', 3, 8, TRUE);

-- Submenús de Vacunas
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Catálogo Vacunas',     'Listado de vacunas disponibles',    'vaccines',     '/vacunas/catalogo',     1, 9, TRUE),
('Registro Vacunación',  'Registrar vacunación',              'add_task',     '/vacunas/registrar',    2, 9, TRUE),
('Próximas Vacunas',     'Vacunas próximas a vencer',         'notification', '/vacunas/proximas',     3, 9, TRUE);

-- Submenús de Pagos
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Pagos',      'Listado de transacciones',           'receipt',      '/pagos/lista',          1, 10, TRUE),
('Nuevo Pago',          'Registrar nuevo pago',               'add_card',     '/pagos/nuevo',          2, 10, TRUE),
('Reporte Financiero',  'Reportes financieros',               'assessment',   '/pagos/reportes',       3, 10, TRUE);

-- Submenús de Sucursales
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Lista de Sucursales',  'Listado de sucursales',             'store',        '/sucursales/lista',     1, 11, TRUE),
('Nueva Sucursal',       'Registrar nueva sucursal',          'add_business', '/sucursales/nueva',     2, 11, TRUE);

-- Submenús de Bitácora
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Bitácora General',     'Registro completo de acciones',    'history',      '/bitacora/general',     1, 12, TRUE),
('Auditoría Usuarios',   'Auditoría por usuario',            'people',       '/bitacora/usuarios',    2, 12, TRUE),
('Auditoría Entidades',  'Auditoría por entidad',            'search',       '/bitacora/entidades',   3, 12, TRUE);

-- Submenús de Reportes
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('Reporte de Reservas',     'Estadísticas de reservas',         'event',        '/reportes/reservas',    1, 13, TRUE),
('Reporte de Clientes',     'Estadísticas de clientes',         'people',       '/reportes/clientes',    2, 13, TRUE),
('Reporte de Proveedores',  'Estadísticas de proveedores',      'business',     '/reportes/proveedores', 3, 13, TRUE),
('Reporte Financiero',      'Reportes de ingresos y pagos',     'assessment',   '/reportes/financiero',  4, 13, TRUE),
('Reporte de Servicios',    'Estadísticas de servicios',        'build',        '/reportes/servicios',   5, 13, TRUE);

-- Submenús de Configuraciones
INSERT INTO submenu (nombre, descripcion, icono, url, orden, menu_id, activo) VALUES
('General',              'Configuración general',              'settings',     '/config/general',       1, 14, TRUE),
('Notificaciones',       'Configuración de notificaciones',    'notifications','/config/notificaciones', 2, 14, TRUE),
('Correo Electrónico',   'Configuración de correo',            'email',        '/config/correo',        3, 14, TRUE),
('Seguridad',            'Configuración de seguridad',         'security',     '/config/seguridad',     4, 14, TRUE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL ADMIN (acceso total)
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Admin: Dashboard',              'Acceso al panel principal',            1, 1,  NULL, TRUE, TRUE, TRUE,  TRUE),
-- Administración
('Admin: Personas',               'CRUD completo de personas',            1, 2,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Usuarios',               'CRUD completo de usuarios',            1, 2,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Roles',                  'CRUD completo de roles',               1, 2,  3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Permisos',               'CRUD completo de permisos',            1, 2,  4,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Menus',                  'CRUD completo de menús',               1, 2,  5,    TRUE, TRUE, TRUE,  TRUE),
-- Mascotas
('Admin: Lista Mascotas',         'Ver mascotas',                         1, 3,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nueva Mascota',          'Crear mascota',                        1, 3,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Especies Razas',         'Gestionar especies y razas',           1, 3,  3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Historial Vacunación',   'Ver historial de vacunación',          1, 3,  4,    TRUE, TRUE, TRUE,  TRUE),
-- Clientes
('Admin: Lista Clientes',         'Ver clientes',                         1, 4,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nuevo Cliente',          'Crear cliente',                        1, 4,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Detalle Cliente',        'Ver detalle de cliente',               1, 4,  3,    TRUE, TRUE, TRUE,  TRUE),
-- Proveedores
('Admin: Lista Proveedores',      'Ver proveedores',                      1, 5,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nuevo Proveedor',        'Crear proveedor',                      1, 5,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Especialidades',         'Gestionar especialidades',             1, 5,  3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Disponibilidad',         'Gestionar disponibilidad',             1, 5,  4,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Verificar Proveedor',    'Verificar proveedores',               1, 5,  5,    TRUE, TRUE, TRUE,  TRUE),
-- Reservas
('Admin: Lista Reservas',         'Ver reservas',                         1, 6,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nueva Reserva',          'Crear reserva',                        1, 6,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Calendario',             'Ver calendario',                       1, 6,  3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Gestión Estados',        'Gestionar estados de reserva',         1, 6,  4,    TRUE, TRUE, TRUE,  TRUE),
-- Servicios
('Admin: Lista Servicios',        'Ver servicios',                        1, 7,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nuevo Servicio',         'Crear servicio',                       1, 7,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Categorías',             'Gestionar categorías',                 1, 7,  3,    TRUE, TRUE, TRUE,  TRUE),
-- Promociones
('Admin: Lista Promociones',      'Ver promociones',                      1, 8,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nueva Promoción',        'Crear promoción',                      1, 8,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Historial Uso',          'Ver historial de uso',                 1, 8,  3,    TRUE, TRUE, TRUE,  TRUE),
-- Vacunas
('Admin: Catálogo Vacunas',       'Ver catálogo de vacunas',              1, 9,  1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Registro Vacunación',    'Registrar vacunación',                 1, 9,  2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Próximas Vacunas',       'Ver próximas vacunas',                 1, 9,  3,    TRUE, TRUE, TRUE,  TRUE),
-- Pagos
('Admin: Lista Pagos',            'Ver pagos',                            1, 10, 1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nuevo Pago',             'Registrar pago',                       1, 10, 2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Reporte Financiero',     'Ver reportes financieros',             1, 10, 3,    TRUE, TRUE, TRUE,  TRUE),
-- Sucursales
('Admin: Lista Sucursales',       'Ver sucursales',                       1, 11, 1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Nueva Sucursal',         'Crear sucursal',                       1, 11, 2,    TRUE, TRUE, TRUE,  TRUE),
-- Bitácora
('Admin: Bitácora General',       'Ver bitácora general',                 1, 12, 1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Auditoría Usuarios',     'Ver auditoría de usuarios',            1, 12, 2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Auditoría Entidades',    'Ver auditoría de entidades',           1, 12, 3,    TRUE, TRUE, TRUE,  TRUE),
-- Reportes
('Admin: Reporte Reservas',       'Ver reporte de reservas',              1, 13, 1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Reporte Clientes',       'Ver reporte de clientes',              1, 13, 2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Reporte Proveedores',    'Ver reporte de proveedores',           1, 13, 3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Reporte Financiero',     'Ver reporte financiero',               1, 13, 4,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Reporte Servicios',      'Ver reporte de servicios',             1, 13, 5,    TRUE, TRUE, TRUE,  TRUE),
-- Configuraciones
('Admin: General',                'Configuración general',                1, 14, 1,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Notificaciones',         'Configurar notificaciones',            1, 14, 2,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Correo Electrónico',     'Configurar correo',                    1, 14, 3,    TRUE, TRUE, TRUE,  TRUE),
('Admin: Seguridad',              'Configurar seguridad',                 1, 14, 4,    TRUE, TRUE, TRUE,  TRUE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL CLIENTE
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Cliente: Dashboard',            'Acceso al panel principal',            2, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Mascotas (suyas)
('Cliente: Lista Mascotas',       'Ver sus mascotas',                     2, 3,  1,    TRUE, TRUE, TRUE,  FALSE),
('Cliente: Nueva Mascota',        'Registrar mascota propia',             2, 3,  2,    TRUE, TRUE, FALSE, FALSE),
('Cliente: Historial Vacunación', 'Ver historial de vacunación',          2, 3,  4,    FALSE, TRUE, FALSE, FALSE),
-- Reservas (suyas)
('Cliente: Lista Reservas',       'Ver sus reservas',                     2, 6,  1,    FALSE, TRUE, FALSE, FALSE),
('Cliente: Nueva Reserva',        'Crear reserva',                        2, 6,  2,    TRUE, TRUE, FALSE, FALSE),
('Cliente: Calendario',           'Ver calendario propio',                2, 6,  3,    FALSE, TRUE, FALSE, FALSE),
-- Servicios
('Cliente: Lista Servicios',      'Ver catálogo de servicios',            2, 7,  1,    FALSE, TRUE, FALSE, FALSE),
-- Promociones
('Cliente: Lista Promociones',    'Ver promociones disponibles',          2, 8,  1,    FALSE, TRUE, FALSE, FALSE),
-- Pagos (suyos)
('Cliente: Lista Pagos',          'Ver sus pagos',                        2, 10, 1,    FALSE, TRUE, FALSE, FALSE),
('Cliente: Nuevo Pago',           'Registrar pago',                       2, 10, 2,    TRUE, TRUE, FALSE, FALSE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL PROVEEDOR
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Proveedor: Dashboard',          'Acceso al panel principal',            3, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Mascotas (de sus clientes)
('Proveedor: Lista Mascotas',     'Ver mascotas de sus clientes',         3, 3,  1,    FALSE, TRUE, FALSE, FALSE),
-- Reservas (asignadas)
('Proveedor: Lista Reservas',     'Ver sus reservas asignadas',           3, 6,  1,    FALSE, TRUE, TRUE,  FALSE),
('Proveedor: Calendario',         'Ver su calendario',                    3, 6,  3,    FALSE, TRUE, FALSE, FALSE),
('Proveedor: Gestión Estados',    'Actualizar estado de reservas',        3, 6,  4,    FALSE, TRUE, TRUE,  FALSE),
-- Servicios
('Proveedor: Lista Servicios',    'Ver catálogo de servicios',            3, 7,  1,    FALSE, TRUE, FALSE, FALSE),
-- Vacunas (de mascotas atendidas)
('Proveedor: Historial Vacunación','Ver historial de vacunación',         3, 3,  4,    TRUE, TRUE, TRUE,  FALSE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL RECEPCIÓN
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Recepción: Dashboard',          'Acceso al panel principal',            4, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Clientes
('Recepción: Lista Clientes',     'Ver clientes',                         4, 4,  1,    TRUE, TRUE, TRUE,  FALSE),
('Recepción: Nuevo Cliente',      'Registrar cliente',                    4, 4,  2,    TRUE, TRUE, FALSE, FALSE),
('Recepción: Detalle Cliente',    'Ver detalle de cliente',               4, 4,  3,    TRUE, TRUE, FALSE, FALSE),
-- Reservas
('Recepción: Lista Reservas',     'Ver todas las reservas',               4, 6,  1,    TRUE, TRUE, TRUE,  FALSE),
('Recepción: Nueva Reserva',      'Crear reserva por teléfono',           4, 6,  2,    TRUE, TRUE, FALSE, FALSE),
('Recepción: Calendario',         'Ver calendario general',               4, 6,  3,    TRUE, TRUE, FALSE, FALSE),
('Recepción: Gestión Estados',    'Gestionar estados de reserva',         4, 6,  4,    TRUE, TRUE, TRUE,  FALSE),
-- Servicios
('Recepción: Lista Servicios',    'Ver catálogo de servicios',            4, 7,  1,    FALSE, TRUE, FALSE, FALSE),
-- Mascotas
('Recepción: Lista Mascotas',     'Ver mascotas',                         4, 3,  1,    TRUE, TRUE, TRUE,  FALSE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL VETERINARIO
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Veterinario: Dashboard',        'Acceso al panel principal',            5, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Mascotas
('Veterinario: Lista Mascotas',   'Ver mascotas atendidas',               5, 3,  1,    FALSE, TRUE, FALSE, FALSE),
('Veterinario: Historial Vacunación','Ver y registrar vacunación',        5, 3,  4,    TRUE, TRUE, TRUE,  FALSE),
-- Vacunas
('Veterinario: Catálogo Vacunas', 'Ver catálogo de vacunas',              5, 9,  1,    FALSE, TRUE, FALSE, FALSE),
('Veterinario: Registro Vacunación','Registrar vacunación',               5, 9,  2,    TRUE, TRUE, FALSE, FALSE),
('Veterinario: Próximas Vacunas', 'Ver próximas vacunas',                 5, 9,  3,    FALSE, TRUE, FALSE, FALSE),
-- Reservas
('Veterinario: Lista Reservas',   'Ver reservas veterinarias',            5, 6,  1,    FALSE, TRUE, TRUE,  FALSE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL PELUQUERO
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Peluquero: Dashboard',          'Acceso al panel principal',            6, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Mascotas
('Peluquero: Lista Mascotas',     'Ver mascotas atendidas',               6, 3,  1,    FALSE, TRUE, FALSE, FALSE),
-- Reservas
('Peluquero: Lista Reservas',     'Ver reservas de peluquería',           6, 6,  1,    FALSE, TRUE, TRUE,  FALSE),
('Peluquero: Calendario',         'Ver calendario propio',                6, 6,  3,    FALSE, TRUE, FALSE, FALSE);

-- ---------------------------------------------------------------------------
-- PERMISOS PARA ROL PASEADOR
-- ---------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion, rol_id, menu_id, submenu_id, crear, leer, actualizar, eliminar) VALUES
-- Dashboard
('Paseador: Dashboard',           'Acceso al panel principal',            7, 1,  NULL, TRUE, TRUE, FALSE, FALSE),
-- Mascotas
('Paseador: Lista Mascotas',      'Ver mascotas paseadas',                7, 3,  1,    FALSE, TRUE, FALSE, FALSE),
-- Reservas
('Paseador: Lista Reservas',      'Ver reservas de paseo',                7, 6,  1,    FALSE, TRUE, TRUE,  FALSE),
('Paseador: Calendario',          'Ver calendario propio',                7, 6,  3,    FALSE, TRUE, FALSE, FALSE);

-- ---------------------------------------------------------------------------
-- SERVICIOS
-- ---------------------------------------------------------------------------
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

-- ---------------------------------------------------------------------------
-- USUARIO ADMINISTRADOR
-- ---------------------------------------------------------------------------
-- Contraseña hasheada con BCrypt: "admin123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('Carlos', 'Administrador', 'Sistema', '1234567', '70123456', 'admin@com', '1985-01-15', 'M');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, TRUE, 0, FALSE);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1);

-- ---------------------------------------------------------------------------
-- CLIENTE DE EJEMPLO
-- ---------------------------------------------------------------------------
INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('María', 'González', 'López', '7654321', '71234567', 'maria.gonzalez@email.com', '1990-05-20', 'F');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('mgonzalez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, TRUE, 0, FALSE);

INSERT INTO cliente (persona_id, usuario_id, notas) VALUES
(2, 2, 'Cliente frecuente, prefiere servicios por la mañana');

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(2, 2);

-- ---------------------------------------------------------------------------
-- PROVEEDOR DE EJEMPLO
-- ---------------------------------------------------------------------------
INSERT INTO persona (nombre, primer_apellido, segundo_apellido, ci, telefono, email, fecha_nacimiento, genero) VALUES
('Roberto', 'Mamani', 'Quispe', '8765432', '72345678', 'roberto.mamani@email.com', '1988-03-10', 'M');

INSERT INTO usuario (username, password, persona_id, activo, intentos_fallidos, bloqueado) VALUES
('rmamani', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, TRUE, 0, FALSE);

INSERT INTO proveedor (persona_id, usuario_id, latitud, longitud, radio_cobertura_km, descripcion, verificado, calificacion) VALUES
(3, 3, -16.5000000, -68.1500000, 15.00, 'Peluquero profesional con 10 años de experiencia', TRUE, 4.80);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(3, 3);

-- Asignar especialidades al proveedor
INSERT INTO proveedor_especialidad (proveedor_id, servicio_id) VALUES
(1, 1),  -- Peluquería Básica
(1, 2),  -- Peluquería Premium
(1, 3),  -- Baño Terapéutico
(1, 15); -- Estética Canina

-- ---------------------------------------------------------------------------
-- DISPONIBILIDAD DEL PROVEEDOR
-- ---------------------------------------------------------------------------
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

-- ---------------------------------------------------------------------------
-- MASCOTA DE EJEMPLO
-- ---------------------------------------------------------------------------
INSERT INTO mascota (nombre, fecha_nacimiento, genero, peso, color, especie_id, raza_id, cliente_id) VALUES
('Max', '2020-06-15', 'M', 25.50, 'Dorado', 1, 1, 1);

-- ---------------------------------------------------------------------------
-- PROMOCIONES DE EJEMPLO
-- ---------------------------------------------------------------------------
INSERT INTO promocion (codigo, nombre, descripcion, tipo_descuento, valor_descuento, fecha_inicio, fecha_fin, activa, limite_usos, usos_actuales) VALUES
('BIENVENIDO10',     'Descuento de Bienvenida',        '10% de descuento para nuevos clientes',              'PERCENTAGE', 10.00,  '2024-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, 100, 0),
('VERANO2024',       'Promoción de Verano',            '20% de descuento en servicios de peluquería',         'PERCENTAGE', 20.00,  '2024-12-01 00:00:00', '2025-03-31 23:59:59', TRUE, 50, 0),
('PASEO15',          'Descuento en Paseos',            '$15 de descuento en paseos',                          'FIXED',      15.00,  '2024-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, 200, 0),
('VACUNA50',         'Oferta Vacunación',              'Bs. 50 de descuento en vacunación',                   'FIXED',      50.00,  '2024-06-01 00:00:00', '2025-12-31 23:59:59', TRUE, 100, 0),
('FIEL25',           'Descuento Cliente Fiel',         '25% de descuento para clientes con más de 5 reservas', 'PERCENTAGE', 25.00,  '2024-01-01 00:00:00', '2025-12-31 23:59:59', TRUE, NULL, 0);

-- ---------------------------------------------------------------------------
-- ESTADO DE CORREO
-- ---------------------------------------------------------------------------
INSERT INTO correo_enviado (destino, asunto, cuerpo, tipo, estado, fecha_envio) VALUES
('admin@com', 'Bienvenido a PETCare', 'Bienvenido al sistema PETCare Home Services', 'SISTEMA', 'ENVIADO', NOW());

-- =============================================================================
-- Mensaje de confirmación
-- =============================================================================
DO $$
BEGIN
    RAISE NOTICE '============================================================';
    RAISE NOTICE 'Datos iniciales insertados exitosamente:';
    RAISE NOTICE '  - 7 Roles';
    RAISE NOTICE '  - 6 Estados de reserva';
    RAISE NOTICE '  - 10 Ciudades';
    RAISE NOTICE '  - 10 Estados/Departamentos';
    RAISE NOTICE '  - 5 Especies';
    RAISE NOTICE '  - 38 Razas';
    RAISE NOTICE '  - 12 Vacunas';
    RAISE NOTICE '  - 14 Menús';
    RAISE NOTICE '  - 50+ Submenús';
    RAISE NOTICE '  - 60+ Permisos (Admin, Cliente, Proveedor, Recepción, Veterinario, Peluquero, Paseador)';
    RAISE NOTICE '  - 18 Servicios';
    RAISE NOTICE '  - 3 Usuarios de ejemplo (Admin, Cliente, Proveedor)';
    RAISE NOTICE '  - 5 Promociones de ejemplo';
    RAISE NOTICE '============================================================';
END $$;
