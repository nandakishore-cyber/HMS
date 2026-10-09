package com.example.hms.appointmentManagement.dto;

import com.example.hms.appointmentManagement.entity.DoctorSchedule;

import java.time.*;
import java.util.UUID;

public record DoctorScheduleResponse(UUID id, UUID doctorId, UUID departmentId,
                                     DayOfWeek dayOfWeek, LocalTime startTime,
                                     LocalTime endTime, boolean available) {
    public static DoctorScheduleResponse from(DoctorSchedule schedule) {
        return new DoctorScheduleResponse(schedule.getId(), schedule.getDoctorId(),
                schedule.getDepartmentId(), schedule.getDayOfWeek(), schedule.getStartTime(),
                schedule.getEndTime(), schedule.isAvailable());
    }
}
