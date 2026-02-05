# Simple Todo Service

A simple backend service for managing to-do list operations.

---
## Service Overview

This service provides a REST API to manage to-do items with clear rules around due dates and state transitions.

### Supported Operations

- **Add a to-do item**
- **Update a to-do item’s description**
    - Description updates are **not allowed** if the item is already past its due date
    - Updates are currently allowed for items in DONE status (this can be adjusted based on requirements)
- **Mark a to-do item as _done_ or _not done_**
    - Status changes are **not allowed** for past-due items
- **Retrieve all to-do items**
    - By default, only items that are **not done** are returned, including those with NOT_DONE and PAST_DUE statuses
    - Optional support to retrieve **all items**, regardless of status
- **Retrieve details of a specific to-do item** by ID

---

## Handling of Past-Due Items

The service currently handles past-due items in a simplified way:

- During the **get-all-items** operation, the service performs a bulk check to detect and update any items that have become past due.
- During **individual get or update operations**, the specific item is also checked and updated PAST_DUE status if it has crossed its due date.
- Once an item is marked as past due, further modifications (such as updating the description or changing the done/not-done status) are disallowed.

This approach was chosen to keep the initial implementation simple and easy to reason about.  
Alternative approaches—such as scheduled background jobs or database-level constraints as future improvements.

---

## Assumptions

- The system is **single-user** and does not support user-based to-do items.
- Authentication and authorization are intentionally **not implemented**, as per requirements.
- All timestamps are handled in **UTC**.
- The dataset is expected to be small (no pagination or archiving required).

---
## API documentation (Swagger UI)

Once the service is running, you can access interactive API docs at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`

Swagger groups endpoints into the following tags:

- **Todos**: create, update, and list todo items.
- **Health**: basic health check endpoint.

---
## Prerequisites

- Git
- Docker (for containerized run)
- Java 21 (for local runs)

---

## Tech stack

- Runtime: Java 21
- Framework: Spring Boot (WebMVC)
- Persistence: Spring Data JPA + Hibernate
- Database: H2 in-memory database
- Validation: Jakarta Bean Validation
- API docs: springdoc-openapi (Swagger UI)

---

## Running with Docker

Clone the repository:
```bash
git clone https://github.com/saritay13/simple-todo-service.git
cd simple-todo-service
```

Build the Docker image:
```bash
docker build -t simple-todo-service .
```

Run the container:

```bash
docker run --rm -p 8080:8080 simple-todo-service
```

After the container starts, open Swagger UI in your browser:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---
---

## Running locally (no Docker)

From the repository root:

```bash
./mvnw spring-boot:run
```

Then open Swagger UI:

- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Run tests locally

From the repository root:

```bash
./mvnw test
```

---