# Architecture

How the Cloud Vendor API is structured, how a request moves through the layers, and how persistence and errors are handled. Everything below is taken from the code under `cloudvendor/src/main/java/com/spring/cloudvendor/`.

---

## High-level view

```
                    ┌─────────────────────────────────────┐
  HTTP clients      │  CloudVendorController              │
  (curl / Postman)  │  @RestController                    │
                    │  @RequestMapping("/cloudvendor")    │
                    └─────────────────┬───────────────────┘
                                      │
                                      ▼
                    ┌─────────────────────────────────────┐
                    │  CloudVendorService (interface)     │
                    │  CloudVendorServiceImpl (@Service)  │
                    └─────────────────┬───────────────────┘
                                      │
                                      ▼
                    ┌─────────────────────────────────────┐
                    │  CloudVendorRepository              │
                    │  extends JpaRepository<…, String>   │
                    └─────────────────┬───────────────────┘
                                      │
                                      ▼
                    ┌─────────────────────────────────────┐
                    │  MySQL · database cloud_vendor      │
                    │  table cloud_vendor_info            │
                    └─────────────────────────────────────┘

  Cross-cutting:
    CloudVendorExceptionHandler (@ControllerAdvice)
    ResponseHandler (success envelope for get-by-id)
```

Classic Spring Boot layering: **controller → service → repository → database**. Controllers stay thin; business rules and not-found checks live in the service; persistence is Spring Data JPA.

---

## Packages and responsibilities

| Package / type | Role |
|----------------|------|
| `CloudvendorApplication` | `@SpringBootApplication` entry point |
| `controller.CloudVendorController` | Maps HTTP verbs/paths; delegates to `CloudVendorService`; wraps get-by-id with `ResponseHandler` |
| `model.CloudVendor` | JPA entity (`@Entity`, `@Table(name = "cloud_vendor_info")`, `@Id` on `vendorId`) |
| `repository.CloudVendorRepository` | `JpaRepository<CloudVendor, String>` plus `findByVendorName(String)` |
| `service.CloudVendorService` | Port: create / update / delete / get / getAll / getByVendorName |
| `service.impl.CloudVendorServiceImpl` | Adapter: calls repository; throws `CloudVendorNotFoundException` when get-by-id misses |
| `exception.*` | Runtime not-found exception, DTO (`CloudVendorException`), and `@ControllerAdvice` handler → HTTP 404 |
| `response.ResponseHandler` | Builds a `Map` envelope (`massage`, `httpStatus`, `data`) returned as `ResponseEntity` |

Constructor injection is used on the controller and service implementation (no field `@Autowired`).

---

## Domain model

`CloudVendor` maps 1:1 to table `cloud_vendor_info`:

| Field | Java type | Persistence |
|-------|-----------|-------------|
| `vendorId` | `String` | Primary key (`@Id`) — client-supplied, not generated |
| `vendorName` | `String` | Column |
| `vendorAddress` | `String` | Column |
| `vendorPhoneNumber` | `String` | Column |

There is no Bean Validation (`@NotNull` / `@Size`) on the entity today; the API accepts whatever JSON Jackson can bind.

---

## Request flow (examples)

### Get by ID — `GET /cloudvendor/{vendorId}`

1. `CloudVendorController.getCloudVendorDetails` receives the path variable.
2. Calls `cloudVendorService.getCloudVendor(vendorId)`.
3. `CloudVendorServiceImpl` calls `findById`; if empty, throws `CloudVendorNotFoundException("Requested Cloud Vendor Does Not Exist")`.
4. On success, controller returns `ResponseHandler.responseBuilder("Requested Vendor Details are Given here", HttpStatus.OK, vendor)`.
5. On not-found, `CloudVendorExceptionHandler` catches the exception and returns `ResponseEntity<CloudVendorException>` with **404**.

### Create — `POST /cloudvendor`

1. Controller deserializes the body to `CloudVendor`.
2. Service calls `cloudVendorRepository.save(cloudVendor)` (insert or overwrite by ID).
3. Controller returns the plain string `Cloud Vendor Created successfully!` with default **200**.

Update and delete follow the same pattern (save / `deleteById`), also returning plain success strings. List-all returns the raw `List<CloudVendor>` with no envelope.

---

## Service API vs REST surface

| Service method | Exposed on controller? |
|----------------|------------------------|
| `createCloudVendor` | Yes — `POST /cloudvendor` |
| `updateCloudVendor` | Yes — `PUT /cloudvendor` |
| `deleteCloudVendor` | Yes — `DELETE /cloudvendor/{vendorId}` |
| `getCloudVendor` | Yes — `GET /cloudvendor/{vendorId}` |
| `getAllCloudVendor` | Yes — `GET /cloudvendor` |
| `getByVendorName` | **No** — implemented via `CloudVendorRepository.findByVendorName`, but no `@GetMapping` yet |

When documenting or extending the API, treat name search as an internal capability until a route is added.

---

## Exception handling

```
CloudVendorNotFoundException (RuntimeException)
        │
        ▼  @ExceptionHandler
CloudVendorExceptionHandler (@ControllerAdvice)
        │
        ▼
ResponseEntity<CloudVendorException>  status = NOT_FOUND (404)
```

`CloudVendorException` is a simple holder (`massage`, `throwable`, `httpStatus`) — not an `Exception` subclass. JSON serialization will expose those getters (note the spelling **`massage`**, matching `ResponseHandler`).

Only `CloudVendorNotFoundException` is handled explicitly today; other failures fall through to Spring Boot’s default error response.

---

## Configuration & runtime

| Concern | Source | Behavior |
|---------|--------|----------|
| App name | `application.properties` | `spring.application.name=cloudvendor` |
| Datasource / JPA | `application.yaml` | MySQL URL `jdbc:mysql://localhost:3306/cloud_vendor…`, `ddl-auto: create`, SQL logging DEBUG |
| Tests | `src/test/resources/application.yaml` | H2 in-memory so tests do not need MySQL |

`ddl-auto: create` recreates the schema on each startup — suitable for local demos; use `validate` or Flyway/Liquibase for anything that must keep data.

Prefer overriding credentials with `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` (or a local profile) rather than committing secrets.

---

## Testing architecture

| Test | Stereotype | Collaborators |
|------|------------|---------------|
| `CloudVendorControllerTest` | `@WebMvcTest(CloudVendorController.class)` | `@MockBean CloudVendorService` — HTTP contract only |
| `CloudVendorServiceImplTest` | Unit | Mocked `CloudVendorRepository` |
| `CloudVendorRepositoryTest` | Spring Data / H2 | Hits real repository methods against in-memory DB |
| `CloudvendorApplicationTests` | Context load smoke test | |

This mirrors the production layers and keeps controller tests free of MySQL.

---

## Design notes / follow-ups

- **Response consistency**: get-by-id uses an envelope; list/create/update/delete do not. A future pass could standardize on one response type.
- **Spelling**: both `ResponseHandler` and `CloudVendorException` use the key/field name `massage` (not `message`). Clients and docs must match the wire format.
- **Idempotent IDs**: `vendorId` is client-assigned; `save` on an existing ID updates the row (JPA merge semantics).
- **Name search**: wire `getByVendorName` to something like `GET /cloudvendor?name=` when you need it over HTTP.
- **Security**: no Spring Security dependency yet — all endpoints are open on the network that can reach port 8080.
