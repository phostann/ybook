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

## Coding Standards & Style Guide

### Java Code Standards
- **Java Version**: Use Java 21 features where appropriate (records, pattern matching, text blocks)
- **Code Formatting**: Follow Google Java Style Guide
- **Line Length**: Maximum 120 characters per line
- **Indentation**: 4 spaces (no tabs)
- **Encoding**: UTF-8 for all source files

### Naming Conventions
- **Classes**: PascalCase (UserEntity, NoteService, UserController)
- **Methods/Variables**: camelCase (getUserById, noteTitle, isActive)
- **Constants**: UPPER_SNAKE_CASE (MAX_FILE_SIZE, DEFAULT_PAGE_SIZE)
- **Packages**: lowercase with dots (com.example.ybook.service)
- **Database Tables**: snake_case with `y_` prefix (y_user, y_note, y_label)
- **Database Columns**: snake_case (user_id, created_at, note_title)

### Class Organization
1. **Static fields** (constants first, then static variables)
2. **Instance fields** (private fields first)
3. **Constructors** (primary constructor first)
4. **Static methods**
5. **Instance methods** (public before private, grouped by functionality)
6. **Inner classes** (static before non-static)

### Annotation Standards
- **Controller Layer**: `@RestController`, `@RequestMapping("/api/...")`, `@Valid` for DTOs
- **Service Layer**: `@Service` for implementations, `@Transactional` where needed
- **Repository Layer**: `@Mapper` for MyBatis interfaces
- **Entity Layer**: `@Entity`, `@Table`, appropriate JPA annotations
- **Configuration**: `@Configuration`, `@Bean` methods
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` appropriately

### Documentation Requirements
- **Public APIs**: All public methods must have Javadoc with `@param` and `@return`
- **Complex Logic**: Add inline comments for non-obvious business logic
- **Swagger**: Use `@Operation`, `@ApiResponse` annotations for REST endpoints
- **TODO/FIXME**: Use standard comment format with assignee and date

### Exception Handling Standards
- **Custom Exceptions**: Extend `BizException` for business logic errors
- **HTTP Status**: Use appropriate status codes (400 for validation, 404 for not found, 500 for server errors)
- **Error Messages**: Use clear, user-friendly error messages in Chinese/English
- **Logging**: Log exceptions at appropriate levels (ERROR for system issues, WARN for business logic issues)

### Testing Standards
- **Unit Tests**: Test business logic in service layer
- **Integration Tests**: Test complete API workflows
- **Test Naming**: `should_ReturnExpectedResult_When_GivenCondition`
- **Test Data**: Use builders or fixtures for test data creation
- **Assertions**: Use AssertJ for fluent assertions
- **Coverage**: Aim for >80% code coverage on service layer

### Performance Guidelines
- **Database Queries**: Use pagination for list queries, avoid N+1 problems
- **Caching**: Use Spring Cache annotations where appropriate
- **Async Processing**: Use `@Async` for time-consuming operations
- **Connection Management**: Use connection pooling, close resources properly
- **Memory Usage**: Avoid keeping large objects in memory, use streaming for file processing

### Security Standards
- **Input Validation**: Always validate user input using Bean Validation
- **SQL Injection**: Use parameterized queries (MyBatis Plus handles this)
- **Authentication**: Verify JWT tokens on protected endpoints
- **Authorization**: Check user permissions before accessing resources
- **Sensitive Data**: Never log passwords, tokens, or personal information
- **HTTPS**: Use HTTPS in production environments

### API Design Standards
- **REST Principles**: Use standard HTTP methods (GET, POST, PUT, DELETE)
- **URL Structure**: `/api/{resource}/{id}` pattern
- **Request/Response**: Use DTOs for requests, VOs for responses
- **Status Codes**: Return appropriate HTTP status codes
- **Pagination**: Use `page` and `size` parameters with `PageResult<T>` wrapper
- **Versioning**: Use URL versioning if API changes are not backward compatible

### Code Quality Rules
- **Method Length**: Keep methods under 30 lines when possible
- **Class Length**: Keep classes under 300 lines when possible
- **Cyclomatic Complexity**: Keep methods under complexity of 10
- **Dependencies**: Minimize dependencies between packages
- **SOLID Principles**: Follow Single Responsibility, Open/Closed, Interface Segregation principles
- **DRY Principle**: Don't Repeat Yourself - extract common code into utilities

### Git Commit Standards
- **Format**: `type(scope): description` (feat, fix, refactor, test, doc, chore)
- **Examples**: 
  - `feat(user): add user profile update functionality`
  - `fix(note): resolve note creation validation issue`
  - `refactor(auth): simplify JWT token validation logic`
- **Description**: Use imperative mood, no capital letter, no period at end

### File Organization
- **Package per Feature**: Group related classes by business feature
- **Configuration**: Keep all config classes in `config/` package
- **Utilities**: Common utilities in `common/` package
- **Resources**: SQL scripts in `resources/db/`, configs in `resources/`
- **Test Structure**: Mirror main package structure in test directory

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

### User Interaction Features
- **Like/Favorite System**: Users can like and favorite notes with real-time count updates
- **Interaction Storage**: Uses bitmap storage in `y_user_note_interaction` table for efficient multi-interaction tracking
- **Async Processing**: Like/favorite count updates processed asynchronously for better performance
- **Batch Queries**: Optimized batch querying for interaction statuses to avoid N+1 problems
- **API Endpoints**: 
  - `POST /api/notes/{id}/like` - Toggle like status
  - `POST /api/notes/{id}/favorite` - Toggle favorite status
  - `GET /api/notes/favorites` - Get user's favorited notes
  - `GET /api/notes/likes` - Get user's liked notes
  - `GET /api/notes/{id}/interaction-status` - Get interaction status
  - `POST /api/notes/batch-interaction-status` - Batch get interaction statuses

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
- 必须严格按照项目技术选型，代码规范，代码风格来完成任务