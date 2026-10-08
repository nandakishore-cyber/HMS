package com.example.hms.patientManagement.dto;

import com.example.hms.patientManagement.entity.MedicalHistory;

import java.time.LocalDate;
import java.util.UUID;

public record MedicalHistoryResponse(UUID id, LocalDate recordedDate, String diagnosis,
                                     String treatment, String notes) {
    public static MedicalHistoryResponse from(MedicalHistory history) {
        return new MedicalHistoryResponse(history.getId(), history.getRecordedDate(),
                history.getDiagnosis(), history.getTreatment(), history.getNotes());
    }
}
