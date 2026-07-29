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
| Java       | 25      | Backend runtime |
| Node.js    | 22      | Frontend build |
| npm        | 10+     | Dependencias frontend |
| Docker     | 24+     | Contenedores |
| Docker Compose | 2.20+ | Orquestación |
| Maven      | 3.9+ (wrapper incluido) | Build backend |
| Apache JMeter | 5.6+ | Load testing |

## Estructura del Proyecto

```
PetCare/
├── backend/
│   ├── src/main/java/bo/capital/tec/pet/
│   │   ├── config/          # Configuración Spring Security, CORS
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── service/         # Lógica de negocio
│   │   └── PetCareApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── logback-spring.xml
│   ├── loadtest/
│   │   └── petcare-load-test.jmx
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/app/
│   │   ├── components/      # Componentes Angular
│   │   ├── services/        # Servicios HTTP
│   │   ├── models/          # Interfaces TypeScript
│   │   └── guards/          # Guards de autenticación
│   ├── nginx.conf
│   └── Dockerfile
├── basededatos/              # Scripts SQL de inicialización
├── docker-compose.yml
├── .env
├── .gitignore
└── README.md
```

# Base de datos
https://mermaid.ai/play?utm_source=mermaid_live_editor&utm_medium=share#pako:eNrNWktv4zYQ_iuCgL1lgzxcJ_B1i172UhTopQggjCnaYUOJKkW522Tz3zuUSD0oUvEjiRwgic3hNyLnxZmhXmIiUhqvYip_ZbCVkD3kEf7QUkEqEklLKncQvTSj-mfNtjjGgEcsjX7_3hF2IMkjyCgX2VrSblzRHypKaUkkKwgT-RhBBBdyPMyIyEU3zHJFt1RGQqa0x0SxTC82KyIiKSiaJqB81KpIR9S1EJxCjqvjFGnjZ-2oLNslvz6YD4RVKaRHCCX687tv9ynbigEprzLkRiIOiqkq9RBEvh1SZhBDYyMn2cZQBgNG-Gwj6ASZ_fZ91q2mTFKijfeg3RLg3LPZWoliPF6w0jMKBUjcDs2VOM1AtBtKuqGS5oTBnrI2RBMO5ldEgaMih3cxukLiCmUCBeWcpR7Rl3Rb5bjt8AzCvC6tcFObQfCyFJoB4wOMFk20oUhNciAsY0NN16gtzQcWY7TSWuUZKEYKfmpADB8Udk1AFNuJWfdZlRVIdljUq5Ccowt7baWAsvwXz7SRdo2pjzzyTVlUHD-j1RJCS8_5qf-jiZXJBmqrLse811z8U1HoW3ytHCWeaI5JwQbzgsdzUEOyr9kZmVqYP8ohszPwJAz11ce5UiC5ak1V8jczrjPyxrJaHymuz5JVY1l6kYc78meeqhkmHx8kxrB3TUmnIRkFh2SnJSTHw5xS6RV0BZw9-xCU48mb9ymzJNQFJYx-nPPPkRfA82nZWpuB1rIZxedz2ivhOnmjhxxIoUN-8riqN50LBeW8BxWUBBdxknaPyX5tjVNQX70UaiZkgHySQcyeNi0bttCAAySj73Moi6TYUZoK-eG2d0TlaSkSUuRHxJpKVUlInrJDcn_cONswMkhKLWcstGva7BFAd8sYYeLjQrh9fooSrEs_PLYq1U_iW_fA4hDlvYaS7ukOgYSka2fgo4Xs9w7mNPWk8VtU_b7NOGv3LYdAzmGUeAZ-XVakkiXw9zhBg92CPbsWw61Gj0LW8QGKxp0DZAyssr-YI-LHwC5h5j5gWYicrdmnGV3r7wySEvWQ-wSdsFxjfZQNO8_K7ZibBU-nfPokPlH2tjxp8pzp1qjZTzhmNEmOmRae4NNknz7Q58EGcGzfuIsdvg6yLx91TiGFVD6rwe2AVEc1jg88mDEcMrRTogNEgvug5cx-tmWlkiJp9r_3HcZett_wdIm9nB4KPkrLeuSdNiQ357fS50J5Epcdbl7qSp25zUmxrl1LP2xmmWO8ycTht0Xj6HZ8y0qxQiR6RuW_ONoBVkm-CSfFo9BJbcWlmywYpauyn69aoh5Nmh7Nuegv4YIAP_CYN9BANek_JqxaMoHK2EctPseaQ1KwFYeIZ5_tj405o0rgEasf5slNm_PXT-xOq0RJyEsgIZE1YvVzISJDva5BZxiDimkGieMx25a673Dz0cULVXE3omYUJfY39UcXX9cVz7ywbDkl4zrhk6W3ZgoIZkXvKDnXpFq7zJVOAcb5YjOO7Nx4DvpSThsZPk0OQmBHziu66wfPtp4vEkhTRLkwffeY6Fpf7SP4rpUpsGoTCc137NB3PDB6KearIqGshjFNrw8DnSzEWyY2dHaHBRaYeJaNjNW1P72XeWueL1-iPygfZCjmVaKfP79-FS_2jZpV9BBvnhIT2ZopD7EP0L2XYjBdoj6EGc5vw4yEY_e9F4O0718YnG0htvMs0M4zMHtrbmDWpcwsC7KzhqD6jtcB6rsk89mC9az9gPhrQfXloUHZu0SDsDdP-o_nGfbKrBOE_hriHZjd520fP40wszq1NjdGBlRfthhE3bc29IBW7H2FQdgielorAZCjDWdh9qrAgGxV4SyvXv40Qk9pXcGsZBphZgVE0DXOraDbXsG0GCaAjii6mS502MEc8elTu-HWXmyr-RSulknQ39sGZOsVzXePx7vbdHplbaTpj769rb24uNtwLMP2mqxnmCTUtQx3AwHYm0sO4NxFWnudRplZwzjevpc7jR1ODj533CBo-YxI7npMV-U4Xs3HnvxN1dxZ9KAM60y5P9x9b8PIUDh7snGk5DLRhY5F4md3uhMdBlm6gfXH3BjhwNs01UDt9z4svoi3kqXxagO8pBcxVlAZ6O9xnaw9xOqRZmjcmkUK8kljXhFUQP6XEFm8UrJCmBTV9rFl0iQ75k3wdhQrqJTKbwLTt3h1s7ivmcSrl_hHvPr6y3JxeX93vby7vrm_ub5dXMT_4ejdcnl5f3O7vLpfXF_dLJf3rxfxc_3Y28vF3eLu9upqubhZ3CK35ev_Qwrs8Q


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
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
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
./mvnw test
```

### Integration Tests

```bash
cd backend
./mvnw verify
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
