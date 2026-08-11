# ARQUITECTURA MONOLITO POR CAPAS 

# PETCare Home Services

Sistema de gestión de servicios de cuidado de mascotas a domicilio. Plataforma full-stack con backend Spring Boot 3 y frontend Angular 19, desplegada con Docker Compose.

## Arquitectura

```
                   ┌──────────────────────────────────────────┐
                   │              Docker Network              │
                   │           (petcare-network)              │
                   │                                          │
  Browser ────────►│  ┌──────────┐    ┌──────────────────┐    │
  :4200            │  │ Frontend │───►│     Backend      │    │
                   │  │  Nginx   │    │   Spring Boot    │    │
                   │  │  :80     │    │     :8080        │    │
                   │  └──────────┘    └────────┬─────────┘    │
                   │                           │              │
                   │                  ┌────────▼─────────┐    │
                   │                  │    PostgreSQL    │    │
                   │                  │      :5432       │    │
                   │                  └──────────────────┘    │
                   │                                          │
                   │  ┌──────────┐                            │
                   │  │ pgAdmin  │──── PostgreSQL             │
                   │  │  :5050   │                            │
                   │  └──────────┘                            │
                   └──────────────────────────────────────────┘
```

#

## Prerrequisitos

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java       | 21+ (pom: `java.version=21`) | Backend runtime |
| Node.js    | 22      | Frontend build |
| npm        | 10+     | Dependencias frontend |
| Docker     | 24+     | Contenedores |
| Docker Compose | 2.20+ | Orquestación |
| Maven      | 3.9+    | Build backend |
| Apache JMeter | 5.6+ | Load testing |

## Arquitectura del Backend (Monolito por Capas)

El backend es un monolito Spring Boot organizado **primero por módulo de dominio y capas dentro de cada módulo**. Cada módulo es una *slice vertical* con esta estructura de dependencias:

```
Controller (Spring MVC, REST)
      │  valida y delega
      ▼
Service / ServiceImpl (lógica de negocio)
      │  aplica reglas, transacciones
      ▼
Mapper (MyBatis) ───► resources/mapper/*.xml (SQL)
      │
      ▼
PostgreSQL
```

Reglas de dependencia:

- `controller` solo depende de `service` y `dto`; nunca accede a `mapper`/`entity`.
- `service` implementa la lógica de negocio y orquesta `mapper` + `entity` + `dto`.
- `entity` son POJOs que reflejan las tablas; `dto` son los objetos que viajan por HTTP.
- Paquetes transversales sin acoplamiento a módulos: `config`, `security`, `common`.
- Los scripts SQL de inicialización viven en `basededatos/` y se aplican al arrancar PostgreSQL.

## Estructura del Proyecto

```
PetCare/
├── backend/                              # Monolito Spring Boot 3
│   ├── pom.xml                           # Java 21, MyBatis, Spring Security, JWT, Mail
│   ├── Dockerfile
│   ├── loadtest/                         # JMeter (petcare-load-test.jmx)
│   └── src/main/
│       ├── java/bo/capital/tec/pet/
│       │   ├── PetCareApplication.java
│       │   ├── config/                   # Seguridad, CORS, OpenAPI
│       │   ├── security/                 # JWT (util, filtro)
│       │   ├── common/                   # upload, email, response, constants, util
│       │   └── <modulo>/                 # auth, usuario, cliente, mascota, reserva, pago, ...
│       │       ├── controller/           # Endpoints REST
│       │       ├── service/              # Interfaz de negocio
│       │       ├── service/impl/         # Implementación
│       │       ├── mapper/               # Mappers MyBatis (interfaces Java)
│       │       ├── entity/               # POJOs de persistencia
│       │       └── dto/                  # Objetos de transferencia (request/response)
│       └── resources/
│           ├── application.yml           # + application-dev.yml / application-prod.yml
│           ├── logback-spring.xml
│           └── mapper/                   # SQL XML de MyBatis (uno por mapper)
├── frontend/
│   ├── src/app/                          # Módulos (usuario, mascota, reserva...) + core/ (guards, interceptors, services)
│   ├── nginx.conf
│   └── Dockerfile
├── basededatos/                          # Scripts SQL (01_database, 02_tables, 03_constraints, 04_indexes, 05_initial_data)
├── diagramas/                            # C4 (contexto, contenedores, componentes, clases)
├── docker-compose.yml
├── .env
└── README.md
```

