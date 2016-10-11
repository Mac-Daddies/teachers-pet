package com.tiy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

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

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	StudentAssignmentRepository studentAssignmentRepository;

	@Autowired
	StudentCourseRepository studentCourseRepository;

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
		Course retrievedCourse;

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
	public void testInsertMultipleCoursesPerTeacher() {
		Teacher testTeacher = null;
		Course testCourse = null;
		Course secondTestCourse = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			secondTestCourse = new Course("second_test_name", "second_test_subject", 12, testTeacher);
			courseRepository.save(secondTestCourse);

			ArrayList<Course> allCoursesByTeacher = courseRepository.findAllByTeacher(testTeacher);
			assertEquals(2, allCoursesByTeacher.size());
			assertEquals(testTeacher.getId(), allCoursesByTeacher.get(0).getTeacher().getId());
			assertEquals(testCourse.getId(), allCoursesByTeacher.get(0).getId());
			assertEquals(testTeacher.getId(), allCoursesByTeacher.get(1).getTeacher().getId());
			assertEquals(secondTestCourse.getId(), allCoursesByTeacher.get(1).getId());

		} finally {
			if (testCourse != null) {
				courseRepository.delete(testCourse);
			}
			if (secondTestCourse != null) {
				courseRepository.delete(secondTestCourse);
			}
			if (testTeacher != null) {
				teacherRepository.delete(testTeacher);
			}
		}

	}

	@Test
	public void testInsertStudent() {
		Student testStudent = null;
		Student retrievedStudent;

		try {
			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
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
		}
	}

	@Test
	public void testInsertStudentCourse() {
		Student testStudent = null;
		Teacher testTeacher = null;
		Course testCourse = null;
		StudentCourse testStudentCourse = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
			studentRepository.save(testStudent);

			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(testCourse);

			ArrayList<Student> allStudentsByCourse = new ArrayList<>();
			for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
				allStudentsByCourse.add(currentStudentCourse.getStudent());
			}
			assertEquals(1, allStudentsByCourse.size());
			assertEquals(testStudent.getId(), allStudentsByCourse.get(0).getId());

		} finally {
			if (testStudentCourse != null) {
				studentCourseRepository.delete(testStudentCourse);
			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
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
	public void testInsertMultipleStudentsPerCourse() {
		Student testStudent = null;
		Student secondTestStudent = null;
		Teacher testTeacher = null;
		Course testCourse = null;
		StudentCourse testStudentCourse = null;
		StudentCourse secondTestStudentCourse = null;

		try {
			testTeacher = new Teacher("test_first_name311", "test_last_name1", "test_email11", "test_password1", "test_school1");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name211", "test_subject1", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name111", "test_last_name1", "test_parent_email1");
			studentRepository.save(testStudent);

			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			secondTestStudent = new Student("second_test_first_name411", "second_test_last_name1", "second_test_parent_email1");
			studentRepository.save(secondTestStudent);

			secondTestStudentCourse = new StudentCourse(secondTestStudent, testCourse);
			studentCourseRepository.save(secondTestStudentCourse);

			ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(testCourse);

			ArrayList<Student> allStudentsByCourse = new ArrayList<>();
			for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
				allStudentsByCourse.add(currentStudentCourse.getStudent());
			}
			assertEquals(2, allStudentsByCourse.size());
			assertEquals(testStudent.getId(), allStudentsByCourse.get(0).getId());
			assertEquals(secondTestStudent.getId(), allStudentsByCourse.get(1).getId());

		} finally {
			if (testStudentCourse != null) {
				studentCourseRepository.delete(testStudentCourse);
			}
			if (secondTestStudentCourse != null) {
				studentCourseRepository.delete(secondTestStudentCourse);

			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
			}
			if (secondTestStudent != null) {
				studentRepository.delete(secondTestStudent);
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
		Assignment testAssignment = null;
		Assignment retrievedAssignment;

		try {
			testAssignment = new Assignment("test_assignment_name", "test_due_date");
			assignmentRepository.save(testAssignment);

			retrievedAssignment = assignmentRepository.findOne(testAssignment.getId());
			assertNotNull(retrievedAssignment);

		} finally {
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
				retrievedAssignment = assignmentRepository.findOne(testAssignment.getId());
				assertNull(retrievedAssignment);
			}
		}
	}

	@Test
	public void testInsertStudentAssignment() {
		StudentAssignment testStudentAssignment = null;
		Assignment testAssignment = null;
		Student testStudent = null;
		Course testCourse = null;
		Teacher testTeacher = null;
		StudentAssignment retrievedStudentAssignment;
		StudentCourse testStudentCourse = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
			studentRepository.save(testStudent);

			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			testAssignment = new Assignment("test_assignment_name", "test_due_date");
			assignmentRepository.save(testAssignment);

			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, 90);
			studentAssignmentRepository.save(testStudentAssignment);

			retrievedStudentAssignment = studentAssignmentRepository.findOne(testStudentAssignment.getId());
			assertNotNull(retrievedStudentAssignment);
			assertEquals(testAssignment.getName(), retrievedStudentAssignment.getAssignment().getName());
			assertEquals(testStudent.getParentEmail(), retrievedStudentAssignment.getStudent().getParentEmail());

		} finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
				retrievedStudentAssignment = studentAssignmentRepository.findOne(testStudentAssignment.getId());
				assertNull(retrievedStudentAssignment);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (testStudentCourse != null) {
				studentCourseRepository.delete(testStudentCourse);
			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
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
	public void testAddAssignmentForMultipleStudents() {
		StudentAssignment testStudentAssignment = null;
		StudentAssignment secondTestStudentAssignment = null;
		Assignment testAssignment = null;
		Student testStudent = null;
		Student secondTestStudent = null;
		Course testCourse = null;
		Teacher testTeacher = null;
		StudentCourse testStudentCourse = null;
		StudentCourse secondTestStudentCourse = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
			studentRepository.save(testStudent);

			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			secondTestStudent = new Student("second_test_first_name", "second_test_last_name", "second_test_parent_email");
			studentRepository.save(secondTestStudent);

			secondTestStudentCourse = new StudentCourse(secondTestStudent, testCourse);
			studentCourseRepository.save(secondTestStudentCourse);

			testAssignment = new Assignment("test_assignment_name", "test_due_date");
			assignmentRepository.save(testAssignment);

			int testGrade = 90;
			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, testGrade);
			studentAssignmentRepository.save(testStudentAssignment);

			int secondTestGrade = 75;
			secondTestStudentAssignment = new StudentAssignment(secondTestStudent, testAssignment, secondTestGrade);
			studentAssignmentRepository.save(secondTestStudentAssignment);

			ArrayList<StudentAssignment> allStudentsByAssignment = studentAssignmentRepository.findAllByAssignment(testAssignment);
			assertEquals(2, allStudentsByAssignment.size());
			assertEquals(testGrade, allStudentsByAssignment.get(0).getGrade());
			assertEquals(secondTestGrade, allStudentsByAssignment.get(1).getGrade());

		} finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
			}
			if (secondTestStudentAssignment != null) {
				studentAssignmentRepository.delete(secondTestStudentAssignment);
			}
			if (testStudentCourse != null) {
				studentCourseRepository.delete(testStudentCourse);
			}
			if (secondTestStudentCourse != null) {
				studentCourseRepository.delete(secondTestStudentCourse);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
			}
			if (secondTestStudent != null) {
				studentRepository.delete(secondTestStudent);
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
	public void testGettingStudentGradesForMultipleAssignments() {
		StudentAssignment testStudentAssignment = null;
		StudentAssignment secondTestStudentAssignment = null;
		Assignment testAssignment = null;
		Assignment secondTestAssignment = null;
		Student testStudent = null;
		Course testCourse = null;
		Teacher testTeacher = null;

		try {
			testTeacher = new Teacher("test_first_name", "test_last_name", "test_email", "test_password", "test_school");
			teacherRepository.save(testTeacher);

			testCourse = new Course("test_name", "test_subject", 9, testTeacher);
			courseRepository.save(testCourse);

			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
			studentRepository.save(testStudent);

			testAssignment = new Assignment("test_assignment_name", "test_due_date");
			assignmentRepository.save(testAssignment);

			secondTestAssignment = new Assignment("second_test_assignment_name", "second_test_due_date");
			assignmentRepository.save(secondTestAssignment);

			int testGrade = 95;
			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, testGrade);
			studentAssignmentRepository.save(testStudentAssignment);

			int secondTestGrade = 100;
			secondTestStudentAssignment = new StudentAssignment(testStudent, secondTestAssignment, secondTestGrade);
			studentAssignmentRepository.save(secondTestStudentAssignment);

			ArrayList<StudentAssignment> allAssignmentsByStudent = studentAssignmentRepository.findAllByStudent(testStudent);
			assertEquals(2, allAssignmentsByStudent.size());
			assertEquals(testGrade, allAssignmentsByStudent.get(0).getGrade());
			assertEquals(secondTestGrade, allAssignmentsByStudent.get(1).getGrade());

		} finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
			}
			if (secondTestStudentAssignment != null) {
				studentAssignmentRepository.delete(secondTestStudentAssignment);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (secondTestAssignment != null) {
				assignmentRepository.delete(secondTestAssignment);
			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
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
