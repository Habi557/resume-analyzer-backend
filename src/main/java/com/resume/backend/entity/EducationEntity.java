package com.resume.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String institute;
    private String degree;
    private String duration;
    private String score;

    @ManyToOne
    @JoinColumn(name="resume_id")
    private Resume resume;
}

