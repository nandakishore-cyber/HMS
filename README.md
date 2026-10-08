# HMS

The `spring` folder contains the Spring Boot backend for the HMS project.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- PostgreSQL

## Run locally

Create a PostgreSQL database named `hms`, then start the application:

```bash
cd spring
mvn spring-boot:run
```

The default database settings target `localhost:5432` with the `postgres` user
and password. Override them with environment variables when needed:

```bash
DB_URL=jdbc:postgresql://localhost:5432/hms \
DB_USERNAME=postgres \
DB_PASSWORD=secret \
mvn spring-boot:run
```

The backend uses Spring Web, Spring Data JPA, Hibernate, Lombok, and the
PostgreSQL JDBC driver.
