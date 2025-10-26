# Social Skills Demo - 3 Spring Boot Services

Services:
- auth-user-service: Spring Boot + PostgreSQL (Auth + User)
- post-feed-service: Spring Boot + MongoDB (Post / Feed)
- media-service: Spring Boot + MongoDB (Media metadata + file upload local)

Quick start (docker):
1. docker-compose up --build
2. Auth service: http://localhost:8081
   Post service: http://localhost:8082
   Media service: http://localhost:8083

Notes:
- Replace JWT secret and DB passwords in production.
- For media storage in production, use S3/MinIO instead of local folder.
