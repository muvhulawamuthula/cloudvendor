# API reference

Base URL (local default): **`http://localhost:8080`**

All routes below are declared on `CloudVendorController` with `@RequestMapping("/cloudvendor")`. Response bodies and status codes reflect the current controller, `ResponseHandler`, and `CloudVendorExceptionHandler` implementations — not aspirational OpenAPI.

Content type for request bodies: **`application/json`**.

---

## Resource: CloudVendor

| Field | Type | Required on write | Notes |
|-------|------|-------------------|-------|
| `vendorId` | string | Yes (client-supplied PK) | Used as path id for GET/DELETE and as body id for POST/PUT |
| `vendorName` | string | No validation enforced | |
| `vendorAddress` | string | No validation enforced | |
| `vendorPhoneNumber` | string | No validation enforced | |

Example JSON:

```json
{
  "vendorId": "1",
  "vendorName": "Amazon",
  "vendorAddress": "USA",
  "vendorPhoneNumber": "1234567890"
}
```

---

## Endpoints

### 1. Get vendor by ID

```
GET /cloudvendor/{vendorId}
```

| | |
|---|---|
| Path params | `vendorId` (string) |
| Success | **200 OK** — JSON envelope from `ResponseHandler` |
| Not found | **404 Not Found** — `CloudVendorException` body |

**Success body** (keys are exactly as coded — including `massage`):

```json
{
  "massage": "Requested Vendor Details are Given here",
  "httpStatus": "OK",
  "data": {
    "vendorId": "1",
    "vendorName": "Amazon",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  }
}
```

**404 body** (shape of `CloudVendorException`):

```json
{
  "massage": "Requested Cloud Vendor Does Not Exist",
  "throwable": null,
  "httpStatus": "NOT_FOUND"
}
```

```bash
curl -s http://localhost:8080/cloudvendor/1 | jq
```

---

### 2. List all vendors

```
GET /cloudvendor
```

| | |
|---|---|
| Success | **200 OK** — JSON array of `CloudVendor` (no envelope) |
| Empty DB | **200 OK** — `[]` |

```json
[
  {
    "vendorId": "1",
    "vendorName": "Amazon",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  }
]
```

```bash
curl -s http://localhost:8080/cloudvendor | jq
```

---

### 3. Create vendor

```
POST /cloudvendor
```

| | |
|---|---|
| Body | `CloudVendor` JSON |
| Success | **200 OK** — plain text |
| Persistence | `repository.save` — inserts, or overwrites if `vendorId` already exists |

**Response body (text/plain-ish string):**

```
Cloud Vendor Created successfully!
```

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

### 4. Update vendor

```
PUT /cloudvendor
```

| | |
|---|---|
| Body | Full `CloudVendor` JSON including `vendorId` of the row to update |
| Success | **200 OK** — plain text |
| Persistence | Same `save` path as create (no separate “must exist” check in the service) |

**Response:**

```
Cloud Vendor Updated successfully!
```

```bash
curl -s -X PUT http://localhost:8080/cloudvendor \
  -H 'Content-Type: application/json' \
  -d '{
    "vendorId": "1",
    "vendorName": "Amazon Web Services",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  }'
```

---

### 5. Delete vendor

```
DELETE /cloudvendor/{vendorId}
```

| | |
|---|---|
| Path params | `vendorId` (string) |
| Success | **200 OK** — plain text |
| Missing id | Spring Data `deleteById` on a missing key does **not** raise `CloudVendorNotFoundException` in the current service impl |

**Response:**

```
Cloud Vendor Deleted successfully!
```

```bash
curl -s -X DELETE http://localhost:8080/cloudvendor/1
```

---

## Summary table

| Method | Path | Success status | Body in | Body out |
|--------|------|----------------|---------|----------|
| `GET` | `/cloudvendor/{vendorId}` | 200 | — | Envelope (`massage`, `httpStatus`, `data`) |
| `GET` | `/cloudvendor` | 200 | — | `CloudVendor[]` |
| `POST` | `/cloudvendor` | 200 | `CloudVendor` | String message |
| `PUT` | `/cloudvendor` | 200 | `CloudVendor` | String message |
| `DELETE` | `/cloudvendor/{vendorId}` | 200 | — | String message |

---

## Not exposed over HTTP (yet)

`CloudVendorService.getByVendorName(String)` / `CloudVendorRepository.findByVendorName(String)` exist in code but have **no** controller mapping. Do not call a name-search URL expecting it to work until a route is added (for example `GET /cloudvendor?name=Amazon`).

---

## Error handling notes

| Condition | Status | Handler |
|-----------|--------|---------|
| Unknown `vendorId` on **GET by id** | 404 | `CloudVendorExceptionHandler` → `CloudVendorException` |
| Malformed JSON | 400 | Spring default |
| Unhandled exceptions | 500 | Spring Boot default `/error` |

There is no authentication or authorization on these endpoints.

---

## Quick Postman / curl checklist

1. `POST /cloudvendor` with a full JSON body → expect success string.
2. `GET /cloudvendor/{vendorId}` → expect envelope with `data`.
3. `GET /cloudvendor` → expect array including the new row.
4. `PUT /cloudvendor` with changed fields → expect success string; GET again to confirm.
5. `DELETE /cloudvendor/{vendorId}` → expect success string; GET by id → **404**.
6. `GET /cloudvendor/does-not-exist` → **404** with `massage`: `Requested Cloud Vendor Does Not Exist`.
