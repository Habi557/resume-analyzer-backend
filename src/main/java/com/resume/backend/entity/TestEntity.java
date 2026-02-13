package com.resume.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name= "test")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Transient
    private String dummyName;
}
