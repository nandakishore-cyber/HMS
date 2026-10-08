package com.example.hms.doctorManagement.dto.doctor;

import com.example.hms.doctorManagement.entity.Doctor;
import com.example.hms.doctorManagement.entity.DoctorStatus;

import java.util.UUID;

public record DoctorResponse(UUID id, String fullName, String specialization, String qualification,
                             UUID departmentId, String departmentName, String phone, String email,
                             Integer experienceYears, DoctorStatus status) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(doctor.getId(), doctor.getFullName(), doctor.getSpecialization(),
                doctor.getQualification(), doctor.getDepartment().getId(), doctor.getDepartment().getName(),
                doctor.getPhone(), doctor.getEmail(), doctor.getExperienceYears(), doctor.getStatus());
    }
}
