# Next Development Phases - Social Media Platform

## Quick Summary of Current State

### ✅ What's Working (60% Complete)
- **Auth Service**: JWT authentication, user registration/login, profile management
- **Post Service**: Basic CRUD for posts, like system, partial comment support
- **Media Service**: Basic file upload to local storage
- **Infrastructure**: Docker Compose ready, microservices architecture

### ❌ Critical Issues
1. **PostResponseDto is EMPTY** - Cannot return proper API responses
2. **Search/Filter NOT implemented** - Feed functionality missing
3. **Media Service missing GET/DELETE** - Can upload but not retrieve/delete
4. **No service integration** - Services work in isolation
5. **No validation or error handling** - Security risk
6. **No tests** - Quality risk

---

## 🎯 Immediate Next Steps (Priority Order)

### Phase 1: Fix Critical Bugs (Week 1-2) 🔥

**1. Complete Post Response DTO** (Day 1-2)
```java
// TODO: Implement PostResponseDto with:
- id, authorId, authorUsername, authorAvatar
- caption, mediaUrls, likes, comments
- createdAt, updatedAt
```

**2. Implement Search & Feed** (Day 3-5)
```java
// TODO: Complete PostServiceImpl.findData()
- Filter by authorId, dateRange, hashtags
- Sort by newest, mostLikes, trending
- Add pagination support
```

**3. Complete Media Service** (Day 6-7)
```java
// TODO: Add missing endpoints:
GET /api/media/{id}          - Get media info
GET /api/media/{id}/download - Download file
DELETE /api/media/{id}       - Delete media
```

---

### Phase 2: Add Validation & Security (Week 3)

**1. Input Validation**
```
□ Add @Valid annotations to all DTOs
□ Email format validation
□ Password strength requirements
□ File type and size validation
```

**2. Authentication Integration**
```
□ Media Service: Validate JWT before upload
□ Post Service: Get user info from Auth Service
□ Add authorization checks (owner-only operations)
```

**3. Error Handling**
```
□ GlobalExceptionHandler in each service
□ Standardized error response format
□ Proper HTTP status codes
```

---

### Phase 3: Service Integration (Week 4-5)

**1. Inter-Service Communication**
```
□ Use FeignClient or RestTemplate
□ Post Service calls Auth Service for user data
□ Media Service validates tokens with Auth Service
```

**2. API Gateway**
```
□ Setup Spring Cloud Gateway
□ Single entry point: http://localhost:8080
□ Centralized authentication
□ Rate limiting
```

---

### Phase 4: Testing & Documentation (Week 6)

**1. Testing**
```
□ Unit tests (JUnit + Mockito)
□ Integration tests
□ API tests (RestAssured)
□ Target: 80% coverage
```

**2. Documentation**
```
□ Complete OpenAPI/Swagger docs
□ Architecture diagram
□ Deployment guide
```

---

## 📋 Detailed Task Breakdown

### Post Feed Service - TODOs

**File**: `post-feed-service/src/main/java/com/example/post/service/PostServiceImpl.java`

```
Line 41:  TODO: convert Post to PostResponseDto
Line 55:  TODO: convert Post to PostResponseDto  
Line 77:  TODO: convert Post to PostResponseDto
Line 83:  TODO: implement search logic
Line 95:  TODO: Uncomment and fix comment mapping code
```

**Action Items**:
1. Create proper PostResponseDto with all fields
2. Add MapStruct mapper or manual conversion
3. Implement search logic with MongoDB queries
4. Add pagination support
5. Fix comment system

---

### Media Service - Missing Features

**Current**: Only has upload endpoint  
**Needed**: Complete CRUD operations

```java
// Add these endpoints:
@GetMapping("/{id}")
MediaResponse getMedia(@PathVariable String id)

@GetMapping("/{id}/download")
Resource downloadFile(@PathVariable String id)

@DeleteMapping("/{id}")
void deleteMedia(@PathVariable String id)

@GetMapping("/user/{userId}")
List<MediaResponse> getUserMedia(@PathVariable String userId)
```

**Add Service Layer**:
```java
// Extract business logic from controller
public class MediaService {
    public Media uploadMedia(MultipartFile file, String ownerId);
    public Media getMedia(String id);
    public Resource downloadFile(String id);
    public void deleteMedia(String id, String userId);
}
```

