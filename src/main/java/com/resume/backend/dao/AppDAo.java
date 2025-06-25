package com.resume.backend.dao;

import com.resume.backend.entity.Course;
import com.resume.backend.entity.Instructor;
import com.resume.backend.entity.InstructorDetails;

import java.util.List;

public interface AppDAo {
    void save(Instructor instructor);
    Instructor findInstructorById(int id);
    void deleteInstructorById(int id);
    Course findCourseById(int id);
    InstructorDetails findInstructorDetailsById(int id);
    List<Course> findCoursesByInstructorId(int id);
     Instructor findInstructorByIdJoinFetch(int id);
     void update(Instructor instructor);
     void updateCourse(Course course);
}
