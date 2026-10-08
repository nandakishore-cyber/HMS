package com.example.hms.doctorManagement.dto.department;

import jakarta.validation.constraints.NotNull;

public record DepartmentStatusRequest(@NotNull Boolean active) {
}
