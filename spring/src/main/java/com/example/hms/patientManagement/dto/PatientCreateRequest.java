package com.example.hms.patientManagement.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientCreateRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank @Size(max = 20) String gender,
        @Size(max = 10) String bloodGroup,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 255) String emergencyContact
) {}
