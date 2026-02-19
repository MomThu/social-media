# Social Media Platform - Spring Boot 3 Microservices

A microservices-based social media platform with authentication, user management, post feed, and media handling.

## Services Overview

### 1. **Auth User Service** (Port: 8081)
- **Technology**: Spring Boot + PostgreSQL + JWT
- **Purpose**: Authentication, user registration, login, JWT token generation
- **Key Features**:
  - User registration & authentication
  - JWT token generation & validation
  - Security configuration
  - User profile management

### 2. **Post Feed Service** (Port: 8082)
- **Technology**: Spring Boot + MongoDB
- **Purpose**: Post creation, feed management, comments, and likes
- **Key Features**:
  - Create/update/delete posts
  - Post feed with pagination & filtering
  - Comment functionality
  - Like system
  - REST API with OpenAPI/Swagger documentation

### 3. **Media Service** (Port: 8083)
- **Technology**: Spring Boot + MongoDB
- **Purpose**: Media upload, management, and metadata storage
- **Key Features**:
  - File upload handling
  - Media metadata storage
  - Local file storage (with S3/MinIO support ready)
  - REST API for media operations

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)
```bash
# Build and run all services with databases
docker-compose up --build

# Services will be available at:
# - Auth Service:   http://localhost:8081
# - Post Service:   http://localhost:8082
# - Media Service:  http://localhost:8083
```

### Option 2: Running Locally
#### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (for auth-user-service)
- MongoDB (for post-feed-service and media-service)

#### Steps
```bash
# 1. Build all modules
mvn clean install

# 2. Run auth-user-service
cd auth-user-service
mvn spring-boot:run

# 3. Run post-feed-service (in new terminal)
cd post-feed-service
mvn spring-boot:run

# 4. Run media-service (in new terminal)
cd media-service
mvn spring-boot:run
```

## Key Technologies
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Maven
- **Databases**: PostgreSQL, MongoDB
- **Security**: JWT (JSON Web Tokens)
- **API Documentation**: OpenAPI 3.0 / Swagger UI
- **ORM**: JPA/Hibernate, Spring Data MongoDB
- **Libraries**: Lombok, MapStruct

## Configuration

### Environment Variables
Create `.env` or update `docker-compose.yml`:
```yaml
POSTGRES_DB: authdb
POSTGRES_USER: user
POSTGRES_PASSWORD: password
MONGO_INITDB_ROOT_USERNAME: root
MONGO_INITDB_ROOT_PASSWORD: password
```

### JWT Configuration
Update in `auth-user-service/src/main/resources/application.yml`:
```yaml
app:
  jwt:
    secret: "your-strong-secret-key-change-in-production"
    expiration-ms: 3600000  # 1 hour
```

## API Endpoints

### Auth Service (8081)
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Post Service (8082)
- `POST /api/posts/create` - Create post
- `POST /api/posts/findData` - Search posts (with pagination)
- `GET /api/posts/{id}` - Get post by ID
- `POST /api/posts/{id}/update` - Update post
- `POST /api/posts/{id}/delete` - Delete post
- `POST /api/posts/{id}/like` - Like post
- `POST /api/posts/{id}/comment` - Add comment

### Media Service (8083)
- `POST /api/media/upload` - Upload media
- `GET /api/media/{id}` - Get media info
- `DELETE /api/media/{id}` - Delete media

## Security Notes
**Important for Production**:
- Change JWT secret to a strong random key
- Update database passwords
- Use HTTPS/TLS for all communications
- For file storage, consider using S3 or MinIO instead of local folder
- Implement proper input validation and rate limiting
- Use environment variables for sensitive data

## Development Notes
- All services use a shared `common` module for DTOs and utilities
- MongoDB collections auto-created on first run
- PostgreSQL schema auto-updated via Hibernate (ddl-auto: update)
- Swagger UI available at `http://localhost:{port}/swagger-ui.html`

## Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
psql -h localhost -U user -d postgres

# Check if MongoDB is running
mongosh
```

### Port Already in Use
```bash
# Kill process on specific port (macOS/Linux)
lsof -ti:8081 | xargs kill -9
```

### Docker Issues
```bash
# Clean up Docker containers and volumes
docker-compose down -v
docker-compose up --build
```

## License
MIT License

## Author
MomThu - Social Media Microservices Project
