package com.resume.backend.repository;

import com.resume.backend.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long>, JpaSpecificationExecutor<Resume> {
    List<Resume> findByYearsOfExperience(double years);
    @Query(value = """
        SELECT  canditate_name FROM resume WHERE LOWER(canditate_name) LIKE %:query%
        UNION
        SELECT  email FROM resume WHERE LOWER(email) LIKE %:query%
        UNION
        SELECT  phone FROM resume WHERE LOWER(phone) LIKE %:query%
        LIMIT 10
        """, nativeQuery = true)
    List<String> getSuggestions(@Param("query") String query);

    @Query("SELECT DISTINCT r.id FROM Resume r JOIN r.skills s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skillName, '%'))")
    Page<Long> findResumeIdsBySkill(@Param("skillName") String skillName, Pageable pageable);
    @Query("SELECT DISTINCT r FROM Resume r JOIN FETCH r.skills s WHERE r.id IN :ids")
    List<Resume> findResumesWithSkills(@Param("ids") List<Long> ids);


//    @Query("SELECT DISTINCT r FROM Resume r JOIN FETCH r.skills s WHERE LOWER(s.name) LIKE %:skillName%")
//    Page<Resume> findResumesBySkillName(@Param("skillName") String skillName, Pageable pageable);

    //  SELECT DISTINCT skill FROM resume WHERE LOWER(skill) LIKE %:query%
    //        UNION
}
