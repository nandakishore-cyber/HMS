package com.example.hms.userRoleManagement.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Email String email,
        @Size(max = 30) String phone) {
}
