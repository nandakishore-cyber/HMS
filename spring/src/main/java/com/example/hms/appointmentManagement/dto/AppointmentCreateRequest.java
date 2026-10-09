package com.example.hms.appointmentManagement.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCreateRequest(
        @NotNull UUID patientId,
        @NotNull UUID departmentId,
        @NotNull UUID doctorId,
        @NotNull @Future LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotBlank @Size(max = 500) String reason,
        @Size(max = 1000) String notes
) {}
