package com.example.hms.userRoleManagement.service;

import com.example.hms.userRoleManagement.dto.user.UserCreateRequest;
import com.example.hms.userRoleManagement.dto.user.UserResponse;
import com.example.hms.userRoleManagement.dto.user.UserUpdateRequest;
import com.example.hms.userRoleManagement.exception.ConflictException;
import com.example.hms.userRoleManagement.exception.ResourceNotFoundException;
import com.example.hms.userRoleManagement.entity.User;
import com.example.hms.userRoleManagement.entity.Role;
import com.example.hms.userRoleManagement.entity.RoleName;
import com.example.hms.userRoleManagement.entity.UserRole;
import com.example.hms.userRoleManagement.repository.RoleRepository;
import com.example.hms.userRoleManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.count() > 0
                && !SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can create users");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("A user with this email already exists");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user = userRepository.save(user);
        if (userRepository.count() == 1) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseGet(() -> {
                Role role = new Role();
                role.setName(RoleName.ADMIN);
                role.setDescription("Hospital system administrator");
                return roleRepository.save(role);
            });
            UserRole assignment = new UserRole();
            assignment.setUser(user);
            assignment.setRole(adminRole);
            user.getUserRoles().add(assignment);
            user = userRepository.save(user);
        }
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> list(String name, Pageable pageable) {
        Page<User> users = name == null || name.isBlank()
                ? userRepository.findAll(pageable)
                : userRepository.findByFullNameContainingIgnoreCase(name, pageable);
        return users.map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public User getEntity(UUID id) {
        return userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return UserResponse.from(getEntity(id));
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = getEntity(id);
        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("A user with this email already exists");
        }
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse setActive(UUID id, boolean active) {
        User user = getEntity(id);
        user.setActive(active);
        return UserResponse.from(userRepository.save(user));
    }
}
