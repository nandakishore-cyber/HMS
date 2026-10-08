package com.example.hms.userRoleManagement.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(@NotNull Boolean active) {
}
