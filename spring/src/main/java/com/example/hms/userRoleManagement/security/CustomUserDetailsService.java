package com.example.hms.userRoleManagement.security;

import com.example.hms.userRoleManagement.entity.User;
import com.example.hms.userRoleManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return toDetails(user);
    }

    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return toDetails(user);
    }

    private UserDetails toDetails(User user) {
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(user.getUserRoles().stream()
                        .map(assignment -> new SimpleGrantedAuthority("ROLE_" + assignment.getRole().getName().name()))
                        .toList())
                .build();
    }
}
