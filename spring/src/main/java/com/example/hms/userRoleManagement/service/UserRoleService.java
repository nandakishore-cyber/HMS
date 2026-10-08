package com.example.hms.userRoleManagement.service;

import com.example.hms.userRoleManagement.dto.role.RoleResponse;
import com.example.hms.userRoleManagement.dto.user.UserResponse;
import com.example.hms.userRoleManagement.entity.UserRole;
import com.example.hms.userRoleManagement.exception.ConflictException;
import com.example.hms.userRoleManagement.exception.ResourceNotFoundException;
import com.example.hms.userRoleManagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;
    private final RoleService roleService;

    @Transactional
    public void assign(UUID userId, UUID roleId) {
        if (userRoleRepository.findByUserIdAndRoleId(userId, roleId).isPresent()) {
            throw new ConflictException("Role is already assigned to this user");
        }
        UserRole assignment = new UserRole();
        assignment.setUser(userService.getEntity(userId));
        assignment.setRole(roleService.getEntity(roleId));
        userRoleRepository.save(assignment);
    }

    @Transactional
    public void remove(UUID userId, UUID roleId) {
        UserRole assignment = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role assignment not found"));
        userRoleRepository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> roles(UUID userId) {
        userService.getEntity(userId);
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRole).map(RoleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> users(UUID roleId) {
        roleService.getEntity(roleId);
        return userRoleRepository.findByRoleId(roleId).stream()
                .map(UserRole::getUser).map(UserResponse::from).toList();
    }
}
