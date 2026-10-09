package com.example.hms.patientManagement.dto;

import com.example.hms.appointmentManagement.entity.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentHistoryResponse(UUID id, LocalDateTime startTime, LocalDateTime endTime,
                                         UUID doctorId, String reason, String status) {
    public static AppointmentHistoryResponse from(Appointment appointment) {
        return new AppointmentHistoryResponse(appointment.getId(), appointment.getStartTime(),
                appointment.getEndTime(), appointment.getDoctorId(), appointment.getReason(),
                appointment.getStatus().name());
    }
}
