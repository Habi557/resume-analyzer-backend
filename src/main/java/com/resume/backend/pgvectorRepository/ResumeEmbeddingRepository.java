package com.resume.backend.pgvectorRepository;

import com.resume.backend.helperclass.ResumeEmbeddingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ResumeEmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(ResumeEmbeddingData resumeEmbeddingData, String vector) {

        String sql = """
            INSERT INTO resume_embedding
            (
                resume_id,
                header,
                experience,
                education,
                projects,
                skills,
                certifications,
                embedding
            )
            VALUES (
                ?,
                ?::jsonb,
                ?::jsonb,
                ?::jsonb,
                ?::jsonb,
                ?::jsonb,
                ?::jsonb,
                ?::vector
            )
            ON CONFLICT (resume_id)
            DO UPDATE SET
                header = EXCLUDED.header,
                experience = EXCLUDED.experience,
                education = EXCLUDED.education,
                projects = EXCLUDED.projects,
                skills = EXCLUDED.skills,
                certifications = EXCLUDED.certifications,
                embedding = EXCLUDED.embedding
            """;

       // String vector = toVectorString(embedding);

        jdbcTemplate.update(
                sql,
                resumeEmbeddingData.resumeId(),
                resumeEmbeddingData.headerJson(),
                resumeEmbeddingData.experienceJson(),
                resumeEmbeddingData.educationJson(),
                resumeEmbeddingData.projectsJson(),
                resumeEmbeddingData.skillsJson(),
                resumeEmbeddingData.certificationsJson(),
                vector
        );
        log.info("Resume embedding saved for resume id: {}", resumeEmbeddingData.resumeId());
    }
    public List<Long> similaritySearch(String pgVector, int limit){
        String sql = """
        SELECT resume_id
        FROM resume_embedding
        ORDER BY embedding <=> CAST(? AS vector)
        LIMIT ?
        """;
//        String sql = """
//    SELECThow
//        resume_id,
//        embedding <=> CAST(? AS vector) AS distance
//    FROM resume_embedding
//    ORDER BY distance
//    LIMIT ?
//    """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("resume_id"),
                pgVector,
                limit
        );
//         jdbcTemplate.query(
//                sql,
//                rs -> {
//                    while (rs.next()) {
//                        Long resumeId = rs.getLong("resume_id");
//                        Double distance = rs.getDouble("distance");
//
//                        System.out.println("Resume ID: " + resumeId);
//                        System.out.println("Distance: " + distance);
//                    }
//                },
//                pgVector,
//                limit
//        );
//         return List.of();
    }
}
