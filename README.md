# Simple Todo Service

A simple backend service for managing to-do list operations.

The service supports basic CRUD operations such as creating, updating, and retrieving to-do items.

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