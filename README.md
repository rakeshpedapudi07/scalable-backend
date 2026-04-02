# Scalable Backend

A production-ready Spring Boot backend implementing stateless JWT authentication, role-based access control, refresh token rotation, and secure REST API design.

This project demonstrates clean architecture principles, domain-driven modular organization, and secure authentication flows suitable for modern distributed systems.

---

## Overview

This project demonstrates a scalable and secure backend architecture using Spring Boot.  
It follows industry-standard layering and stateless authentication practices suitable for modern REST APIs.

The system is designed with production-readiness in mind, including structured configuration, secure token handling, and clean dependency management.

---

## Core Features

- Stateless JWT Authentication (Access + Refresh Tokens)
- Role-Based Access Control (USER / ADMIN)
- Secure Filter Integration (OncePerRequestFilter)
- Refresh Token Rotation
- Logout with Token Invalidation
- Global Exception Handling
- DTO-based Response Structure
- OpenAPI (Swagger) API Documentation
- Environment-based Configuration
- Clean Layered Architecture

---

## System Architecture

```mermaid
flowchart TD
    A[Client Request] --> B[Security Filter]
    B --> C[JWT Validation]
    C --> D[Controller Layer]
    D --> E[Service Layer]
    E --> F[Repository Layer]
    F --> G[(Database)]

    E --> H[Token Rotation Logic]
    B --> I[Security Context]
```

---

## Request Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Filter
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant DB as Database

    U->>F: HTTP Request with JWT
    F->>F: Validate Token
    F->>C: Forward Request
    C->>S: Business Logic
    S->>R: Fetch or Save Data
    R->>DB: Execute Query
    DB-->>R: Data
    R-->>S: Response
    S-->>C: Result
    C-->>U: JSON Response
```

---

## Authentication Flow

```mermaid
sequenceDiagram
    participant U as User
    participant A as Auth Controller
    participant S as Auth Service
    participant DB as Database

    U->>A: Login Request
    A->>S: Validate Credentials
    S->>DB: Fetch User
    DB-->>S: User Data
    S-->>A: Generate Access + Refresh Token
    A-->>U: Tokens

    U->>A: Refresh Token
    A->>S: Validate Refresh Token
    S-->>A: New Access Token
    A-->>U: Updated JWT
```

---

## Design Principles

- Feature-first modular architecture  
- Clear separation of concerns  
- Stateless authentication  
- Scalable and maintainable structure  
- Production-oriented configuration  

---

## Project Structure (Visual)

```mermaid
graph TD
    A[scalable-backend]

    subgraph Core
        B[config]
        C[security]
        D[user module]
        E[common]
    end

    subgraph Security
        C --> C1[JwtService]
        C --> C2[JwtFilter]
    end

    subgraph User Module
        D --> D1[Controller]
        D --> D2[Service]
        D --> D3[Repository]
        D --> D4[Entity]
        D --> D5[DTO]
    end

    A --> B
    A --> C
    A --> D
    A --> E
```

---

## Tech Stack

- Java 21  
- Spring Boot  
- Spring Security  
- Spring Data JPA  
- PostgreSQL  
- JWT (jjwt)  
- OpenAPI (springdoc)  
- Maven  

---

## API Endpoints

| Method | Endpoint        | Description              |
|--------|----------------|--------------------------|
| POST   | /users         | Register new user        |
| POST   | /users/login   | Authenticate user        |
| POST   | /users/refresh | Refresh access token     |
| GET    | /users/me      | Get current user         |
| GET    | /users         | Get all users (Auth)     |
| GET    | /users/admin   | Admin-only endpoint      |
| POST   | /users/logout  | Logout user              |

---

## Configuration

Environment variables (recommended for production):

```env
DB_URL=jdbc:postgresql://localhost:5432/scalable_backend
DB_USER=postgres
DB_PASS=password
JWT_SECRET=your_secret_key
```

---

## Running Locally

### Using Maven Wrapper

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Or Build JAR

```bash
./mvnw clean package
java -jar target/scalable-backend-0.0.1-SNAPSHOT.jar
```

### Swagger UI

🔗 http://localhost:8080/swagger-ui/index.html

---

## Security Design

- Stateless REST architecture (no server sessions)  
- JWT access & refresh token mechanism  
- Role-based authorization (USER / ADMIN)  
- Token validation via custom filter  
- SecurityContext population per request  
- Password encryption using BCrypt  
- Endpoint-level access control  

---

## Future Enhancements

- Spring Boot Actuator monitoring  
- Distributed microservices architecture  
- API Gateway integration  
- Microservices split (Auth Service / User Service)  

---

## Author

**Rakesh Pedapudi**  
Backend Engineering · Secure API Design · Scalable Systems  

---

## License

This project is licensed under the **MIT License**.
