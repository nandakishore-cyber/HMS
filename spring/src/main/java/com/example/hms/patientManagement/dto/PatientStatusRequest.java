package com.example.hms.patientManagement.dto;

import com.example.hms.patientManagement.entity.PatientStatus;
import jakarta.validation.constraints.NotNull;

public record PatientStatusRequest(@NotNull PatientStatus status) {}
