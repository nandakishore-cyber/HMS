package com.example.hms.doctorManagement.dto.doctor;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record DoctorCreateRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Size(max = 160) String specialization,
        @NotBlank @Size(max = 160) String qualification,
        @NotNull UUID departmentId,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 255) String email,
        @NotNull @PositiveOrZero Integer experienceYears) {
}
