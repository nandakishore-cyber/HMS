package com.example.hms.userRoleManagement.dto.user;

import com.example.hms.userRoleManagement.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String fullName, String email, String phone, boolean active,
                           Instant createdAt, List<String> roles) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.isActive(), user.getCreatedAt(),
                user.getUserRoles().stream().map(assignment -> assignment.getRole().getName().name()).sorted().toList());
    }
}
