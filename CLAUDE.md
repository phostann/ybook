# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

YBook is a Spring Boot 3.x application with Maven build system, implementing a REST API for user management with JWT authentication. The project uses:
- Java 17 with Maven wrapper
- Spring Boot 3.5.4 with Spring Web and Spring Security
- MyBatis Plus 3.5.13 for ORM with MySQL database
- Lombok for reducing boilerplate code
- MapStruct 1.5.5 for object mapping
- JWT (jjwt 0.12.x) for authentication
- OpenAPI/Swagger UI for API documentation
- JUnit 5 for testing

## Common Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw clean package

# Run application
./mvnw spring-boot:run
```

### Development
```bash
# Skip tests during build
./mvnw clean package -DskipTests

# Run specific test class
./mvnw test -Dtest=YbookApplicationTests

# Generate sources (for MapStruct processors)
./mvnw generate-sources
```

## Architecture

### Package Structure
- `com.example.ybook`
  - `entity/` - JPA entities extending BaseEntity (auto-generated timestamps)
  - `dto/` - Data Transfer Objects for API requests (UserCreateDTO, UserUpdateDTO, LoginRequest)  
  - `vo/` - Value Objects for API responses (UserVO)
  - `controller/` - REST controllers with `/api` prefix
  - `service/` and `service/impl/` - Business logic layer
  - `mapper/` - MyBatis Plus mappers with `@MapperScan` configuration
  - `mapper/converter/` - MapStruct converters for entity/DTO/VO mapping
  - `config/` - Spring configuration classes (MybatisPlusConfig, SecurityConfig, PasswordConfig)
  - `handler/` - Global exception handler and MyBatis Plus field handlers
  - `common/` - Shared utilities (ApiResponse, PageResult, ApiCode)
  - `exception/` - Custom exception classes (BizException)
  - `security/` - JWT authentication and Spring Security configuration

### Key Patterns
- **Layered Architecture**: Controller → Service → Mapper → Entity
- **Unified API Response**: All endpoints return `ApiResponse<T>` wrapper
- **Pagination**: Uses MyBatis Plus `Page<T>` for pagination with `PageResult<T>` wrapper
- **Validation**: Jakarta Bean Validation with `@Valid` on DTOs
- **Exception Handling**: Global exception handler for consistent error responses
- **Object Mapping**: MapStruct for converting between entities, DTOs, and VOs
- **JWT Authentication**: Stateless JWT-based authentication with custom filters

### Authentication & Security
- JWT-based authentication with configurable expiration
- Public endpoints: `/api/auth/login`, Swagger UI documentation
- All other endpoints require authentication
- Custom JWT filter processes tokens from Authorization header
- Password encoding using Spring Security Crypto

### Database Configuration
- MySQL database connection configured in `application.yaml`
- MyBatis Plus with SQL logging enabled for development
- Auto-generated timestamps via BaseEntity and MetaObjectHandler

## Development Notes

- Main application class: `YbookApplication` with `@MapperScan("com.example.ybook.mapper")`
- All entities should extend `BaseEntity` for automatic ID generation and timestamps
- Use DTOs for input validation and VOs for response data shaping
- MapStruct converters handle object transformations
- Password handling via Spring Security Crypto (PasswordConfig)
- RESTful API design with standard HTTP methods and `/api` prefix
- Swagger UI available at `/swagger-ui.html` for API documentation

## Database Setup
Ensure MySQL is running with database `ybook` and credentials as configured in `application.yaml`:
- Database: `ybook` 
- Username: `root`
- Password: `ybook_root_password`
- Port: 3306