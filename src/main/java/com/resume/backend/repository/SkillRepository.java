package com.resume.backend.repository;

import com.resume.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("SELECT DISTINCT s.name FROM Skill s WHERE LOWER(s.name) LIKE %:query%")
    List<String> findSkillSuggestions(@Param("query") String query);
    List<Skill> findByNameContainingIgnoreCase(String skillName);
}
