package com.example.hms.doctorManagement.dto.doctor;

import com.example.hms.doctorManagement.entity.DoctorStatus;
import jakarta.validation.constraints.NotNull;

public record DoctorStatusRequest(@NotNull DoctorStatus status) {
}
