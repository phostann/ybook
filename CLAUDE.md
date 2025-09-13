# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

YBook is a Spring Boot 3.x application with Maven build system, implementing a REST API for note-taking and user management with JWT authentication. The project uses:
- Java 21 with Maven wrapper
- Spring Boot 3.5.4 with Spring Web and Spring Security
- Spring Boot Actuator for monitoring and health checks
- MyBatis Plus 3.5.13 for ORM with MySQL database
- Lombok 1.18.36 for reducing boilerplate code
- MapStruct 1.6.3 for object mapping
- JWT (jjwt 0.12.6) for authentication
- MinIO 8.5.14 for file storage
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
  - `entity/` - JPA entities extending BaseEntity (auto-generated timestamps): UserEntity, NoteEntity, LabelEntity, NoteLabelEntity
  - `dto/` - Data Transfer Objects for API requests (UserCreateDTO, UserUpdateDTO, NoteCreateDTO, NoteUpdateDTO, LabelCreateDTO, LabelUpdateDTO, LoginRequestDTO, ChangePasswordRequestDTO)  
  - `vo/` - Value Objects for API responses (UserVO, NoteVO, LabelVO, FileUploadVO, LoginResponse)
  - `controller/` - REST controllers with `/api` prefix (UserController, NoteController, LabelController, AuthController, FileUploadController)
  - `service/` and `service/impl/` - Business logic layer
  - `mapper/` - MyBatis Plus mappers with `@MapperScan` configuration
  - `converter/` - MapStruct converters for entity/DTO/VO mapping (UserConverter, NoteConverter, LabelConverter)
  - `config/` - Spring configuration classes (MybatisPlusConfig, SecurityConfig, PasswordConfig, OpenApiConfig, MinioConfig)
  - `handler/` - Global exception handler and MyBatis Plus field handlers (GlobalExceptionHandler, MetaObjectHandler)
  - `common/` - Shared utilities (ApiResult, PageResult, ApiCode)
  - `exception/` - Custom exception classes (BizException)
  - `security/` - JWT authentication and Spring Security configuration (JwtService, JwtAuthenticationFilter, CurrentUser, CurrentUserContext)

### Key Patterns
- **Layered Architecture**: Controller → Service → Mapper → Entity
- **Unified API Response**: All endpoints return `ApiResult<T>` wrapper
- **Pagination**: Uses MyBatis Plus `Page<T>` for pagination with `PageResult<T>` wrapper
- **Validation**: Jakarta Bean Validation with `@Valid` on DTOs
- **Exception Handling**: Global exception handler for consistent error responses
- **Object Mapping**: MapStruct for converting between entities, DTOs, and VOs
- **JWT Authentication**: Stateless JWT-based authentication with custom filters

### Authentication & Security
- JWT-based authentication with configurable expiration (default: 1 day)
- JWT secret is Base64-encoded and configured in application.yaml
- Public endpoints: `/api/auth/login`, `/api/auth/register`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
- All other endpoints require authentication
- Custom JWT filter processes tokens from Authorization header
- Password encoding using Spring Security Crypto
- CORS enabled with default configuration
- CSRF disabled for stateless API
- Method-level security enabled with `@EnableMethodSecurity`

### Database Configuration
- MySQL database connection configured in `application.yaml` with timezone set to `Asia/Shanghai`
- MyBatis Plus with SQL logging enabled for development
- Auto-generated timestamps via BaseEntity and MetaObjectHandler
- Table naming convention: entities use `y_` prefix (e.g., `y_user`, `y_note`, `y_label`)

### File Storage
- MinIO object storage server for file uploads
- Configuration in `application.yaml` with endpoint, access-key, secret-key, and bucket-name
- FileUploadService and FileUploadController handle file operations
- Maximum file size: 100MB, maximum request size: 500MB

## Development Notes

- Main application class: `YbookApplication` with `@MapperScan("com.example.ybook.mapper", annotationClass = org.apache.ibatis.annotations.Mapper.class)`
- All entities should extend `BaseEntity` for automatic ID generation and timestamps
- Use DTOs for input validation and VOs for response data shaping  
- MapStruct converters handle object transformations between entities, DTOs, and VOs
- Password handling via Spring Security Crypto (PasswordConfig) with `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` for password fields
- RESTful API design with standard HTTP methods and `/api` prefix
- Swagger UI available at `/swagger-ui.html` for API documentation
- Spring Boot Actuator endpoints available at `/actuator/*` for monitoring
- Authentication context available via `CurrentUser` and `CurrentUserContext` for accessing current user info
- Core domain includes Users, Notes, and Labels with many-to-many relationship between Notes and Labels
- Static resources are disabled (`add-mappings: false`)
- Notes support multiple images stored as comma-separated values in the `images` field

## Testing Notes

- Current test coverage is minimal (only context loading test exists)
- Run tests with `./mvnw test`
- Consider adding unit tests for services and integration tests for controllers
- No specific testing patterns established yet

## Database Setup
Ensure MySQL is running with database `ybook` and credentials as configured in `application.yaml`:
- Database: `ybook` 
- Username: `root`
- Password: `ybook_root_password`
- Port: 3306

## MinIO Setup
Ensure MinIO server is running for file storage operations:
- Endpoint: `http://localhost:9000`
- Access Key: `VpvLTWcT0Ozq0poYDKNg`
- Secret Key: `bO8K1dM2kJv9D3Ok133ZXLHhvh6KD4oGAlEIWKTV`
- Bucket: `files`

## Security Considerations

- **Production Setup**: Change JWT secret from the default Base64-encoded value in `application.yaml`
- **MinIO Credentials**: The current MinIO access keys are hardcoded for development - use environment variables in production
- **MySQL Timezone**: Database is configured for `Asia/Shanghai` timezone