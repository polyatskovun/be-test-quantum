# Product API

This is a Spring Boot 3 application providing a REST API for managing products. It demonstrates a multi-layered architecture, including JPA/Hibernate, service layer, DTOs with validation, MapStruct mapping, Flyway migrations, caching, and Swagger documentation.

## Prerequisites
- Java 17 or higher
- Maven 3.8+

## Setup & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/polyatskovun/be-test-quantum.git
   cd be-test-quantum
   ```
2. Build and start the application:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
3. The app runs on port **8080** by default.

## Database & Migrations
- Uses H2 in-memory database (configured in `application.yml`).
- Flyway migrations are in `src/main/resources/db/migration/`. On startup, Flyway creates the `products` table and inserts default data.

## API Documentation (Swagger/OpenAPI)
Once running, access the interactive API docs at:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI spec: <http://localhost:8080/v3/api-docs>

## Security
- HTTP Basic authentication is enabled for all endpoints (except Swagger UI and API docs).
- Default user credentials: **admin** / **admin**

## Postman
The repository includes a Postman collection (`postman/Products.postman_collection.json`) for testing all endpoints.

## REST Endpoints
| Method | Path                                   | Description                    |
| ------ | -------------------------------------- | ------------------------------ |
| GET    | `/api/v1/products?page=&size=`         | List products (paginated)      |
| GET    | `/api/v1/products/{id}`                | Get single product by ID       |
| POST   | `/api/v1/products`                     | Create a new product           |
| PUT    | `/api/v1/products/{id}`                | Update an existing product     |
| DELETE | `/api/v1/products/{id}`                | Delete a product               |
| GET    | `/api/v1/products/category/{category}` | Find products by category      |

## General Architecture
- **Entity**: `Product` with JPA annotations and auditing for created/updated timestamps.
- **Repository**: `ProductRepository` extends `JpaRepository`.
- **Service**: `ProductService` contains business logic and pagination.
- **DTOs**: `ProductRequest`, `ProductResponse` with Bean Validation for request payloads.
- **Mapper**: MapStruct `ProductMapper` for entity/DTO conversion.
- **Controller**: `ProductController` defines CRUD endpoints and integrates validation + mapping.
- **Flyway**: Database migrations under `db/migration` to initialize schema and seed data.
- **Caching**: Enabled via Spring Cache abstraction.
- **Swagger/OpenAPI**: Auto-generated docs via SpringDoc.

---
# be-test-quantum
