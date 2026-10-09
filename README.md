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

## Patient management

Patient management is available under `/api/v1/patients`:

- `POST /api/v1/patients` registers a patient.
- `GET /api/v1/patients?query=&status=` searches and lists patients.
- `GET` and `PUT /api/v1/patients/{patientId}` view and update a patient.
- `PATCH /api/v1/patients/{patientId}/status` changes patient status.
- `DELETE /api/v1/patients/{patientId}` archives a patient.
- `GET /api/v1/patients/{patientId}/appointment-history` views appointments.
- `GET /api/v1/patients/{patientId}/medical-history` views medical records.
- `GET /api/v1/patients/{patientId}/billing-history` views the patient's billing records.

The JPA schema creates `patients`, `medical_history`, and `billing_history`
tables in PostgreSQL. Appointment history is read from the appointment module's
`appointments` table. Basic browser pages are available at
`/patients.html`, `/patient-form.html`, and `/patient-details.html`.

## Appointment management

Appointment management is available under `/api/v1/appointments`. It supports
creation, search by patient/doctor/status/time range, rescheduling, status
transitions, cancellation, doctor schedules, and availability slots. The JPA
schema creates `appointments` and `doctor_schedules`; patient and doctor IDs
are integration identifiers so this branch remains independently deployable.
Basic API pages are available at `/appointments.html`,
`/appointment-form.html`, and `/appointment-details.html`.
