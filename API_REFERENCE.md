# API Reference - Social Media Platform

## 🔐 Auth-User Service (Port 8081)

### Authentication Endpoints

#### POST /api/auth/register
**Đăng ký user mới**
```json
Request:
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe"
}

Response:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "123",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "createdAt": "2026-01-29T10:00:00"
  }
}
```

#### POST /api/auth/login
**Đăng nhập và nhận JWT token**
```json
Request:
{
  "username": "john_doe",
  "password": "SecurePass123!"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "type": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": "123",
      "username": "john_doe",
      "email": "john@example.com"
    }
  }
}
```

### User Management Endpoints

#### GET /api/users/me
**Lấy thông tin user hiện tại** (Requires Authentication)
```
Headers:
  Authorization: Bearer <jwt-token>

Response:
{
  "success": true,
  "data": {
    "id": "123",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "bio": "Software Developer",
    "createdAt": "2026-01-29T10:00:00",
    "updatedAt": "2026-01-29T10:00:00"
  }
}
```

#### GET /api/users/{userId}
**Lấy thông tin user theo ID**
```
Response:
{
  "success": true,
  "data": {
    "id": "123",
    "username": "john_doe",
    "fullName": "John Doe",
    "bio": "Software Developer",
    "followerCount": 150,
    "followingCount": 200
  }
}
```

---

## 📝 Post-Feed Service (Port 8082)

### Post Management Endpoints

#### POST /api/posts
**Tạo bài viết mới** (Requires Authentication)
```json
Request:
Headers:
  Authorization: Bearer <jwt-token>
  Content-Type: application/json

Body:
{
  "content": "Just deployed my new microservices app! 🚀",
  "mediaIds": ["media123", "media456"]
}

Response:
{
  "success": true,
  "data": {
    "id": "post123",
    "userId": "123",
    "content": "Just deployed my new microservices app! 🚀",
    "mediaIds": ["media123", "media456"],
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2026-01-29T10:30:00",
    "updatedAt": "2026-01-29T10:30:00"
  }
}
```

#### GET /api/posts
**Lấy danh sách bài viết (Feed)**
```
Query Parameters:
  - page: 0 (default)
  - size: 20 (default)
  - sort: createdAt,desc (default)

Response:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "post123",
        "userId": "123",
        "username": "john_doe",
        "content": "Just deployed my new microservices app! 🚀",
        "mediaIds": ["media123"],
        "likeCount": 15,
        "commentCount": 3,
        "createdAt": "2026-01-29T10:30:00"
      },
      {
        "id": "post124",
        "userId": "456",
        "username": "jane_smith",
        "content": "Learning Spring Boot microservices",
        "mediaIds": [],
        "likeCount": 8,
        "commentCount": 1,
        "createdAt": "2026-01-29T09:15:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3
  }
}
```

#### GET /api/posts/{postId}
**Lấy chi tiết một bài viết**
```
Response:
{
  "success": true,
  "data": {
    "id": "post123",
    "userId": "123",
    "username": "john_doe",
    "fullName": "John Doe",
    "content": "Just deployed my new microservices app! 🚀",
    "mediaIds": ["media123", "media456"],
    "likeCount": 15,
    "commentCount": 3,
    "isLiked": false,
    "createdAt": "2026-01-29T10:30:00",
    "updatedAt": "2026-01-29T10:30:00"
  }
}
```

#### PUT /api/posts/{postId}
**Cập nhật bài viết** (Requires Authentication & Ownership)
```json
Request:
Headers:
  Authorization: Bearer <jwt-token>
  Content-Type: application/json

Body:
{
  "content": "Updated content for my post"
}

Response:
{
  "success": true,
  "data": {
    "id": "post123",
    "content": "Updated content for my post",
    "updatedAt": "2026-01-29T11:00:00"
  }
}
```

#### DELETE /api/posts/{postId}
**Xóa bài viết** (Requires Authentication & Ownership)
```
Headers:
  Authorization: Bearer <jwt-token>

Response:
{
  "success": true,
  "message": "Post deleted successfully"
}
```

---

## 📷 Media Service (Port 8083)

### Media Management Endpoints

#### POST /api/media/upload
**Upload file (image/video)** (Requires Authentication)
```
Request:
Headers:
  Authorization: Bearer <jwt-token>
  Content-Type: multipart/form-data

Body:
  file: <binary file data>
  description: "My vacation photo" (optional)

Response:
{
  "success": true,
  "data": {
    "id": "media123",
    "fileName": "photo_20260129.jpg",
    "fileSize": 2048576,
    "contentType": "image/jpeg",
    "url": "http://localhost:8083/api/media/media123",
    "uploadedBy": "123",
    "description": "My vacation photo",
    "createdAt": "2026-01-29T10:30:00"
  }
}
```

#### GET /api/media/{mediaId}
**Download/View media file**
```
Response:
  Content-Type: image/jpeg (or video/mp4, etc.)
  Content-Disposition: inline; filename="photo_20260129.jpg"
  
  <binary file data>
```

#### GET /api/media/{mediaId}/metadata
**Lấy metadata của media**
```
Response:
{
  "success": true,
  "data": {
    "id": "media123",
    "fileName": "photo_20260129.jpg",
    "fileSize": 2048576,
    "contentType": "image/jpeg",
    "url": "http://localhost:8083/api/media/media123",
    "uploadedBy": "123",
    "description": "My vacation photo",
    "createdAt": "2026-01-29T10:30:00"
  }
}
```

#### DELETE /api/media/{mediaId}
**Xóa media file** (Requires Authentication & Ownership)
```
Headers:
  Authorization: Bearer <jwt-token>

Response:
{
  "success": true,
  "message": "Media deleted successfully"
}
```

---

## 🔧 Common Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      }
    ]
  },
  "timestamp": "2026-01-29T10:30:00"
}
```

### HTTP Status Codes
- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required or failed
- `403 Forbidden` - Not authorized to access resource
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## 🚨 Not Yet Implemented

### Features Chưa Có:
- ❌ Like/Unlike posts
- ❌ Comment on posts
- ❌ Follow/Unfollow users
- ❌ Personalized feed
- ❌ Search (users, posts)
- ❌ Notifications
- ❌ Password reset
- ❌ Email verification

### Technical Improvements Needed:
- ❌ Inter-service communication
- ❌ API Gateway
- ❌ Rate limiting
- ❌ Input validation (comprehensive)
- ❌ Comprehensive error handling
- ❌ Pagination for all list endpoints
- ❌ CORS configuration
- ❌ File size limits
- ❌ Image optimization
- ❌ Video transcoding

---

## 📚 Testing Examples

### Using cURL

```bash
# 1. Register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "fullName": "Test User"
  }'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!"
  }' | jq -r '.data.token')

# 3. Get my profile
curl -X GET http://localhost:8081/api/users/me \
  -H "Authorization: Bearer $TOKEN"

# 4. Create post
curl -X POST http://localhost:8082/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "My first post from API!"
  }'

# 5. Get feed
curl -X GET "http://localhost:8082/api/posts?page=0&size=10"

# 6. Upload media
curl -X POST http://localhost:8083/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/image.jpg" \
  -F "description=Test upload"
```

### Using Postman

Import the collection từ `postman_collection.json` (tạo file này trong tương lai)

---

**Last Updated**: January 29, 2026  
**API Version**: 1.0.0 (Development)
