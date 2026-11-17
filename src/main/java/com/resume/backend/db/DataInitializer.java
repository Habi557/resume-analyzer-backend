package com.resume.backend.db;

import com.resume.backend.entity.Role;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.repository.RoleRepository;
import com.resume.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Qualifier("vectorJdbcTemplate")
    private final JdbcTemplate vectorJdbcTemplate;

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
        // Initialize vector database
        initializeVectorDatabase();

    }

    private void initializeVectorDatabase() {
        // Enable pgvector extension
        log.info("Enabling pgvector extension");
        vectorJdbcTemplate.execute(
                "CREATE EXTENSION IF NOT EXISTS vector"
        );

        // Create resume embedding table
        log.info("Creating resume embedding table");
        vectorJdbcTemplate.execute("""
    CREATE TABLE IF NOT EXISTS resume_embedding (
        id BIGSERIAL PRIMARY KEY,

        resume_id BIGINT NOT NULL,

        header JSONB,
        experience JSONB,
        education JSONB,
        projects JSONB,
        skills JSONB,
        certifications JSONB,

        embedding VECTOR(768) NOT NULL,

        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        CONSTRAINT uk_resume_embedding_resume
            UNIQUE (resume_id)
    )
    """);
        log.info("Resume embedding table created");
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
    }
}
