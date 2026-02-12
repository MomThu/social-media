# Authentication Service Features - Complete Implementation

## Features Implemented

### 1. **Email Verification** ✅
- Automatic email verification on user registration
- Token-based verification with expiry (24 hours default)
- Prevents login until email is verified
- Resend verification email capability
- Welcome email after verification

**Models:**
- `EmailVerificationToken`: Stores verification tokens with expiry

**Services:**
- `EmailVerificationService`: Handles token generation and email verification

**API Endpoints:**
```
POST /api/auth/email/verify
Content-Type: application/json
{
  "token": "uuid-token-here"
}

POST /api/auth/email/resend-verification
Content-Type: application/json
{
  "email": "user@example.com"
}
```

---

### 2. **Password Reset** ✅
- Secure password reset flow
- Time-limited reset tokens (1 hour default)
- One-time use tokens
- Email with reset link
- Password validation and confirmation

**Models:**
- `PasswordResetToken`: Stores reset tokens with expiry and usage status

**Services:**
- `PasswordResetService`: Manages password reset flow
- `EmailService`: Sends reset emails

**API Endpoints:**
```
POST /api/auth/password-reset/request
Content-Type: application/json
{
  "email": "user@example.com"
}

POST /api/auth/password-reset/reset
Content-Type: application/json
{
  "token": "uuid-token-here",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

---

### 3. **OAuth2 Integration** ✅
Supports Google and Facebook authentication

#### Google OAuth2
1. **Setup:**
   - Create OAuth2 credentials in Google Cloud Console
   - Add authorized redirect URIs: `http://localhost:8081/login/oauth2/code/google`

2. **Configuration:**
   ```yaml
   spring.security.oauth2.client.registration.google.clientId: YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.google.clientSecret: YOUR_CLIENT_SECRET
   ```

3. **Flow:**
   - User redirected to Google login
   - Google returns authorization code
   - Server exchanges code for access token
   - User info retrieved and account created/updated
   - JWT token returned for API access

**API Endpoint:**
```
GET /login/oauth2/authorization/google
```

Response after successful auth:
```json
{
  "code": 200,
  "data": {
    "token": "jwt-token-here",
    "user": {
      "id": 1,
      "email": "user@gmail.com",
      "username": "User Name"
    }
  }
}
```

#### Facebook OAuth2
1. **Setup:**
   - Create app in Facebook Developer Console
   - Configure OAuth Redirect URIs: `http://localhost:8081/login/oauth2/code/facebook`

2. **Configuration:**
   ```yaml
   spring.security.oauth2.client.registration.facebook.clientId: YOUR_APP_ID
   spring.security.oauth2.client.registration.facebook.clientSecret: YOUR_APP_SECRET
   ```

3. **Flow:**
   - User redirected to Facebook login
   - Facebook returns authorization code
   - Server exchanges code for access token
   - User info retrieved and account created/updated
   - JWT token returned for API access

**API Endpoint:**
```
GET /login/oauth2/authorization/facebook
```

---

## Email Service Configuration

### SMTP Setup (Gmail Example)
```yaml
spring.mail.host: smtp.gmail.com
spring.mail.port: 587
spring.mail.username: your-email@gmail.com
spring.mail.password: your-app-specific-password  # Not your regular password!
spring.mail.properties.mail.smtp.auth: true
spring.mail.properties.mail.smtp.starttls.enable: true
```

**For Gmail:**
1. Enable 2-Step Verification
2. Generate App-specific password
3. Use this password in configuration

### Email Templates

**Password Reset Email:**
- Subject: "Password Reset Request"
- Contains: Reset link (valid 1 hour), warning if not requested

**Email Verification Email:**
- Subject: "Email Verification"
- Contains: Verification link (valid 24 hours), account security reminder

**Welcome Email:**
- Subject: "Welcome to Social Skills Demo!"
- Sent after email verification
- Customized with user's name

---

## Database Schema

### Tables Created
1. **app_user** (enhanced)
   - New columns: `email_verified` (boolean), `oauth_provider` (string), `oauth_id` (string), `created_at` (timestamp)

2. **password_reset_tokens**
   ```sql
   - id (Long, PK)
   - token (String, unique)
   - user_id (Long, FK)
   - expiry_date (Instant)
   - used (boolean)
   - created_at (Instant)
   ```

