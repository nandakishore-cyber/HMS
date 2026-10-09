# HMS

The `spring` folder contains the Spring Boot backend for the HMS project.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- PostgreSQL

## Run locally

Create a PostgreSQL database named `Dyn_Pri_DB`, then start the application:

```bash
cd spring
mvn spring-boot:run
```

The default database settings target `localhost:5432/Dyn_Pri_DB` with the
`postgres` user and `postgres` password. Override them with environment
variables when needed:

```bash
DB_URL=jdbc:postgresql://localhost:5432/Dyn_Pri_DB \
DB_USERNAME=postgres \
DB_PASSWORD=your-password \
mvn spring-boot:run
```

The backend uses Spring Web, Spring Data JPA, Hibernate, Lombok, and the
PostgreSQL JDBC driver.

## Appointment management

Appointment management is available under `/api/v1/appointments`. It supports
creation, search by patient/doctor/status/time range, rescheduling, status
transitions, cancellation, doctor schedules, and availability slots. The JPA
schema creates `appointments` and `doctor_schedules`. `patientId`,
`departmentId`, and `doctorId` are integration identifiers supplied by the
patient and doctor modules when the feature branches are combined.
Basic API pages are available at `/appointments.html`,
`/appointment-form.html`, and `/appointment-details.html`.
