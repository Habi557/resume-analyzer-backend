package com.resume.backend.helperclass;

import com.resume.backend.entity.Resume;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ResumeSpecification {

    public static Specification<Resume> hasSkill(String skillName) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> skillJoin = root.join("skills", JoinType.INNER);
            return cb.like(cb.lower(skillJoin.get("name")), "%" + skillName.trim().toLowerCase() + "%");
        };
    }
public static Specification<Resume> hasAnyAlias(List<String> aliases) {
    return (root, query, cb) -> {
        Join<Object, Object> skillJoin = root.join("skills", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        for (String alias : aliases) {
            predicates.add(cb.equal(cb.lower(skillJoin.get("name")), alias.toLowerCase()));
        }
        return cb.or(predicates.toArray(new Predicate[0]));
    };
}
//public static Specification<Resume> hasSkill(String skillName) {
//    return (root, query, cb) -> {
//        Join<Object, Object> skillJoin = root.join("skills", JoinType.INNER);
//        return cb.like(cb.lower(skillJoin.get("name")), skillName.toLowerCase() + "%");
//    };
//}

    public static Specification<Resume> hasExperienceGreaterThan(Double experience) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("yearsOfExperience"), experience);
    }
}