3. **email_verification_tokens**
   ```sql
   - id (Long, PK)
   - token (String, unique)
   - user_id (Long, FK)
   - expiry_date (Instant)
   - verified (boolean)
   - created_at (Instant)
   ```

---

## Security Features

✅ **Password Security:**
- Passwords hashed with BCrypt
- Password confirmation required for reset
- Minimum 8 characters validation

✅ **Token Security:**
- Unique UUIDs for all tokens
- Time-limited validity
- One-time use enforcement (password reset)
- Tokens invalidated on use

✅ **Email Security:**
- Email verification before login
- OAuth2 emails auto-verified
- Session-less JWT authentication

✅ **OAuth2 Security:**
- Authorization code flow (most secure)
- CSRF protection
- State parameter validation

---

## API Response Format

All endpoints follow standard `APIResponse` format:

**Success Response:**
```json
{
  "code": 200,
  "data": { ... },
  "message": "Operation successful"
}
```

**Error Response:**
```json
{
  "code": 400,
  "data": null,
  "message": "Error description"
}
```

---

## Configuration Properties Reference

```yaml
app:
  password-reset:
    token-expiry-hours: 1              # Default: 1 hour
  email-verification:
    token-expiry-hours: 24             # Default: 24 hours
  frontend:
    url: http://localhost:3000         # For email links
  jwt:
    secret: "your-secret-key"
    expiration-ms: 3600000             # 1 hour
```

---

## Testing Endpoints

### Email Verification Flow
```bash
# 1. Register user
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!"
  }'

# 2. Check email for verification token, then verify
curl -X POST http://localhost:8081/api/auth/email/verify \
  -H "Content-Type: application/json" \
  -d '{"token": "token-from-email"}'

# 3. Login (now allowed since email verified)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Password123!"
  }'
```

### Password Reset Flow
```bash
# 1. Request reset
curl -X POST http://localhost:8081/api/auth/password-reset/request \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'

# 2. Check email for reset token, then reset
curl -X POST http://localhost:8081/api/auth/password-reset/reset \
  -H "Content-Type: application/json" \
  -d '{
    "token": "token-from-email",
    "newPassword": "NewPassword123!",
    "confirmPassword": "NewPassword123!"
  }'
```

### OAuth2 Flow
```bash
# Google OAuth2
# Redirect user to: http://localhost:8081/oauth2/authorization/google
# After user grants permission, automatically redirected with JWT token

# Facebook OAuth2
# Redirect user to: http://localhost:8081/oauth2/authorization/facebook
# After user grants permission, automatically redirected with JWT token
```

---

## Next Steps

1. **Configure Email Provider:**
   - Get SMTP credentials (Gmail, SendGrid, AWS SES, etc.)
   - Update `application.yml`

2. **Setup OAuth2 Applications:**
   - Create Google OAuth2 app in Google Cloud Console
   - Create Facebook app in Facebook Developer Console
   - Add credentials to `application.yml`

3. **Frontend Integration:**
   - Create password reset page at `/reset-password?token=...`
   - Create email verification page at `/verify-email?token=...`
   - Add OAuth2 login buttons
   - Store JWT token in localStorage/sessionStorage

4. **Database Migration:**
   - Hibernate will auto-create new tables (ddl-auto: update)
   - Or create migration scripts for production

---

## Dependencies Added

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## Files Created

- `PasswordResetToken.java` - Model
- `EmailVerificationToken.java` - Model
- `PasswordResetTokenRepository.java` - Repository
- `EmailVerificationTokenRepository.java` - Repository
- `EmailService.java` - Service for sending emails
- `PasswordResetService.java` - Service for password reset
- `EmailVerificationService.java` - Service for email verification
- `PasswordResetController.java` - REST endpoints
- `OAuth2Controller.java` - OAuth2 endpoints
- `OAuth2Config.java` - OAuth2 security configuration
- DTOs: `PasswordResetRequestDto`, `PasswordResetDto`, `EmailVerificationDto`

---

## Security Recommendations

1. ✅ Use environment variables for sensitive data (client IDs, secrets, SMTP passwords)
2. ✅ Always use HTTPS in production
3. ✅ Implement rate limiting on password reset and verification endpoints
4. ✅ Add CORS configuration for frontend domain
5. ✅ Monitor failed login attempts and implement account lockout
6. ✅ Regularly rotate JWT secret
7. ✅ Use refresh tokens for long-lived sessions
8. ✅ Implement email domain verification for production

