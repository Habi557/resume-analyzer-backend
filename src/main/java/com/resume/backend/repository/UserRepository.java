package com.resume.backend.repository;

import com.resume.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "SELECT * FROM users WHERE BINARY username = :username", nativeQuery = true)
    UserEntity findByUserNameCaseSensitive(String username);
}
