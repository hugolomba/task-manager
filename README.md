# Task Manager API

A secure REST API for managing personal tasks, built with Java 21 and Spring Boot. The project includes JWT authentication, PostgreSQL persistence, Flyway migrations, Docker support, validation, pagination, filtering, and automated tests.

This project was built as a backend portfolio project to demonstrate production-style API development with Spring Boot.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Docker and Docker Compose
- Maven
- JUnit 5 and Mockito
- Springdoc OpenAPI / Swagger UI

## Main Features

- User registration and login
- BCrypt password hashing
- JWT-based stateless authentication
- Authenticated users can manage only their own tasks
- Task CRUD operations
- Category creation and lookup support
- Pagination and filtering for task search
- Centralized exception handling
- Request validation with clear error responses
- PostgreSQL schema management with Flyway
- Dockerized local environment
- Unit tests for services and authentication flow

## Architecture

The application follows a layered Spring Boot structure:

```text
controller  -> HTTP endpoints and request handling
service     -> business rules and transaction boundaries
repository  -> database access through Spring Data JPA
entity      -> JPA database models
dto         -> request and response contracts
mapper      -> conversion between entities and DTOs
security    -> JWT filter, authentication entry point, security config
exception   -> centralized error handling
```

Key design decisions:

- Controllers do not expose JPA entities for tasks and users.
- Passwords are never returned in API responses.
- Tasks are linked to the authenticated user from the JWT, not to a `userId` supplied by the client.
- Flyway owns the database schema instead of relying on Hibernate auto-DDL.
- Docker Compose can run the API, PostgreSQL, and pgAdmin together.

## Running Locally

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker and Docker Compose

### Option 1: Run With Docker Compose

```bash
docker compose up --build
```

Services:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: `localhost:5432`
- pgAdmin: `http://localhost:5050`

pgAdmin credentials:

```text
Email: admin@admin.com
Password: admin
```

PostgreSQL credentials:

```text
Database: taskdb
Username: taskuser
Password: 1234
```

### Option 2: Run API With Maven

Start PostgreSQL first:

```bash
docker compose up -d postgres pgadmin
```

Then run the API:

```bash
./mvnw spring-boot:run
```

## Configuration

The application reads database and JWT values from environment variables, with local defaults in `application.properties`.

| Variable | Default |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/taskdb` |
| `SPRING_DATASOURCE_USERNAME` | `taskuser` |
| `SPRING_DATASOURCE_PASSWORD` | `1234` |
| `JWT_SECRET` | local development secret |
| `JWT_EXPIRATION` | `3600000` |

## Authentication

Most endpoints require a JWT:

```http
Authorization: Bearer <token>
```

Public endpoints:

- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/signin`
- `GET /api/v1/users/welcome`

### Register

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hugo",
    "surname": "Lomba",
    "username": "hugo",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "hugo",
    "password": "password123"
  }'
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Store the token:

```bash
TOKEN="paste-token-here"
```

## API Examples

### Create Category

```bash
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Study",
    "description": "College and interview preparation"
  }'
```

### Create Task

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Review Spring Security",
    "description": "Study JWT filter and authentication flow",
    "completed": false,
    "categoryId": 1
  }'
```

### List Tasks

```bash
curl http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN"
```

### Search Tasks

```bash
curl "http://localhost:8080/api/v1/tasks/search?title=spring&completed=false&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### Update Task

```bash
curl -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Review Spring Security",
    "description": "Finish notes and examples",
    "completed": true,
    "categoryId": 1
  }'
```

### Delete Task

```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Main Endpoints

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/signup` | Register a user |
| `POST` | `/api/v1/auth/signin` | Login and receive JWT |

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/tasks` | List authenticated user's tasks with pagination |
| `GET` | `/api/v1/tasks/all` | List authenticated user's tasks without pagination |
| `GET` | `/api/v1/tasks/{id}` | Get one task owned by authenticated user |
| `POST` | `/api/v1/tasks` | Create task for authenticated user |
| `PUT` | `/api/v1/tasks/{id}` | Update task owned by authenticated user |
| `DELETE` | `/api/v1/tasks/{id}` | Delete task owned by authenticated user |
| `GET` | `/api/v1/tasks/search` | Search tasks by title/status with pagination |
| `GET` | `/api/v1/tasks/completed` | Filter tasks by completion status |

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/categories` | List categories |
| `GET` | `/api/v1/categories/{id}` | Get category by id |
| `POST` | `/api/v1/categories` | Create category |

## Error Handling

Errors are returned in a consistent JSON format:

```json
{
  "timestamp": "2026-06-21T13:00:00",
  "message": "Task not found with id: 99",
  "path": "/api/v1/tasks/99"
}
```

Common responses:

| Status | Scenario |
|--------|----------|
| `400` | Validation error |
| `401` | Invalid login or missing/invalid token |
| `404` | Task, user, or category not found |
| `409` | Username already exists |

## Tests

Run all tests:

```bash
./mvnw test
```

Current test coverage includes:

- Task ownership rules
- Task creation, lookup, and deletion service behavior
- User registration and duplicate username handling
- Password encoding during signup
- Category creation and not-found behavior
- Authentication controller login/signup behavior

## Database Migrations

Flyway migrations live in:

```text
src/main/resources/db/migration
```

Existing migrations:

- `V1__init.sql`: creates users, categories, and tasks
- `V2__add_password_to_users.sql`: adds password support to users

## Project Status

This is a backend portfolio project aimed at junior, internship, and graduate software engineering roles. It demonstrates core backend skills expected in a Java/Spring role: REST APIs, authentication, persistence, Docker, migrations, validation, error handling, and automated tests.
