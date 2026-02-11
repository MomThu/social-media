# PHASE 2 LÀM GÌ? - Tóm Tắt Nhanh

## 🎯 Mục Tiêu Phase 2
**Kết nối các microservices với nhau** - Cho phép Post Service và Media Service verify user thông qua Auth Service.

## ⚡ TL;DR - Những Gì Quan Trọng Nhất

### 1️⃣ Spring Cloud OpenFeign
Thêm library để services gọi nhau dễ dàng (không cần viết HTTP client thủ công).

### 2️⃣ AuthClient - Feign Interface
```java
@FeignClient(name = "auth-user-service", url = "${auth.service.url}")
public interface AuthClient {
    @GetMapping("/api/user/verify")
    UserInfo verifyUser(@RequestHeader("Authorization") String token);
}
```
**Đơn giản:** Interface này giống như một "điện thoại" để gọi Auth Service.

### 3️⃣ User Verification Endpoint
Auth Service thêm endpoint `/api/user/verify` để các service khác có thể:
- Gửi JWT token
- Nhận lại thông tin user (username, email, id)

### 4️⃣ Integration Trong Services
**Media Service example:**
```java
@PostMapping("/upload")
public ResponseEntity upload(@RequestHeader("Authorization") String token, ...) {
    // Verify user trước
    UserInfo user = authClient.verifyUser(token);
    
    // Upload file với user ID
    mediaService.upload(file, user.getId());
}
```

### 5️⃣ CORS Configuration
Thêm CORS config để frontend có thể gọi API từ browser.

## 🔄 Workflow Sau Phase 2

```
1. User login → Nhận JWT token
2. User upload file → Gửi token trong header
3. Media Service → Dùng AuthClient gọi Auth Service
4. Auth Service → Verify token, trả về user info
5. Media Service → Upload file với user ID
```

## 📊 Diagram

```
      ┌─────────┐
      │ Browser │
      └────┬────┘
           │ JWT token
    ┌──────┴──────┐
    │             │
    ▼             ▼
┌────────┐   ┌────────┐
│  Post  │   │ Media  │
│ :8082  │   │ :8083  │
└───┬────┘   └────┬───┘
    │             │
    │ AuthClient  │
    └──────┬──────┘
           ▼
      ┌────────┐
      │  Auth  │
      │ :8081  │
      └────────┘
```

## 💡 Tại Sao Cần Phase 2?

### Trước Phase 2:
- ❌ Mỗi service không biết user là ai
- ❌ Không verify authentication
- ❌ Bất kỳ ai cũng có thể upload/post

### Sau Phase 2:
- ✅ Services verify user qua Auth Service
- ✅ Chỉ authenticated users mới được phép
- ✅ Services biết user nào đang thao tác
- ✅ Có thể track owner của posts/media

## 🎓 Technical Terms

| Term | Nghĩa | Ví Dụ |
|------|-------|-------|
| **Feign Client** | Library gọi HTTP API như gọi method | `authClient.verifyUser(token)` |
| **Inter-Service** | Giao tiếp giữa các services | Media → Auth |
| **JWT Token** | Token xác thực user | `*****` |
| **CORS** | Cho phép browser gọi API | Frontend → Backend |
| **UserInfo** | Thông tin user đã verify | username, email, id |

## 📁 Files Quan Trọng

```
common/
├── client/AuthClient.java        ← Feign client để gọi Auth
├── dto/UserInfo.java             ← User info DTO
└── config/CorsConfig.java        ← CORS config

auth-user-service/
└── controller/UserController.java ← Endpoint /verify

Tất cả services:
└── Application.java               ← @EnableFeignClients
```

## 🚀 Demo Flow

```bash
# 1. Login để lấy token
curl -X POST http://localhost:8081/api/auth/login \
  -d '{"username":"john","password":"pass123"}'
# Response: {"token": "*****"}

# 2. Upload file với token
curl -X POST http://localhost:8083/api/media/upload \
  -H "Authorization: *****" \
  -F "file=@photo.jpg"

# Behind the scenes:
# → Media Service gọi authClient.verifyUser(token)
# → Auth Service verify token và return UserInfo
# → Media Service lưu file với user.getId()
```

## ✅ Checklist Phase 2

- [x] Thêm Spring Cloud OpenFeign dependencies
- [x] Tạo AuthClient interface
- [x] Tạo UserInfo DTO
- [x] Thêm /api/user/verify endpoint
- [x] Enable Feign clients trong all services
- [x] Configure auth.service.url
- [x] Implement user verification trong Media service
- [x] Add CORS configuration
- [x] Test và verify build success

## 🎉 Kết Quả

**Trước Phase 2:**
```
Services ≠ Kết nối
Không verify user ❌
```

**Sau Phase 2:**
```
Services ↔ Kết nối qua Feign
Verify user ở mọi request ✅
Services biết user là ai ✅
```

## 📚 Đọc Thêm

- **PHASE2_SUMMARY.md** - Chi tiết đầy đủ với code examples
- **IMPLEMENTATION_SUMMARY.md** - Technical implementation
- **FINAL_SUMMARY.md** - Overview Phase 1 & 2

---

**Phase 2 = Kết nối các services + User verification**  
**Status:** ✅ HOÀN THÀNH  
**Ready for:** Production deployment
