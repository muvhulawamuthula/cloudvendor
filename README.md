# Cloud Vendor API

Spring Boot 3 REST service for managing cloud-vendor records. Provides full CRUD over a MySQL-backed `cloud_vendor_info` table, with a layered architecture, global exception handling, and JUnit 5 / Mockito tests.

| | |
|---|---|
| **Stack** | Java 17 · Spring Boot 3.5.5 · Spring Web · Spring Data JPA · MySQL · H2 (tests) · Maven |
| **Base URL** | `http://localhost:8080/cloudvendor` |
| **Module root** | [`cloudvendor/`](./cloudvendor) |

> **Docs in this repo**
> - [Architecture](./docs/architecture.md) — layers, data model, request flow, exceptions
> - [API reference](./docs/api.md) — endpoints, payloads, status codes (grounded in `CloudVendorController`)

---

## Features

- Create, read, update, and delete cloud-vendor records
- Get-by-ID responses wrapped in a consistent envelope (`ResponseHandler`)
- `CloudVendorNotFoundException` mapped to HTTP 404 via `@ControllerAdvice`
- Service-layer lookup by vendor name (`findByVendorName`) — available in the service/repository; not yet exposed as a REST route
- Unit tests for controller (MockMvc), service (Mockito), and repository layers

---

## Quick start

### Prerequisites

- JDK 17+
- Maven 3.6+ (or use the included Maven Wrapper: `./mvnw`)
- MySQL 8.x running locally (or reachable from your machine)

### Clone and build

```bash
git clone https://github.com/muvhulawamuthula/cloudvendor.git
cd cloudvendor/cloudvendor
./mvnw clean install
```

### Configure the database

1. Create the schema (name matches `application.yaml`):

```sql
CREATE DATABASE cloud_vendor;
```

2. Set credentials via environment overrides or edit `src/main/resources/application.yaml`. Prefer env vars so secrets never land in git:

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/cloud_vendor?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
export SPRING_DATASOURCE_USERNAME='root'
export SPRING_DATASOURCE_PASSWORD='your_password'
```

Default JPA settings in the repo:

| Property | Value | Notes |
|----------|-------|-------|
| `spring.jpa.hibernate.ddl-auto` | `create` | Recreates schema on startup — fine for local demos; switch to `validate` / `update` for shared environments |
| Entity table | `cloud_vendor_info` | Mapped on `CloudVendor` via `@Table` |
| Dialect | `MySQL8Dialect` | |

### Run

```bash
./mvnw spring-boot:run
# or
./mvnw clean package && java -jar target/cloudvendor-0.0.1-SNAPSHOT.jar
```

App listens on **http://localhost:8080**.

Smoke-check:

```bash
curl -s http://localhost:8080/cloudvendor | jq
```

---

## API at a glance

Full contract: [docs/api.md](./docs/api.md).

| Method | Path | Description | Response shape |
|--------|------|-------------|----------------|
| `GET` | `/cloudvendor/{vendorId}` | Fetch one vendor | Envelope: `massage`, `httpStatus`, `data` |
| `GET` | `/cloudvendor` | List all vendors | `CloudVendor[]` |
| `POST` | `/cloudvendor` | Create vendor | Plain text success message |
| `PUT` | `/cloudvendor` | Update vendor (full body, including `vendorId`) | Plain text success message |
| `DELETE` | `/cloudvendor/{vendorId}` | Delete vendor | Plain text success message |

Example create:

```bash
curl -s -X POST http://localhost:8080/cloudvendor \
  -H 'Content-Type: application/json' \
  -d '{
    "vendorId": "1",
    "vendorName": "Amazon",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  }'
```

---

## Project layout

Application code lives under the nested `cloudvendor/` Maven module (IDE metadata under `.idea/` is ignored).

```
cloudvendor/                          # GitHub repo root
├── README.md
├── docs/
│   ├── architecture.md
│   └── api.md
└── cloudvendor/                      # Maven project
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    └── src/
        ├── main/java/com/spring/cloudvendor/
        │   ├── CloudvendorApplication.java
        │   ├── controller/CloudVendorController.java
        │   ├── model/CloudVendor.java
        │   ├── repository/CloudVendorRepository.java
        │   ├── service/CloudVendorService.java
        │   ├── service/impl/CloudVendorServiceImpl.java
        │   ├── exception/
        │   │   ├── CloudVendorException.java
        │   │   ├── CloudVendorExceptionHandler.java
        │   │   └── CloudVendorNotFoundException.java
        │   └── response/ResponseHandler.java
        ├── main/resources/
        │   ├── application.properties   # spring.application.name
        │   └── application.yaml         # datasource + JPA
        └── test/
            ├── java/...                 # controller / service / repository tests
            └── resources/application.yaml  # H2 for tests
```

---

## Testing

Tests use **H2** (`src/test/resources/application.yaml`) so MySQL is not required for CI or local `mvn test`.

```bash
cd cloudvendor
./mvnw test
./mvnw test -Dtest=CloudVendorControllerTest
```

| Layer | Class | Approach |
|-------|-------|----------|
| Controller | `CloudVendorControllerTest` | `@WebMvcTest` + MockMvc + `@MockBean` service |
| Service | `CloudVendorServiceImplTest` | Mockito against the repository |
| Repository | `CloudVendorRepositoryTest` | Spring Data / H2 integration |

---

## Configuration notes

- **Port conflict**: set `server.port` in `application.properties` / YAML or `SERVER_PORT=8081`.
- **MySQL connection failures**: confirm the `cloud_vendor` database exists and that `SPRING_DATASOURCE_*` values match your local server.
- **Schema wiped on restart**: current `ddl-auto: create` drops and recreates tables at startup — intentional for demos, not for durable data.
- **Secrets**: do not commit real DB passwords. Override via environment or a local, gitignored profile.

---

## Contributing

1. Fork / branch from `main` (`feature/<name>` or `docs/<name>`).
2. Keep changes focused; update `docs/api.md` when endpoints change.
3. Run `./mvnw test` before opening a PR.

## License & contact

Personal learning / portfolio project. Questions: muthulamuvhulawavhofhololani@gmail.com
