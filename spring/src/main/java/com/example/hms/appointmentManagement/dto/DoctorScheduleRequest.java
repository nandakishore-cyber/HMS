package com.example.hms.appointmentManagement.dto;

import jakarta.validation.constraints.*;

import java.time.*;
import java.util.UUID;

public record DoctorScheduleRequest(
        @NotNull UUID doctorId,
        @NotNull UUID departmentId,
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        boolean available
) {}
