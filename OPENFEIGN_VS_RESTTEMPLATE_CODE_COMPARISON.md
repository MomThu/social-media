# So Sánh Code: OpenFeign vs RestTemplate

## 🎯 Ví Dụ Thực Tế: Verify User & Upload Media

### Scenario
Media Service cần:
1. Verify user qua Auth Service
2. Upload file nếu user hợp lệ

---

## ❌ Implementation với RestTemplate (Cũ)

### 1. Configuration

```java
// config/RestTemplateConfig.java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configure timeouts
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        restTemplate.setRequestFactory(factory);
        
        // Configure error handler
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // Custom error handling
            }
        });
        
        return restTemplate;
    }
}
```

### 2. Auth Service Client

```java
// client/AuthServiceClient.java
@Service
public class AuthServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    public UserInfo verifyUser(String token) {
        String url = authServiceUrl + "/api/user/verify";
        
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            // Make HTTP call
            ResponseEntity<APIResponse<UserInfo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<APIResponse<UserInfo>>() {}
            );
            
            // Check status
            if (response.getStatusCode() == HttpStatus.OK) {
                APIResponse<UserInfo> body = response.getBody();
                if (body != null && body.getData() != null) {
                    return body.getData();
                }
            }
            
            throw new RuntimeException("Failed to verify user");
            
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new UnauthorizedException("Invalid token: " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new ServiceUnavailableException("Auth service error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Auth service unavailable: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    public UserInfo getUserById(String userId) {
        String url = authServiceUrl + "/api/user/" + userId;
        
        try {
            ResponseEntity<APIResponse<UserInfo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<APIResponse<UserInfo>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getData();
            }
            
            return null;
            
        } catch (Exception e) {
            // Log and return null or throw
            return null;
        }
    }
}
```

### 3. Media Service

```java
// service/MediaService.java
@Service
public class MediaService {
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Value("${upload.directory}")
    private String uploadDirectory;
    
    public Media upload(String token, MultipartFile file) throws IOException {
        // Step 1: Verify user (complex call)
        UserInfo user;
        try {
            user = authServiceClient.verifyUser(token);
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException("Please login first");
        } catch (ServiceUnavailableException e) {
            throw new ServiceUnavailableException("Authentication service is down. Please try again later.");
        }
        
        // Step 2: Validate file
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new ValidationException("File too large");
        }
        
        // Step 3: Save file
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Step 4: Save metadata
        Media media = new Media();
        media.setFileName(fileName);
        media.setOriginalFileName(file.getOriginalFilename());
        media.setContentType(file.getContentType());
        media.setSize(file.getSize());
        media.setUploadedBy(user.getId());
        media.setUploadedAt(LocalDateTime.now());
        
        return mediaRepository.save(media);
    }
}
```

### 4. Controller

```java
// controller/MediaController.java
@RestController
@RequestMapping("/api/media")
public class MediaController {
    
    @Autowired
    private MediaService mediaService;
    
    @PostMapping("/upload")
    public ResponseEntity<APIResponse<Media>> upload(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            Media media = mediaService.upload(token, file);
            return ResponseEntity.ok(
                APIResponse.ok("Upload successful", media)
            );
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.error(401, e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                .body(APIResponse.error(400, e.getMessage()));
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(APIResponse.error(503, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.error(500, "Failed to upload file"));
        }
    }
}
```

**Tổng code:** ~150 lines

**Vấn đề:**
- ❌ Phải tự tạo RestTemplate bean
- ❌ Phải tự config timeout, error handler
- ❌ Client class dài dòng với error handling
- ❌ Mỗi method phải repeat try-catch logic
- ❌ Type erasure với ParameterizedTypeReference
- ❌ Controller phải handle nhiều exceptions
- ❌ Khó maintain khi thêm endpoints mới

---

## ✅ Implementation với OpenFeign (Hiện Tại)

### 1. Configuration

```java
// pom.xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```yaml
# application.yml
auth:
  service:
    url: http://localhost:8081

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

### 2. Auth Client Interface

```java
// common/client/AuthClient.java
@FeignClient(name = "auth-user-service", url = "${auth.service.url}")
public interface AuthClient {
    
    @GetMapping("/api/user/verify")
    UserInfo verifyUser(@RequestHeader("Authorization") String token);
    
    @GetMapping("/api/user/{userId}")
    UserInfo getUserById(@PathVariable("userId") String userId);
}
```

### 3. Error Decoder (Optional)

