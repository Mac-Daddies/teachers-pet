package com.tiy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeachersPetApplicationTests {
	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	StudentRepository studentRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testInsertTeacher() {
		Teacher testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
		teacherRepository.save(testTeacher);

		Teacher retrievedTeacher = teacherRepository.findOne(testTeacher.getId());
		assertNotNull(retrievedTeacher);

		teacherRepository.delete(testTeacher);
		retrievedTeacher = teacherRepository.findOne(testTeacher.getId());
		assertNull(retrievedTeacher);
	}

	@Test
	public void testInsertCourse() {
		Teacher testTeacher = null;
		Course testCourse = null;
		Course retrievedCourse = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			retrievedCourse = courseRepository.findOne(testCourse.getId());
			assertNotNull(retrievedCourse);
			assertEquals(testTeacher.getEmail(), retrievedCourse.getTeacher().getEmail());

		} finally {
			if (testCourse != null) {
				courseRepository.delete(testCourse);
				retrievedCourse = courseRepository.findOne(testCourse.getId());
				assertNull(retrievedCourse);
			}
			if (testTeacher != null) {
				teacherRepository.delete(testTeacher);
			}
		}

	}

	@Test
	public void testInsertStudent() {
		Student testStudent = null;
		Teacher testTeacher = null;
		Course testCourse = null;
		Student retrievedStudent = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email", testCourse);
			studentRepository.save(testStudent);

			retrievedStudent = studentRepository.findOne(testStudent.getId());
			assertNotNull(retrievedStudent);
			assertEquals(testStudent.getParentEmail(), retrievedStudent.getParentEmail());

		} finally {
			if (testStudent != null) {
				studentRepository.delete(testStudent);
				retrievedStudent = studentRepository.findOne(testStudent.getId());
				assertNull(retrievedStudent);
			}
			if (testCourse != null) {
				courseRepository.delete(testCourse);
			}
			if (testTeacher != null) {
				teacherRepository.delete(testTeacher);
			}
		}
	}

	@Test
	public void testInsertAssignment() {
		Student testStudent = null;
		Teacher testTeacher = null;
		Course testCourse = null;
		Student retrievedStudent = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email", testCourse);
			studentRepository.save(testStudent);

			retrievedStudent = studentRepository.findOne(testStudent.getId());
			assertNotNull(retrievedStudent);
			assertEquals(testStudent.getParentEmail(), retrievedStudent.getParentEmail());

		} finally {
			if (testStudent != null) {
				studentRepository.delete(testStudent);
				retrievedStudent = studentRepository.findOne(testStudent.getId());
				assertNull(retrievedStudent);
			}
			if (testCourse != null) {
				courseRepository.delete(testCourse);
			}
			if (testTeacher != null) {
				teacherRepository.delete(testTeacher);
			}
		}
	}


}