---

### Auth User Service - Improvements

**Current Gaps**:
- No input validation
- No token refresh
- No user search
- JWT secret hardcoded

**Action Items**:
```java
// 1. Add validation
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request)

// 2. Add refresh token
@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request)

// 3. Add user search
@GetMapping("/search")
public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String query)

// 4. Move secret to env
@Value("${app.jwt.secret}")
private String jwtSecret;
```

---

## 🔧 Technical Debt to Address

### Security
- [ ] Move JWT secret to environment variable
- [ ] Add rate limiting (e.g., 100 req/min per IP)
- [ ] Input sanitization for XSS prevention
- [ ] CORS configuration review
- [ ] SQL injection prevention checks

### Performance
- [ ] Add Redis caching for frequently accessed data
- [ ] Database indexing on commonly queried fields
- [ ] Lazy loading for large result sets
- [ ] Connection pooling optimization

### Scalability
- [ ] Replace local storage with S3/MinIO
- [ ] Add message queue (RabbitMQ/Kafka) for async operations
- [ ] Service discovery (Eureka)
- [ ] Load balancing consideration

### Observability
- [ ] Centralized logging (ELK stack)
- [ ] Distributed tracing (Zipkin/Jaeger)
- [ ] Metrics collection (Prometheus)
- [ ] Health check endpoints

---

## 📅 Suggested Timeline

```
Week 1:     Fix PostResponseDto + Search implementation
Week 2:     Complete Media Service endpoints
Week 3:     Add validation + error handling
Week 4:     Service integration (FeignClient)
Week 5:     API Gateway setup
Week 6:     Testing + Documentation
Week 7-8:   Advanced features (notifications, etc.)
Week 9-10:  Security hardening
Week 11-12: Production deployment prep
```

---

## 💡 Best Practices to Implement

### Code Quality
```
□ Use DTOs for all API requests/responses
□ Separate concerns (Controller → Service → Repository)
□ Use interfaces for services (loose coupling)
□ Add Lombok to reduce boilerplate
□ Use MapStruct for DTO mapping
```

### API Design
```
□ RESTful conventions (GET, POST, PUT, DELETE)
□ Consistent response format (use APIResponse wrapper)
□ Proper HTTP status codes (200, 201, 400, 401, 404, 500)
□ API versioning (/api/v1/posts)
□ Pagination for list endpoints
```

### Security
```
□ Never log sensitive data (passwords, tokens)
□ Use HTTPS in production
□ Validate all input
□ Sanitize output to prevent XSS
□ Use parameterized queries (JPA handles this)
```

---

## 🎓 Resources & References

### Documentation to Create
1. **API Documentation**: OpenAPI/Swagger for each service
2. **Architecture Diagram**: Show service interactions
3. **Deployment Guide**: Docker + K8s instructions
4. **Development Guide**: Setup local environment
5. **Contributing Guide**: Code standards

### Technologies to Learn/Use
- **Spring Cloud**: Gateway, Config, Feign
- **Testing**: JUnit 5, Mockito, TestContainers
- **Monitoring**: Spring Actuator, Prometheus
- **Caching**: Redis/Caffeine
- **Message Queue**: RabbitMQ/Kafka

---

## ✅ Definition of Done

Before moving to production, ensure:

- [ ] All TODOs in code are resolved
- [ ] Test coverage > 80%
- [ ] All APIs documented with Swagger
- [ ] Security audit passed
- [ ] Performance tested (load testing)
- [ ] CI/CD pipeline working
- [ ] Monitoring and alerting setup
- [ ] Backup and disaster recovery plan
- [ ] User documentation complete

---

## 📞 Questions to Clarify

1. **Target Scale**: How many users expected? (affects architecture decisions)
2. **Budget**: Cloud hosting budget? (affects S3 vs local storage decision)
3. **Timeline**: Hard deadline? (affects feature prioritization)
4. **Features**: Which features are must-have vs nice-to-have?
5. **Team Size**: Solo developer or team? (affects timeline estimates)

---

**Last Updated**: 2026-02-12  
**Status**: Analysis Complete - Ready for Phase 1 Implementation