```java
// config/FeignErrorDecoder.java
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 401:
                return new UnauthorizedException("Invalid token");
            case 404:
                return new ResourceNotFoundException("User not found");
            case 503:
                return new ServiceUnavailableException("Auth service unavailable");
            default:
                return new RuntimeException("Unknown error");
        }
    }
}
```

### 4. Enable Feign

```java
// MediaServiceApplication.java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.common.client")
public class MediaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaServiceApplication.class, args);
    }
}
```

### 5. Media Service

```java
// service/MediaService.java
@Service
public class MediaService {
    
    @Autowired
    private AuthClient authClient; // Just inject!
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Value("${upload.directory}")
    private String uploadDirectory;
    
    public Media upload(String token, MultipartFile file) throws IOException {
        // Step 1: Verify user (ONE LINE!)
        UserInfo user = authClient.verifyUser(token);
        // Exceptions automatically thrown by Feign error decoder
        
        // Step 2: Validate file
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ValidationException("File too large");
        }
        
        // Step 3: Save file
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Step 4: Save metadata
        Media media = new Media();
        media.setFileName(fileName);
        media.setOriginalFileName(file.getOriginalFilename());
        media.setContentType(file.getContentType());
        media.setSize(file.getSize());
        media.setUploadedBy(user.getId());
        media.setUploadedAt(LocalDateTime.now());
        
        return mediaRepository.save(media);
    }
}
```

### 6. Controller (với GlobalExceptionHandler)

```java
// controller/MediaController.java
@RestController
@RequestMapping("/api/media")
public class MediaController {
    
    @Autowired
    private MediaService mediaService;
    
    @PostMapping("/upload")
    public ResponseEntity<APIResponse<Media>> upload(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        Media media = mediaService.upload(token, file);
        return ResponseEntity.ok(APIResponse.ok("Upload successful", media));
        
        // Exceptions handled by GlobalExceptionHandler!
    }
}
```

**Tổng code:** ~80 lines

**Lợi ích:**
- ✅ Config đơn giản trong yml
- ✅ Client chỉ là interface - 5 lines!
- ✅ Error decoder tái sử dụng cho tất cả Feign clients
- ✅ Service code sạch sẽ, focus vào business logic
- ✅ Controller đơn giản, exceptions tự động handle
- ✅ Dễ thêm endpoints mới (chỉ thêm method vào interface)
- ✅ Type-safe, compile-time checking

---

## 📊 Comparison Summary

| Aspect | RestTemplate | OpenFeign |
|--------|-------------|-----------|
| **Config** | 30+ lines Java code | 5 lines YAML |
| **Client** | 60+ lines với error handling | 5 lines interface |
| **Service** | Try-catch everywhere | Clean business logic |
| **Controller** | Manual exception handling | Auto-handled |
| **Total Lines** | ~150 lines | ~80 lines |
| **Maintainability** | Medium | High |
| **Readability** | Low | High |
| **Testability** | Complex (mock RestTemplate) | Simple (mock interface) |
| **Adding New Endpoint** | 20+ lines | 1 line method |

---

## 🧪 Testing Comparison

### RestTemplate Test

```java
@Test
public void testVerifyUser_RestTemplate() {
    // Arrange
    RestTemplate restTemplate = mock(RestTemplate.class);
    ResponseEntity<APIResponse<UserInfo>> responseEntity = mock(ResponseEntity.class);
    APIResponse<UserInfo> apiResponse = new APIResponse<>();
    UserInfo userInfo = UserInfo.builder().id("123").username("john").build();
    apiResponse.setData(userInfo);
    
    when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        any(ParameterizedTypeReference.class)
    )).thenReturn(responseEntity);
    
    when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(responseEntity.getBody()).thenReturn(apiResponse);
    
    // Act
    AuthServiceClient client = new AuthServiceClient();
    ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
    UserInfo result = client.verifyUser("token");
    
    // Assert
    assertEquals("123", result.getId());
}
```

### OpenFeign Test

```java
@Test
public void testVerifyUser_Feign() {
    // Arrange
    AuthClient authClient = mock(AuthClient.class);
    UserInfo userInfo = UserInfo.builder().id("123").username("john").build();
    when(authClient.verifyUser("token")).thenReturn(userInfo);
    
    // Act
    UserInfo result = authClient.verifyUser("token");
    
    // Assert
    assertEquals("123", result.getId());
}
```

**Feign test đơn giản và rõ ràng hơn nhiều!** ✅

---

## 🎯 Kết Luận

**OpenFeign thắng áp đảo:**
- 50% less code
- Cleaner architecture
- Easier to maintain
- Better for microservices
- More production-ready features

**→ Đây là lý do project chọn OpenFeign!** 🏆
