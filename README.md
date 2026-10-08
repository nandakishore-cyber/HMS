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

## Patient management

Patient management is available under `/api/v1/patients`:

- `POST /api/v1/patients` registers a patient.
- `GET /api/v1/patients?query=&status=` searches and lists patients.
- `GET` and `PUT /api/v1/patients/{patientId}` view and update a patient.
- `PATCH /api/v1/patients/{patientId}/status` changes patient status.
- `DELETE /api/v1/patients/{patientId}` archives a patient.
- `GET /api/v1/patients/{patientId}/appointment-history` views appointments.
- `GET /api/v1/patients/{patientId}/medical-history` views medical records.
- `GET /api/v1/patients/{patientId}/billing-history` views bills from the billing module.

The JPA schema creates `patients`, `appointment_history`, and
`medical_history` tables in PostgreSQL. Basic browser pages are available at
`/patients.html`, `/patient-form.html`, and `/patient-details.html`.
