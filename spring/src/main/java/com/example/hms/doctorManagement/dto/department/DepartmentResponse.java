package com.example.hms.doctorManagement.dto.department;

import com.example.hms.doctorManagement.entity.Department;

import java.util.UUID;

public record DepartmentResponse(UUID id, String name, String description, boolean active) {
    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName(),
                department.getDescription(), department.isActive());
    }
}
