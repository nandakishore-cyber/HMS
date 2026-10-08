package com.example.hms.patientManagement.dto;

import com.example.hms.patientManagement.entity.AppointmentHistory;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentHistoryResponse(UUID id, LocalDateTime appointmentDate,
                                         String doctorName, String reason, String status) {
    public static AppointmentHistoryResponse from(AppointmentHistory history) {
        return new AppointmentHistoryResponse(history.getId(), history.getAppointmentDate(),
                history.getDoctorName(), history.getReason(), history.getStatus());
    }
}
