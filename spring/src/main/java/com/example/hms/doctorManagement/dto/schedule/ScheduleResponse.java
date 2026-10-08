package com.example.hms.doctorManagement.dto.schedule;

import com.example.hms.doctorManagement.entity.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleResponse(UUID id, UUID doctorId, DayOfWeek dayOfWeek,
                               LocalTime startTime, LocalTime endTime, boolean available) {
    public static ScheduleResponse from(DoctorSchedule schedule) {
        return new ScheduleResponse(schedule.getId(), schedule.getDoctor().getId(), schedule.getDayOfWeek(),
                schedule.getStartTime(), schedule.getEndTime(), schedule.isAvailable());
    }
}
