package com.example.hms.appointmentManagement.dto;

import com.example.hms.appointmentManagement.entity.*;

import java.time.*;
import java.util.UUID;

public record AppointmentResponse(UUID id, UUID patientId, UUID departmentId, UUID doctorId,
                                  LocalDateTime startTime, LocalDateTime endTime, String reason,
                                  String notes, AppointmentStatus status) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(appointment.getId(), appointment.getPatientId(),
                appointment.getDepartmentId(), appointment.getDoctorId(), appointment.getStartTime(),
                appointment.getEndTime(), appointment.getReason(), appointment.getNotes(),
                appointment.getStatus());
    }
}
