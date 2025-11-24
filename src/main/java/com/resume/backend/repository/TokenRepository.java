package com.resume.backend.repository;

import com.resume.backend.entity.Token;
import com.resume.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(Long userId);
}
