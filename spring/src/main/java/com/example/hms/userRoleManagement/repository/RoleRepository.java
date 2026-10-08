package com.example.hms.userRoleManagement.repository;

import com.example.hms.userRoleManagement.entity.Role;
import com.example.hms.userRoleManagement.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
