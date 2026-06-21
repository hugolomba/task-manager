# Task Manager API

A RESTful API for task management built with Spring Boot 4, featuring pagination, filtering, category support, and interactive API documentation via Swagger UI.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Data JPA** — data persistence with PostgreSQL
- **Flyway** — database migrations
- **Spring Security + JWT** — stateless authentication
- **Spring Validation** — request body validation
- **Spring Actuator** — health checks and monitoring endpoints
- **Springdoc OpenAPI (Swagger UI)** — interactive API documentation
- **Lombok** — boilerplate reduction
- **Maven** — build and dependency management

## Project Structure

```
src/main/java/com/hugo/taskmanager/
├── controller/
│   ├── TaskController.java          # Task CRUD and search endpoints
│   ├── CategoryController.java      # Category endpoints
│   ├── AuthenticationController.java # Signup and signin endpoints
│   ├── UserController.java          # User endpoints
│   ├── InfoController.java          # App info endpoint
│   └── InfoControllerAdvanced.java
├── service/
│   ├── TaskService.java
│   ├── UserService.java
│   └── CategoryService.java
├── repository/
│   ├── TaskRepository.java          # Custom JPQL queries + pagination
│   ├── UserRepository.java
│   └── CategoryRepository.java
├── entity/
│   ├── Task.java
│   ├── User.java
│   └── Category.java
├── dto/
│   ├── TaskRequest.java
│   ├── TaskResponse.java
│   ├── UserRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── CategoryRequest.java
│   └── CategoryResponse.java
├── mapper/
│   └── TaskMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java  # Centralized error handling
│   ├── TaskNotFoundException.java
│   ├── CategoryNotFoundException.java
│   ├── UserNotFoundException.java
│   └── UsernameAlreadyExistsException.java
└── config/
    └── AppProperties.java
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker and Docker Compose, if you want to run the included PostgreSQL setup

### Running the application

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Run with Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8080` by default.

PostgreSQL runs on `localhost:5432`, and pgAdmin is available at `http://localhost:5050`.

## API Documentation (Swagger UI)

Interactive API docs are available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```

## Endpoints

### Auth — `/api/v1/auth`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/auth/signup` | Register a new user |
| `POST` | `/api/v1/auth/signin` | Authenticate and return a JWT token |

Signin request:

```json
{
  "username": "hugo",
  "password": "secret"
}
```

Signin response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

All endpoints except `/api/v1/auth/**` and `/api/v1/users/welcome` require this header:

```http
Authorization: Bearer <token>
```

### Tasks — `/api/v1/tasks`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/tasks` | List all tasks (paginated) |
| `GET` | `/api/v1/tasks/all` | List all tasks (no pagination) |
| `GET` | `/api/v1/tasks/{id}` | Get task by ID |
| `POST` | `/api/v1/tasks` | Create a new task |
| `PUT` | `/api/v1/tasks/{id}` | Update a task |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task |
| `GET` | `/api/v1/tasks/search` | Search tasks with filters and pagination |
| `GET` | `/api/v1/tasks/completed/{status}` | Get tasks by completion status |

#### Pagination & Filtering (GET `/api/v1/tasks/search`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | string | — | Filter by title (case-insensitive, partial match) |
| `completed` | boolean | — | Filter by completion status |
| `page` | int | `0` | Page number |
| `size` | int | `10` | Page size |
| `sortBy` | string | `createdAt` | Sort field |
| `sortDir` | string | `DESC` | Sort direction (`ASC` or `DESC`) |

### Categories — `/api/v1/categories`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/categories` | List all categories |
| `GET` | `/api/v1/categories/{id}` | Get category by ID |
| `POST` | `/api/v1/categories` | Create a new category |

Category request:

```json
{
  "name": "Work",
  "description": "Tasks related to work"
}
```

### Info — `/api/v1/info`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/info` | Get application info (name, version, config) |

### Actuator — `/actuator`

Exposed endpoints: `health`, `info`, `metrics`, `env`, `logger`, `beans`, `mappings`

## Database

The application uses **PostgreSQL** by default.

The included `docker-compose.yml` creates:

- database: `taskdb`
- username: `taskuser`
- password: `1234`
- PostgreSQL port: `5432`
- pgAdmin URL: `http://localhost:5050`

| Property | Value |
|----------|-------|
| JDBC URL | `jdbc:postgresql://localhost:5432/taskdb` |
| Username | `taskuser` |
| Password | `1234` |

Schema changes are managed with Flyway migrations in `src/main/resources/db/migration`.

## Error Handling

All errors are handled globally via `GlobalExceptionHandler` and return a consistent JSON structure:

```json
{
  "timestamp": "2026-06-06T14:00:00",
  "message": "Task not found with id: 99",
  "path": "/api/v1/tasks/99"
}
```

Validation errors return a list of field-level messages with HTTP `400 Bad Request`.

## Configuration

Key properties in `application.properties`:

```properties
app.name=Task Manager
app.version=1.0.0
app.max-tasks-per-page=10
```

## Author

**Hugo Lomba**
