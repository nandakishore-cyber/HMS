package com.example.hms.userRoleManagement.repository;

import com.example.hms.userRoleManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "userRoles.role")
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "userRoles.role")
    Optional<User> findWithRolesById(UUID id);

    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    boolean existsByEmailIgnoreCase(String email);
}
