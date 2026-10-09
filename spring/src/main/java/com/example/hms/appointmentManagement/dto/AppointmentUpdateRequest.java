package com.example.hms.appointmentManagement.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record AppointmentUpdateRequest(
        @NotNull @Future LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotBlank @Size(max = 500) String reason,
        @Size(max = 1000) String notes
) {}
