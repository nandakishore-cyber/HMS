package com.example.hms.doctorManagement.dto.schedule;

import jakarta.validation.constraints.NotNull;

public record ScheduleAvailabilityRequest(@NotNull Boolean available) {
}