**Módulos de dominio** (`backend/src/main/java/bo/capital/tec/pet/`): auth, bitacora, cliente, direccion, mascota, menu, notificacion, pago, persona, promocion, proveedor, reserva, rol, servicio, sucursal, usuario, vacuna.

## Inicio Rápido

### 1. Clonar y configurar

```bash
git clone <repository-url>
cd PetCare
cp .env .env.local  # Editar variables sensibles
```

### 2. Levantar con Docker Compose

```bash
docker-compose up --build -d
```

### 3. Verificar servicios

| Servicio   | URL                           |
|------------|-------------------------------|
| Frontend   | http://localhost:4200         |
| Backend    | http://localhost:8080         |


## Base de Datos

### Conexión

| Parámetro  | Valor          |
|------------|----------------|
| Host       | localhost      |
| Puerto     | 5432           |
| Database   | petcaredb      |
| Usuario    | postgres       |
| Contraseña | 123qwe         |

### pgAdmin

| Parámetro  | Valor               |
|------------|---------------------|
| Email      | admin@petcare.com   |
| Contraseña | admin123            |

Los scripts en `basededatos/` se ejecutan automáticamente al iniciar el contenedor PostgreSQL.

## Ejecución en Desarrollo

### Backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend
npm install
npm start
```

El frontend en desarrollo (`ng serve`) se ejecuta en `http://localhost:4200` y proxya las llamadas `/api/` al backend en `:8080`.

## Documentación API

El Swagger UI está disponible en:

```
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI 3 en JSON:

```
http://localhost:8080/v3/api-docs
```

## Flujo de Autenticación

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Login   │────────►│  Backend │────────►│  JWT     │
│  Form    │  POST   │  /auth   │  Token  │  Store   │
└──────────┘  creds  └──────────┘         └──────────┘
                                                │
                    ┌──────────┐         ┌───────▼──────┐
                    │ Protected│◄─────── │  Authorization│
                    │  Routes  │ Bearer  │  Header       │
                    └──────────┘  JWT    └──────────────┘
```

1. El usuario envía credenciales a `POST /api/v1/auth/login`
2. El backend valida y retorna un token JWT
3. El frontend almacena el token en `localStorage`
4. Cada petición incluye `Authorization: Bearer <token>`
5. El `JwtFilter` valida el token en cada request

## Testing

### Unit Tests (Backend)

```bash
cd backend
mvn test
```

### Integration Tests

```bash
cd backend
mvn verify
```

### Frontend Tests

```bash
cd frontend
npm test
```

### Load Testing

```bash
# Ejecutar con JMeter GUI
jmeter -t backend/loadtest/petcare-load-test.jmx

# Ejecutar en modo non-GUI
jmeter -n -t backend/loadtest/petcare-load-test.jmx \
  -l results.jtl -e -o report/
```

**Escenarios de prueba:**

| Escenario       | Usuarios | Ramp-up | Duración |
|-----------------|----------|---------|----------|
| Smoke Test      | 100      | 30s     | 5 min    |
| Load Test       | 500      | 60s     | 10 min   |
| Stress Test     | 1000     | 120s    | 15 min   |

**Flujos incluidos:**
- Login y obtención de token
- Listado de servicios
- Creación de reservas
- Consulta de estado de reserva
- Visualización de promociones
- Búsqueda de proveedores

## Despliegue

### Variables de Entorno (Producción)

```bash
export JWT_SECRET=<secret-key>
export DB_HOST=<db-host>
export DB_PORT=5432
export DB_NAME=petcaredb
export DB_USERNAME=<db-user>
export DB_PASSWORD=<db-password>
export MAIL_USERNAME=<mail-user>
export MAIL_PASSWORD=<mail-password>
```

### Producción

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
```

## Licencia

MIT License - ver archivo [LICENSE](LICENSE) para detalles.
