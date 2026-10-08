package com.example.hms.doctorManagement.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(@NotNull DayOfWeek dayOfWeek, @NotNull LocalTime startTime,
                              @NotNull LocalTime endTime) {
}
