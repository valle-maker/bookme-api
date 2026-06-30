# BookMe API

REST API for a barber shop appointment booking system. Clients can book appointments with barbers, choosing from available services, with real-time conflict detection and business rule enforcement.

Built with Java 17+ and Spring Boot 4.x as a portfolio project demonstrating backend development practices.

## Tech Stack

- **Java 21** + **Spring Boot 4.0.6**
- **Spring Data JPA** + **Hibernate** (ORM and database access)
- **MySQL 8** (relational database)
- **MapStruct** (object mapping between entities and DTOs)
- **Lombok** (boilerplate reduction)
- **Bean Validation** (request validation)

## Features

- Full CRUD for users, barbers, services, and appointments
- Role-based user model (CLIENT, BARBER, ADMIN)
- Appointment scheduling with automatic end-time calculation based on service duration
- **Double-booking prevention** — validates that a barber has no overlapping appointments before confirming
- **Work hours enforcement** — appointments can only be scheduled within a barber's defined working hours
- **Cancellation policy** — appointments cannot be cancelled less than 1 hour before start time
- Soft delete pattern across all entities (deactivation instead of permanent deletion)
- Paginated responses on all list endpoints
- Centralized exception handling with consistent error response format
- Constructor-based dependency injection throughout

## Architecture

```
com.bookme.bookme_api
├── controller/       # REST controllers — request/response handling only
├── service/          # Business logic and validation rules
├── repository/       # Spring Data JPA interfaces with custom queries
├── entity/           # JPA entities mapped to MySQL tables
├── dto/              # Request and response DTOs with validation
├── mapper/           # MapStruct interfaces for entity ↔ DTO conversion
├── exception/        # Custom exceptions + global exception handler
└── enums/            # Role and Status enumerations
```

## API Endpoints

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users` | Create a new user |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users` | List all active users (paginated) |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Deactivate user |

### Barbers
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/barbers` | Create barber profile (requires existing user with BARBER role) |
| GET | `/api/v1/barbers/{id}` | Get barber by ID |
| GET | `/api/v1/barbers` | List all active barbers (paginated) |
| PUT | `/api/v1/barbers/{id}` | Update barber profile |
| DELETE | `/api/v1/barbers/{id}` | Deactivate barber |

### Services
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/services` | Create a service (e.g., haircut, beard trim) |
| GET | `/api/v1/services/{id}` | Get service by ID |
| GET | `/api/v1/services` | List all active services (paginated) |
| PUT | `/api/v1/services/{id}` | Update service |
| DELETE | `/api/v1/services/{id}` | Deactivate service |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/appointments` | Book an appointment |
| GET | `/api/v1/appointments/{id}` | Get appointment by ID |
| GET | `/api/v1/appointments/barber/{barberId}?start=...&end=...` | Get barber's appointments in date range |
| DELETE | `/api/v1/appointments/{id}` | Cancel appointment |

## Business Rules

- A barber cannot have two overlapping appointments at the same time
- Appointments must fall within the barber's defined working hours (start and end)
- Appointments cannot be cancelled less than 1 hour before the scheduled start time
- Only appointments with SCHEDULED status can be cancelled
- Users, barbers, and services use soft delete — deactivated records are preserved for referential integrity
- Deactivated entities cannot be updated or used to create new appointments
- Duplicate email registration is prevented with a 409 Conflict response

## Getting Started

### Prerequisites

- Java 21+
- MySQL 8+
- Maven (or use the included Maven wrapper)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/valle-maker/bookme-api.git
cd bookme-api
```

2. Create the MySQL database:
```sql
CREATE DATABASE bookme_db;
```

3. Create `src/main/resources/application-dev.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookme_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Roadmap

- [X] Unit and integration tests (JUnit 5 + Mockito)
- [X] Authentication and authorization (Spring Security + JWT)
- [ ] API documentation (Swagger / OpenAPI)
- [X] Docker containerization
- [ ] Cloud deployment

## Author

**Diego Valle**
Systems Engineering student at Universidad de Antioquia

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin)](https://www.linkedin.com/in/diego-valle-7781b6270)
[![GitHub](https://img.shields.io/badge/GitHub-black?logo=github)](https://github.com/valle-maker)