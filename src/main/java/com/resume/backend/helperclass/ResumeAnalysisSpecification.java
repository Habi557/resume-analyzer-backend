package com.resume.backend.helperclass;

import com.resume.backend.entity.ResumeAnalysisEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class ResumeAnalysisSpecification {
    @Autowired

    public static Specification<ResumeAnalysisEntity>
    matchPercentageGreaterThan(int value) {

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("matchPercentage"),
                        value
                );
    }
}
