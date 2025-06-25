package com.resume.backend;

import com.resume.backend.dao.AppDAo;
import com.resume.backend.entity.Course;
import com.resume.backend.entity.Instructor;
import com.resume.backend.entity.InstructorDetails;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ResumeAiBackendApplication {

	//	swap two numbers
	public static void main(String[] args) {
		SpringApplication.run(ResumeAiBackendApplication.class, args);
	}

	
//	@Bean
//	public CommandLineRunner commandLineRunner(AppDAo appDAo){
//		return runner->{
//            //createInstructor(appDAo);
//			//findInsturctor(appDAo);
//			//deleteInsturctorById(appDAo);
//			//findInsturctorDetailsById(appDAo);
//			//createInstructorWithCourses(appDAo);
//			//findInstructorWithCourses(appDAo);
//			//findCoursesForInstructor(appDAo);
//			//findInsturctorwithCoursesJoinFetch(appDAo);
//			//updateInstructor(appDAo);
//			//updateCourse(appDAo);
//			//deleteInsturctor(appDAo);
//
//		};
//	}
//
//	private void deleteInsturctor(AppDAo appDAo) {
//	}

	private void updateCourse(AppDAo appDAo) {
		int id=15;
		Course courseById = appDAo.findCourseById(id);
		System.out.println("Finding the course by id: "+ id);
		System.out.println(courseById);
		courseById.setTitle("Docker");
		appDAo.updateCourse(courseById);
		System.out.println("Done");
	}

	private void updateInstructor(AppDAo appDAo) {
	  int id=4;
	  // find the instructor
		System.out.println("Finding the instructor by id: "+ id);
		Instructor instructorById = appDAo.findInstructorById(id);
		// update the instructor
		System.out.println("Updating the instructor id:" + id);
		instructorById.setLastName("SHAIK");
		appDAo.update(instructorById);
	}

	private void findInsturctorwithCoursesJoinFetch(AppDAo appDAo) {
		int id=4;
		System.out.println("Finding the instructor id:"+ id);
		Instructor instructorByIdJoinFetch = appDAo.findInstructorByIdJoinFetch(id);
		System.out.println("InstructorDetails "+ instructorByIdJoinFetch);
		System.out.println("The Instructor courses: "+ instructorByIdJoinFetch.getCourses());
		System.out.println("Done");
	}

	private void findCoursesForInstructor(AppDAo appDAo) {
		int id=4;
		System.out.println("Finding the Instructor by Id: "+ id);
		Instructor instructorById = appDAo.findInstructorById(id);
		System.out.println("Instructor Details "+ instructorById);
		System.out.println("Finding the courses of Instructor");
		List<Course> coursesByInstructorId = appDAo.findCoursesByInstructorId(id);
		instructorById.setCourses(coursesByInstructorId);
		System.out.println("List of Courses"+ instructorById.getCourses());
		System.out.println("Courses "+ coursesByInstructorId);
	}

	private void findInstructorWithCourses(AppDAo appDAo) {
	 int id=4;
	 System.out.println("Finding the Instructor by Id: "+ id);
		Instructor instructorById = appDAo.findInstructorById(id);
		System.out.println("Instructor Details "+ instructorById);
		//System.out.println("The Instructor Courses "+ instructorById.getCourses());
		System.out.println("Done");
	}

	private void createInstructorWithCourses(AppDAo appDAo) {
		Instructor tempInstructor = new Instructor("Habibulla","shaik","habi@gamil.com");
		InstructorDetails tempInstructorDetails = new InstructorDetails(
				"http://www.habi.com/youtube",
				"Playing Cricket"
		);
		tempInstructor.setInstructorDetails(tempInstructorDetails);
		Course course1 = new Course("html Course");
		Course course2 = new Course("css Course");
		Course course3 = new Course("php Course");
		tempInstructor.add(course1);
		tempInstructor.add(course2);
		tempInstructor.add(course3);
		System.out.println("Saveing the Instructor :"+ tempInstructor);
		System.out.println("The courses: "+ tempInstructor.getCourses());
		appDAo.save(tempInstructor);
	}

	private void findInsturctorDetailsById(AppDAo appDAo) {
         int id=1;
		 System.out.println("Finding the InstructorDetails ById: "+ id);
		InstructorDetails instructorDetailsById = appDAo.findInstructorDetailsById(id);
		System.out.println("The Associated isnturctor  "+ instructorDetailsById.getInstructor());
	}

	private void deleteInsturctorById(AppDAo appDAo) {
		int id=4;
		System.out.println("Deleting the instructor id: "+ id);
		appDAo.deleteInstructorById(id);
		System.out.println("Deleting the Instructor is Sucessfull: "+ id);

	}

	private void findInsturctor(AppDAo appDAo) {
		int id=2;
		System.out.println("Finding the id "+ id);
		Instructor instructorById = appDAo.findInstructorById(id);
		System.out.println("TempInstructor "+ instructorById);
		System.out.println("The associated instructorDetails only: "+ instructorById.getInstructorDetails());
	}

	private void createInstructor(AppDAo appDAo){
		Instructor tempInstructor = new Instructor("Habibulla","shaik","habi1235@gamil.com");
		InstructorDetails tempInstructorDetails = new InstructorDetails(
				"http://www.habibulla.com/youtube",
				"Love to learn"
		);
		tempInstructor.setInstructorDetails(tempInstructorDetails);
		System.out.println("Saveing the insturctor Deatails"+ tempInstructor);
		appDAo.save(tempInstructor);
	}
		
	
}

