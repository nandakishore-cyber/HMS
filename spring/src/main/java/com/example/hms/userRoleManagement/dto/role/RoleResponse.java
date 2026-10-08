package com.example.hms.userRoleManagement.dto.role;

import com.example.hms.userRoleManagement.entity.Role;

import java.util.UUID;

public record RoleResponse(UUID id, String name, String description) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getId(), role.getName().name(), role.getDescription());
    }
}
