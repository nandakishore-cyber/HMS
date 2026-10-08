package com.example.hms.userRoleManagement.service;

import com.example.hms.userRoleManagement.dto.role.RoleRequest;
import com.example.hms.userRoleManagement.dto.role.RoleResponse;
import com.example.hms.userRoleManagement.entity.Role;
import com.example.hms.userRoleManagement.exception.ConflictException;
import com.example.hms.userRoleManagement.exception.ResourceNotFoundException;
import com.example.hms.userRoleManagement.repository.RoleRepository;
import com.example.hms.userRoleManagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ConflictException("Role already exists: " + request.name());
        }
        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description().trim());
        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Role getEntity(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        return RoleResponse.from(getEntity(id));
    }

    @Transactional
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = getEntity(id);
        if (!role.getName().equals(request.name()) && roleRepository.existsByName(request.name())) {
            throw new ConflictException("Role already exists: " + request.name());
        }
        role.setName(request.name());
        role.setDescription(request.description().trim());
        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional
    public void delete(UUID id) {
        Role role = getEntity(id);
        if (!userRoleRepository.findByRoleId(id).isEmpty()) {
            throw new ConflictException("Cannot delete a role assigned to users");
        }
        roleRepository.delete(role);
    }
}
