package com.example.hms.userRoleManagement.dto.auth;

import com.example.hms.userRoleManagement.dto.user.UserResponse;

public record LoginResponse(String accessToken, UserResponse user) {
}
