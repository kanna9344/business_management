[README.md](https://github.com/user-attachments/files/27111110/README.md)
# Business Management Backend API

> Version: `1.0.0` | Base URL: `http://localhost:8080`  
> Contact: [support@businessmgmt.com](mailto:support@businessmgmt.com)

This API collection contains all the backend APIs for the **Business Management System**.

---

## Table of Contents

- [Project Setup](#project-setup)
- [System Overview](#system-overview)
- [Authentication](#authentication)
- [Roles & Access](#roles--access)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication-apis)
  - [Business Management](#business-management-apis)
  - [Shop Management](#shop-management-apis)
  - [Request Management](#request-management-apis)
- [Data Models](#data-models)

---

## Project Setup

### Tech Stack

| Technology | Version                         |
|------------|---------------------------------|
| Java       | 17                              |
| Spring Boot | 3.2.5                          |
| Build Tool | Maven                           |
| Database   | MySQL                           |
| Security   | Spring Security + JWT (JJWT 0.11.5) |
| ORM        | Spring Data JPA / Hibernate     |

### Prerequisites

- Java 17 installed
- MySQL server running on `localhost:3306`
- Maven installed

### Database Configuration

Create a MySQL database named `business_db`:

```sql
CREATE DATABASE business_db;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/business_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
jwt.token=<your_secret_key>
jwt.expiry=3600000
```

### How to Run

```bash
# Clone the project
git clone <repository-url>
cd app

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080`.

---

## System Overview

This system manages three main entities:

- **Business** – Represents a registered business
- **Shop** – A shop that belongs to a Business
- **Request** – A denomination request made by a shop

---

## Authentication

This API uses **JWT Bearer Token** authentication.

1. Register a user via `POST /auth/register`
2. Login via `POST /auth/login` to receive the JWT token
3. Include the token in all protected requests as:
   ```
   Authorization: Bearer <token>
   ```

---

## Roles & Access

| Role       | Access                                            |
|------------|---------------------------------------------------|
| `ADMIN`    | Can view any Business, Shop, or Request           |
| `BUSINESS` | Can manage only their own Business/Shop/Request   |

---

## API Endpoints

### Authentication APIs

---

#### `POST /auth/register` — Register a new user

Registers a new user in the system. Roles can be `ADMIN` or `BUSINESS`. Password must be at least **12 characters** long.

**Request Body**

```json
{
  "username": "Priya",
  "password": "Priya@1234567",
  "roles": ["BUSINESS"]
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | User registered successfully |
| `400`  | Validation error – missing or invalid fields |
| `500`  | Internal server error |

<details>
<summary>200 – Success Example</summary>

```json
{
  "status": 200,
  "message": "User created successfully",
  "isSuccess": true,
  "errors": {}
}
```
</details>

<details>
<summary>400 – Validation Error Examples</summary>

Username missing:
```json
{
  "status": 400,
  "isSuccess": false,
  "errors": { "username": "Username is required" }
}
```

Password too short:
```json
{
  "status": 400,
  "isSuccess": false,
  "errors": { "password": "Minimum 12 characters required" }
}
```
</details>

**Error Scenarios**

- Missing username → 400 Validation Error
- Missing password → 400 Validation Error
- Password less than 12 characters → 400 Validation Error
- Missing roles → 400 Validation Error

---

#### `POST /auth/login` — Login and get JWT token

Authenticates a registered user and returns a JWT token. Use this token in the `Authorization: Bearer <token>` header for all protected endpoints.

**Request Body**

```json
{
  "username": "Priya",
  "password": "Priya@1234567"
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Login successful – returns JWT token |
| `400`  | Validation error – username or password is null |
| `500`  | User not found or credentials invalid |

<details>
<summary>200 – Success Example</summary>

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
</details>

<details>
<summary>500 – Bad Credentials Example</summary>

```json
{
  "status": 500,
  "message": "Bad credentials",
  "isSuccess": false,
  "errors": {}
}
```
</details>

**Error Scenarios**

- `username` is null → 400 Validation Error
- `password` is null → 400 Validation Error
- User not found / wrong credentials → 500 Error

---

### Business Management APIs

> 🔒 All endpoints require `Authorization: Bearer <token>`

---

#### `POST /business/create` — Create a new Business

Creates a new Business record. Only authenticated **BUSINESS** users can create a business. The `createdBy` field is automatically set from the JWT token.

**Request Body**

```json
{
  "businessName": "Nair Retail Group",
  "ownerName": "Priya Nair",
  "ownerAddress": "23, Anna Salai, Chennai"
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Business created successfully |
| `400`  | Validation error – required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Internal server error |

<details>
<summary>200 – Success Example</summary>

```json
{
  "id": 1,
  "businessName": "Nair Retail Group",
  "ownerName": "Priya Nair",
  "ownerAddress": "23, Anna Salai, Chennai",
  "createdBy": "Priya",
  "createdAt": "2026-04-15T10:30:00.000Z"
}
```
</details>

---

#### `PUT /business/update` — Update an existing Business

Updates a Business record. The `id` field is required.

**Request Body**

```json
{
  "id": 1,
  "businessName": "Tech Corp Updated",
  "ownerName": "Sara Lee",
  "ownerAddress": "45, Orchard Rd, Singapore"
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Business updated successfully |
| `400`  | Validation error – id or required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Business not found or unauthorized access |

**Error Scenarios**

- Business ID not found → 500 Error (`Business not found`)
- BUSINESS user tries to update another user's business → 500 Error (`Unauthorized`)

---

#### `GET /business/view` — View a Business by ID

Retrieves Business details by ID.
- **ADMIN** users can view any Business.
- **BUSINESS** users can only view their own Business.

**Query Parameters**

| Parameter | Type    | Required | Description              |
|-----------|---------|----------|--------------------------|
| `id`      | integer | Yes      | ID of the Business to retrieve |

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Business details retrieved successfully |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Business not found or unauthorized access |

**Error Scenarios**

- BUSINESS user tries to view another user's business → 500 Error (`Unauthorized`)

---

### Shop Management APIs

> 🔒 All endpoints require `Authorization: Bearer <token>`

---

#### `POST /shop/create` — Create a new Shop

Creates a new Shop under a Business. The `busId` must refer to an existing Business. Only authenticated **BUSINESS** users can create shops.

**Request Body**

```json
{
  "shopName": "Nair Supermart",
  "busId": 1
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Shop created successfully |
| `400`  | Validation error – required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Internal server error |

<details>
<summary>200 – Success Example</summary>

```json
{
  "id": 1,
  "shopName": "Nair Supermart",
  "busId": 1,
  "createdBy": "Priya",
  "createdAt": "2026-04-15T10:30:00.000Z"
}
```
</details>

---

#### `PUT /shop/update` — Update an existing Shop

Updates a Shop record. The `id` field is required.

**Request Body**

```json
{
  "id": 1,
  "shopName": "Smith Main Store"
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Shop updated successfully |
| `400`  | Validation error – id or required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Shop not found or unauthorized access |

**Error Scenarios**

- Shop ID not found → 500 Error (`Shop not found`)
- BUSINESS user tries to update a shop not belonging to their business → 500 Error (`Unauthorized`)

---

#### `GET /shop/view` — View a Shop by ID

Retrieves Shop details by ID.
- **ADMIN** users can view any Shop.
- **BUSINESS** users can only view shops that belong to their own business.

**Query Parameters**

| Parameter | Type    | Required | Description            |
|-----------|---------|----------|------------------------|
| `id`      | integer | Yes      | ID of the Shop to retrieve |

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Shop details retrieved successfully |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Shop not found or unauthorized access |

**Error Scenarios**

- BUSINESS user views a shop not belonging to their business → 500 Error (`Unauthorized`)

---

### Request Management APIs

> 🔒 Most endpoints require `Authorization: Bearer <token>`

---

#### `POST /request/create` — Create a new denomination Request

Creates a new denomination Request for a Shop. The `shopId` must refer to an existing Shop. At least one denomination is required. Status is automatically set to `PENDING` on creation.

**Request Body**

```json
{
  "requestDate": "2026-04-15",
  "shopId": 1,
  "denominations": [
    { "note": "2000", "count": 6 },
    { "note": "100", "count": 20 }
  ]
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Request created successfully |
| `400`  | Validation error – required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Internal server error |

<details>
<summary>200 – Success Example</summary>

```json
{
  "id": 1,
  "requestDate": "2026-04-15",
  "status": "PENDING",
  "shopId": 1,
  "denominations": [
    { "id": 1, "note": "2000", "count": 6, "reqId": 1 },
    { "id": 2, "note": "100", "count": 20, "reqId": 1 }
  ],
  "createdBy": "Priya",
  "createdAt": "2026-04-15T10:30:00.000Z"
}
```
</details>

---

#### `PUT /request/update` — Update an existing Request

Updates a denomination Request. The `id` field is required.

**Request Body**

```json
{
  "id": 1,
  "requestDate": "2026-06-26",
  "shopId": 1,
  "denominations": [
    { "note": "2000", "count": 10 }
  ]
}
```

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Request updated successfully |
| `400`  | Validation error – id or required fields missing |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Request not found or unauthorized access |

**Error Scenarios**

- Request ID not found → 500 Error (`Request not found`)
- BUSINESS user tries to update a request not belonging to their business → 500 Error (`Unauthorized`)

---

#### `PATCH /request/completeRequest` — Complete a Transaction

Marks a denomination Request as **COMPLETED**. This is the final step in the request lifecycle: `PENDING → COMPLETED`.

**Query Parameters**

| Parameter | Type    | Required | Description                      |
|-----------|---------|----------|----------------------------------|
| `reqId`   | integer | Yes      | ID of the Request to complete    |

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Transaction completed successfully |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Request not found or already completed |

<details>
<summary>200 – Success Example</summary>

```json
{
  "status": 200,
  "message": "Transaction completed successfully",
  "isSuccess": true,
  "errors": {}
}
```
</details>

---

#### `GET /request/view` — View a Request by ID

Retrieves Request details by ID.
- **ADMIN** users can view any Request.
- **BUSINESS** users can only view requests that belong to their own business's shops.

**Query Parameters**

| Parameter | Type    | Required | Description                   |
|-----------|---------|----------|-------------------------------|
| `id`      | integer | Yes      | ID of the Request to retrieve |

**Responses**

| Status | Description |
|--------|-------------|
| `200`  | Request details retrieved successfully |
| `401`  | Unauthorized – JWT token missing or invalid |
| `500`  | Request not found or unauthorized access |

**Error Scenarios**

- BUSINESS user views a request not belonging to their business → 500 Error (`Unauthorized`)

---

## Data Models

### UserRegisterRequest

| Field      | Type     | Required | Description                        |
|------------|----------|----------|------------------------------------|
| `username` | string   | Yes      | Unique username                    |
| `password` | string   | Yes      | Minimum 12 characters              |
| `roles`    | string[] | Yes      | One or more roles: `ADMIN`, `BUSINESS` |

### UserLoginRequest

| Field      | Type   | Required |
|------------|--------|----------|
| `username` | string | Yes      |
| `password` | string | Yes      |

### BusinessCreateRequest

| Field          | Type   | Required |
|----------------|--------|----------|
| `businessName` | string | Yes      |
| `ownerName`    | string | Yes      |
| `ownerAddress` | string | Yes      |

### BusinessUpdateRequest

| Field          | Type    | Required | Description                    |
|----------------|---------|----------|--------------------------------|
| `id`           | integer | Yes      | ID of the business to update   |
| `businessName` | string  | Yes      |                                |
| `ownerName`    | string  | Yes      |                                |
| `ownerAddress` | string  | Yes      |                                |

### BusinessResponse

| Field          | Type      | Description              |
|----------------|-----------|--------------------------|
| `id`           | integer   |                          |
| `businessName` | string    |                          |
| `ownerName`    | string    |                          |
| `ownerAddress` | string    |                          |
| `createdBy`    | string    |                          |
| `createdAt`    | date-time |                          |
| `updatedBy`    | string    |                          |
| `updatedAt`    | date-time |                          |

### ShopCreateRequest

| Field      | Type    | Required | Description                           |
|------------|---------|----------|---------------------------------------|
| `shopName` | string  | Yes      |                                       |
| `busId`    | integer | Yes      | ID of the Business this shop belongs to |

### ShopUpdateRequest

| Field      | Type    | Required | Description               |
|------------|---------|----------|---------------------------|
| `id`       | integer | Yes      | ID of the shop to update  |
| `shopName` | string  | Yes      |                           |

### ShopResponse

| Field      | Type      |
|------------|-----------|
| `id`       | integer   |
| `shopName` | string    |
| `busId`    | integer   |
| `createdBy`| string    |
| `createdAt`| date-time |
| `updatedBy`| string    |
| `updatedAt`| date-time |

### DenominationTo

| Field   | Type    | Required | Description                       |
|---------|---------|----------|-----------------------------------|
| `id`    | integer | No       | Auto-generated                    |
| `note`  | string  | Yes      | Currency denomination note value  |
| `count` | integer | Yes      | Number of notes                   |
| `reqId` | integer | No       | Associated Request ID             |

### RequestCreateRequest

| Field          | Type           | Required |
|----------------|----------------|----------|
| `requestDate`  | date           | Yes      |
| `shopId`       | integer        | Yes      |
| `denominations`| DenominationTo[] | Yes    |

### RequestUpdateRequest

| Field          | Type           | Required |
|----------------|----------------|----------|
| `id`           | integer        | Yes      |
| `requestDate`  | date           | Yes      |
| `shopId`       | integer        | No       |
| `denominations`| DenominationTo[] | Yes    |

### RequestResponse

| Field          | Type           | Description                         |
|----------------|----------------|-------------------------------------|
| `id`           | integer        |                                     |
| `requestDate`  | date           |                                     |
| `status`       | string         | `PENDING` or `COMPLETED`            |
| `shopId`       | integer        |                                     |
| `denominations`| DenominationTo[] |                                   |
| `createdBy`    | string         |                                     |
| `createdAt`    | date-time      |                                     |
| `updatedBy`    | string         |                                     |
| `updatedAt`    | date-time      |                                     |

### SuccessResponse

| Field       | Type    | Example                    |
|-------------|---------|----------------------------|
| `status`    | integer | `200`                      |
| `message`   | string  | `"Operation successful"`   |
| `isSuccess` | boolean | `true`                     |
| `errors`    | object  | `{}`                       |

### ErrorResponse

| Field       | Type    | Example                    |
|-------------|---------|----------------------------|
| `status`    | integer | `500`                      |
| `message`   | string  | `"An error occurred"`      |
| `isSuccess` | boolean | `false`                    |
| `errors`    | object  | `{}`                       |

### ValidationErrorResponse

| Field       | Type    | Example                                                        |
|-------------|---------|----------------------------------------------------------------|
| `status`    | integer | `400`                                                          |
| `message`   | string  | `null`                                                         |
| `isSuccess` | boolean | `false`                                                        |
| `errors`    | object  | `{ "username": "Username is required", "password": "..." }`   |

---

## Security

All protected endpoints use **Bearer Token** authentication via the `Authorization` header.

```
Authorization: Bearer <JWT token>
```

Obtain the token by calling `POST /auth/login`.
