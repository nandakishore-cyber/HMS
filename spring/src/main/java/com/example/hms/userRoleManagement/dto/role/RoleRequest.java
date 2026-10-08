package com.example.hms.userRoleManagement.dto.role;

import com.example.hms.userRoleManagement.entity.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(@NotNull RoleName name, @NotBlank String description) {
}
