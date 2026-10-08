package com.example.hms.patientManagement.dto;

import com.example.hms.patientManagement.entity.*;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID patientId, String fullName, LocalDate dateOfBirth, int age, String gender,
        String bloodGroup, String phone, String email, String address, String emergencyContact,
        LocalDate registrationDate, PatientStatus status
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getFullName(), patient.getDateOfBirth(),
                java.time.Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears(),
                patient.getGender(), patient.getBloodGroup(), patient.getPhone(), patient.getEmail(),
                patient.getAddress(), patient.getEmergencyContact(), patient.getRegistrationDate(),
                patient.getStatus());
    }
}
