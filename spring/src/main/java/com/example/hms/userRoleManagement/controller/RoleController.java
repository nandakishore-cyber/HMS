package com.example.hms.userRoleManagement.controller;

import com.example.hms.userRoleManagement.dto.role.*;
import com.example.hms.userRoleManagement.dto.user.UserResponse;
import com.example.hms.userRoleManagement.service.RoleService;
import com.example.hms.userRoleManagement.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @GetMapping
    public List<RoleResponse> list() {
        return roleService.list();
    }

    @GetMapping("/{roleId}")
    public RoleResponse get(@PathVariable UUID roleId) {
        return roleService.get(roleId);
    }

    @PutMapping("/{roleId}")
    public RoleResponse update(@PathVariable UUID roleId, @Valid @RequestBody RoleRequest request) {
        return roleService.update(roleId, request);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID roleId) {
        roleService.delete(roleId);
    }

    @GetMapping("/{roleId}/users")
    public List<UserResponse> users(@PathVariable UUID roleId) {
        return userRoleService.users(roleId);
    }
}
