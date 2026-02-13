package com.resume.backend.db;

import com.resume.backend.entity.Role;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.repository.RoleRepository;
import com.resume.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create roles
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole  = createRoleIfNotExists("ROLE_USER");

        // Create default admin
        if (!userRepository.existsByUsername("admin")) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("habi79972@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .provider("LOCAL")
                    .enabled(true)
                    .roles(Set.of(adminRole, userRole))
                    .build();

            userRepository.save(admin);
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
    }
}
