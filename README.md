# Task Manager API

A RESTful API for task management built with Spring Boot 4, featuring pagination, filtering, category support, and interactive API documentation via Swagger UI.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Data JPA** — data persistence with H2 in-memory database
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
│   ├── InfoController.java          # App info endpoint
│   └── InfoControllerAdvanced.java
├── service/
│   ├── TaskService.java
│   └── CategoryService.java
├── repository/
│   ├── TaskRepository.java          # Custom JPQL queries + pagination
│   └── CategoryRepository.java
├── entity/
│   ├── Task.java
│   └── Category.java
├── dto/
│   ├── TaskRequest.java
│   └── TaskResponse.java
├── mapper/
│   └── TaskMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java  # Centralized error handling
│   └── TaskNotFoundException.java
└── config/
    └── AppProperties.java
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Running the application

```bash
# Clone the repository
git clone <repository-url>
cd taskmanager

# Run with Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8080` by default.

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
| `GET` | `/api/v1/categories` | Get category by ID |
| `POST` | `/api/v1/categories` | Create a new category |

### Info — `/api/v1/info`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/info` | Get application info (name, version, config) |

### Actuator — `/actuator`

Exposed endpoints: `health`, `info`, `metrics`, `env`, `logger`, `beans`, `mappings`

## Database

The application uses an **H2 in-memory database** by default.

The H2 console is available at:

```
http://localhost:8080/h2-console
```

| Property | Value |
|----------|-------|
| JDBC URL | `jdbc:h2:mem:taskdb` |
| Username | `sa` |
| Password | `password` |

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

Key properties in `application.yml`:

```yaml
app:
  name: Task Manager
  version: 1.0.0
  max-tasks-per-page: 10
```

## Author

**Hugo Lomba**
