package com.example.hms.userRoleManagement.controller;

import com.example.hms.userRoleManagement.dto.user.*;
import com.example.hms.userRoleManagement.service.UserRoleService;
import com.example.hms.userRoleManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;
import com.example.hms.userRoleManagement.dto.role.RoleResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRoleService userRoleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> list(@RequestParam(required = false) String name, Pageable pageable) {
        return userService.list(name, pageable);
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable UUID userId) {
        return userService.get(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse update(@PathVariable UUID userId, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(userId, request);
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse status(@PathVariable UUID userId, @Valid @RequestBody UserStatusRequest request) {
        return userService.setActive(userId, request.active());
    }

    @GetMapping("/{userId}/roles")
    public List<RoleResponse> roles(@PathVariable UUID userId) {
        return userRoleService.roles(userId);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void assign(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.assign(userId, roleId);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void remove(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.remove(userId, roleId);
    }
}
