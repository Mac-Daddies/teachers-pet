package com.tiy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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

	@Autowired
	OriginalGradeRepository originalGradeRepository;

	CurveMyScores testCurver = new CurveMyScores();
	EmailCustomContent myEmailer = new EmailCustomContent();
	JSONController myJsonController = new JSONController();

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
	public void testOrderAssignmentsByDate() {
		ArrayList<Assignment> allAssignments = new ArrayList<>();
		Assignment testAssignment = new Assignment("testAssignment", "2016-08-11T04:00:00.000Z");
		allAssignments.add(testAssignment);
		Assignment secondTestAssignment = new Assignment("secondTestAssignment", "2016-09-05T04:00:00.000Z");
		allAssignments.add(secondTestAssignment);
		Assignment thirdTestAssignment = new Assignment("thirdTestAssignment", "2016-08-02T04:00:00.000Z");
		allAssignments.add(thirdTestAssignment);
		Assignment fourthTestAssignment = new Assignment("fourthTestAssignment", "2016-11-30T04:00:00.000Z");
		allAssignments.add(fourthTestAssignment);
		Assignment fifthTestAssignment = new Assignment("fifthTestAssignment", "2016-07-21T04:00:00.000Z");
		allAssignments.add(fifthTestAssignment);

		allAssignments = myJsonController.orderAssignmentsByDate(allAssignments);
		assertEquals(fifthTestAssignment.getName(), allAssignments.get(0).getName());
		assertEquals(thirdTestAssignment.getName(), allAssignments.get(1).getName());
		assertEquals(testAssignment.getName(), allAssignments.get(2).getName());
		assertEquals(secondTestAssignment.getName(), allAssignments.get(3).getName());
		assertEquals(fourthTestAssignment.getName(), allAssignments.get(4).getName());
	}

	@Test
	public void testOrderAssignmentsByDateWithSomeTheSame() {
		ArrayList<Assignment> allAssignments = new ArrayList<>();
		Assignment testAssignment = new Assignment("testAssignment", "2017-01-30T04:00:00.000Z");
		allAssignments.add(testAssignment);
		Assignment secondTestAssignment = new Assignment("secondTestAssignment", "2016-12-05T04:00:00.000Z");
		allAssignments.add(secondTestAssignment);
		Assignment thirdTestAssignment = new Assignment("thirdTestAssignment", "2017-02-19T04:00:00.000Z");
		allAssignments.add(thirdTestAssignment);
		Assignment fourthTestAssignment = new Assignment("fourthTestAssignment", "2016-12-05T04:00:00.000Z");
		allAssignments.add(fourthTestAssignment);
		Assignment fifthTestAssignment = new Assignment("fifthTestAssignment", "2016-10-09T04:00:00.000Z");
		allAssignments.add(fifthTestAssignment);
		Assignment sixthTestAssignment = new Assignment("sixthTestAssignment", "2016-12-21T04:00:00.000Z");
		allAssignments.add(sixthTestAssignment);

		allAssignments = myJsonController.orderAssignmentsByDate(allAssignments);

//		for (Assignment assignment : allAssignments) {
//			System.out.println(assignment.getName() + " " + assignment.getDueDate());
//		}

		assertEquals(fifthTestAssignment.getName(), allAssignments.get(0).getName());
		assertEquals(secondTestAssignment.getName(), allAssignments.get(1).getName());
		assertEquals(fourthTestAssignment.getName(), allAssignments.get(2).getName());
		assertEquals(sixthTestAssignment.getName(), allAssignments.get(3).getName());
		assertEquals(testAssignment.getName(), allAssignments.get(4).getName());
		assertEquals(thirdTestAssignment.getName(), allAssignments.get(5).getName());
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

	@Test
	public void testUpdateGradeAndReturnAllGradesForAssignment() {
		Student testStudent = null;
		Assignment testAssignment = null;
		StudentAssignment testStudentAssignment = null;

		try {
			testStudent = new Student("test_first_name", "test_last_name", "test_parent_email");
			studentRepository.save(testStudent);

			testAssignment = new Assignment("test_assignment_name", "test_due_date");
			assignmentRepository.save(testAssignment);

			int testGrade = 89;
			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, testGrade);
			studentAssignmentRepository.save(testStudentAssignment);

			ArrayList<StudentAssignment> studentAssmtsByStudentAndAssmt = studentAssignmentRepository.findAllByStudentAndAssignment(testStudent, testAssignment);
			assertEquals(1, studentAssmtsByStudentAndAssmt.size());
			assertEquals(testGrade, studentAssmtsByStudentAndAssmt.get(0).getGrade());

			int newTestGrade = 96;
			testStudentAssignment.setGrade(newTestGrade);

			studentAssignmentRepository.delete(studentAssmtsByStudentAndAssmt.get(0));
			studentAssmtsByStudentAndAssmt = studentAssignmentRepository.findAllByStudentAndAssignment(testStudent, testAssignment);
			assertEquals(0, studentAssmtsByStudentAndAssmt.size());

//			studentAssignmentRepository.save(testStudentAssignment);
//			studentAssmtsByStudentAndAssmt = studentAssignmentRepository.findAllByStudentAndAssignment(testStudent, testAssignment);
//			assertEquals(1, studentAssmtsByStudentAndAssmt.size());
//			assertEquals(newTestGrade, studentAssmtsByStudentAndAssmt.get(0).getGrade());

		} finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
				assertEquals(null, studentAssignmentRepository.findOne(testStudentAssignment.getId()));
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (testStudent != null) {
				studentRepository.delete(testStudent);
			}
		}
	}

	@Test
	public void testEmailOneStudentWithMissingAssignments() {
		Teacher testTeacher = null;
		Course testCourse = null;
		Student testStudent = null;
		StudentCourse testStudentCourse = null;
		Assignment testAssignment = null;
		Assignment secondTestAssignment = null;
		Assignment thirdTestAssignment = null;
		StudentAssignment testStudentAssignment = null;
		StudentAssignment secondTestStudentAssignment = null;
		StudentAssignment thirdTestStudentAssignment = null;
		try {
			testTeacher = new Teacher("teacherFirstName", "teacherLastName", "teacheremail@email.com", "test-password", "test-school");
			teacherRepository.save(testTeacher);
			testCourse = new Course("course-name", "test-course-subject", 10, testTeacher);
			courseRepository.save(testCourse);
			testStudent = new Student("firstName", "lastName", "j.tracy916@gmail.com");
			studentRepository.save(testStudent);
			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			testAssignment = new Assignment("assignment-name", "assignment-dueDate");
			assignmentRepository.save(testAssignment);
			secondTestAssignment = new Assignment("second-assignment-name", "second-assignment-dueDate");
			assignmentRepository.save(secondTestAssignment);
			thirdTestAssignment = new Assignment("third-assignment-name", "third-assignment-dueDate");
			assignmentRepository.save(thirdTestAssignment);

			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, 98);
			studentAssignmentRepository.save(testStudentAssignment);
			secondTestStudentAssignment = new StudentAssignment(testStudent, secondTestAssignment, 0);
			studentAssignmentRepository.save(secondTestStudentAssignment);
			thirdTestStudentAssignment = new StudentAssignment(testStudent, thirdTestAssignment, 0);
			studentAssignmentRepository.save(thirdTestStudentAssignment);

			ArrayList<StudentAssignment> testStudentAssignments = studentAssignmentRepository.findAllByStudent(testStudent);
			ArrayList<Integer> testGrades = new ArrayList<>();
			for (StudentAssignment currentStudentAssignment : testStudentAssignments) {
				testGrades.add(currentStudentAssignment.getGrade());
			}
			int testAverage = testCurver.getAverage(testGrades);
			StudentContainer testStudentContainer = new StudentContainer(testStudent, testStudentAssignments, testAverage);
			myEmailer.sendEmailOneStudent(testCourse, testTeacher, testStudentContainer, studentAssignmentRepository);

		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
			}
			if (secondTestStudentAssignment != null) {
				studentAssignmentRepository.delete(secondTestStudentAssignment);
			}
			if (thirdTestStudentAssignment != null) {
				studentAssignmentRepository.delete(thirdTestStudentAssignment);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (secondTestAssignment != null) {
				assignmentRepository.delete(secondTestAssignment);
			}
			if (thirdTestAssignment != null) {
				assignmentRepository.delete(thirdTestAssignment);
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
	public void testEmailOneStudentWithNoMissingAssignments() {
		Teacher testTeacher = null;
		Course testCourse = null;
		Student testStudent = null;
		StudentCourse testStudentCourse = null;
		Assignment testAssignment = null;
		Assignment secondTestAssignment = null;
		Assignment thirdTestAssignment = null;
		StudentAssignment testStudentAssignment = null;
		StudentAssignment secondTestStudentAssignment = null;
		StudentAssignment thirdTestStudentAssignment = null;
		try {
			testTeacher = new Teacher("teacherfirstName", "teacherlastName", "teacheremail@email.com", "test-password", "test-school");
			teacherRepository.save(testTeacher);
			testCourse = new Course("course-name", "test-course-subject", 10, testTeacher);
			courseRepository.save(testCourse);
			testStudent = new Student("testName", "lastName", "j.tracy916@gmail.com");
			studentRepository.save(testStudent);
			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			testAssignment = new Assignment("test-assignment-name", "test-assignment-dueDate");
			assignmentRepository.save(testAssignment);
			secondTestAssignment = new Assignment("second-test-assignment-name", "second-test-assignment-dueDate");
			assignmentRepository.save(secondTestAssignment);
			thirdTestAssignment = new Assignment("third-test-assignment-name", "third-test-assignment-dueDate");
			assignmentRepository.save(thirdTestAssignment);

			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, 98);
			studentAssignmentRepository.save(testStudentAssignment);
			secondTestStudentAssignment = new StudentAssignment(testStudent, secondTestAssignment, 90);
			studentAssignmentRepository.save(secondTestStudentAssignment);
			thirdTestStudentAssignment = new StudentAssignment(testStudent, thirdTestAssignment, 94);
			studentAssignmentRepository.save(thirdTestStudentAssignment);

			ArrayList<StudentAssignment> testStudentAssignments = studentAssignmentRepository.findAllByStudent(testStudent);
			ArrayList<Integer> testGrades = new ArrayList<>();
			for (StudentAssignment currentStudentAssignment : testStudentAssignments) {
				testGrades.add(currentStudentAssignment.getGrade());
			}
			int testAverage = testCurver.getAverage(testGrades);
			StudentContainer testStudentContainer = new StudentContainer(testStudent, testStudentAssignments, testAverage);
			myEmailer.sendEmailOneStudent(testCourse, testTeacher, testStudentContainer, studentAssignmentRepository);

		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
			}
			if (secondTestStudentAssignment != null) {
				studentAssignmentRepository.delete(secondTestStudentAssignment);
			}
			if (thirdTestStudentAssignment != null) {
				studentAssignmentRepository.delete(thirdTestStudentAssignment);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (secondTestAssignment != null) {
				assignmentRepository.delete(secondTestAssignment);
			}
			if (thirdTestAssignment != null) {
				assignmentRepository.delete(thirdTestAssignment);
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
	public void testEmailForAllZerosBothStudentsWithMissingAssignments() {
		Teacher testTeacher = null;
		Course testCourse = null;
		Student testStudent = null;
		Student secondTestStudent = null;
		StudentCourse testStudentCourse = null;
		StudentCourse secondTestStudentCourse = null;
		Assignment testAssignment = null;
		Assignment secondTestAssignment = null;
		StudentAssignment testStudentAssignment = null;
		StudentAssignment secondTestStudentAssignment = null;
		StudentAssignment thirdTestStudentAssignment = null;
		StudentAssignment fourthTestStudentAssignment = null;
		try {
			testTeacher = new Teacher("Teacher", "Teacher", "myTeacherEmail@email.com", "test-password", "test-school");
			teacherRepository.save(testTeacher);
			testCourse = new Course("course-name", "test-course-subject", 10, testTeacher);
			courseRepository.save(testCourse);
			testStudent = new Student("Bill", "Smith", "j.tracy916@gmail.com");
			studentRepository.save(testStudent);
			testStudentCourse = new StudentCourse(testStudent, testCourse);
			studentCourseRepository.save(testStudentCourse);

			testAssignment = new Assignment("test-assignment-name", "test-assignment-dueDate");
			assignmentRepository.save(testAssignment);
			secondTestAssignment = new Assignment("second-test-assignment-name", "second-test-assignment-dueDate");
			assignmentRepository.save(secondTestAssignment);

			testStudentAssignment = new StudentAssignment(testStudent, testAssignment, 0);
			studentAssignmentRepository.save(testStudentAssignment);
			secondTestStudentAssignment = new StudentAssignment(testStudent, secondTestAssignment, 70);
			studentAssignmentRepository.save(secondTestStudentAssignment);

			secondTestStudent = new Student("Bob", "Smith", "j.tracy916@gmail.com");
			studentRepository.save(secondTestStudent);
			secondTestStudentCourse = new StudentCourse(secondTestStudent, testCourse);
			studentCourseRepository.save(secondTestStudentCourse);

			thirdTestStudentAssignment = new StudentAssignment(secondTestStudent, testAssignment, 0);
			studentAssignmentRepository.save(thirdTestStudentAssignment);
			fourthTestStudentAssignment = new StudentAssignment(secondTestStudent, secondTestAssignment, 0);
			studentAssignmentRepository.save(fourthTestStudentAssignment);

			ArrayList<StudentContainer> studentContainers = new ArrayList<>();
			for (StudentCourse currentStudentCourse : studentCourseRepository.findAllByCourse(testCourse)) {
				ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudentCourse.getStudent());
				ArrayList<Integer> myGrades = new ArrayList<>();
				for (StudentAssignment currentStudentAssignment : allMyStudentAssignments) {
					myGrades.add(currentStudentAssignment.getGrade());
				}
				int average = testCurver.getAverage(myGrades);
				StudentContainer newStudentContainer = new StudentContainer(currentStudentCourse.getStudent(), allMyStudentAssignments, average);
				studentContainers.add(newStudentContainer);
			}

			myEmailer.sendEmailForAllZeros(testCourse, testTeacher, studentContainers, studentAssignmentRepository);

		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if (testStudentAssignment != null) {
				studentAssignmentRepository.delete(testStudentAssignment);
			}
			if (secondTestStudentAssignment != null) {
				studentAssignmentRepository.delete(secondTestStudentAssignment);
			}
			if (thirdTestStudentAssignment != null) {
				studentAssignmentRepository.delete(thirdTestStudentAssignment);
			}
			if (fourthTestStudentAssignment != null) {
				studentAssignmentRepository.delete(fourthTestStudentAssignment);
			}
			if (testAssignment != null) {
				assignmentRepository.delete(testAssignment);
			}
			if (secondTestAssignment != null) {
				assignmentRepository.delete(secondTestAssignment);
			}
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




	//Run this test to insert the data for our demo
	@Test
	public void insertDemoData() {
		Teacher teacher = new Teacher("Yehia", "Abdullah", "yehia.abdullah@tiy.com", "password", "THS");
		teacherRepository.save(teacher);


		Course firstPeriod = new Course("Analytic Geometry", "Math", 10, teacher);
		courseRepository.save(firstPeriod);

		Student s11 = new Student("Brenda", "Adkison", "j.tracy916@gmail.com");
		studentRepository.save(s11);
		StudentCourse sc11 = new StudentCourse(s11, firstPeriod);
		studentCourseRepository.save(sc11);
		Student s12 = new Student("Chase", "Barnett", "j.tracy916@gmail.com");
		studentRepository.save(s12);
		StudentCourse sc12 = new StudentCourse(s12, firstPeriod);
		studentCourseRepository.save(sc12);
		Student s13 = new Student("Star", "Blaylock", "j.tracy916@gmail.com");
		studentRepository.save(s13);
		StudentCourse sc13 = new StudentCourse(s13, firstPeriod);
		studentCourseRepository.save(sc13);
		Student s14 = new Student("Truett", "Bowman", "j.tracy916@gmail.com");
		studentRepository.save(s14);
		StudentCourse sc14 = new StudentCourse(s14, firstPeriod);
		studentCourseRepository.save(sc14);
		Student s15 = new Student("Cameron", "Dover", "j.tracy916@gmail.com");
		studentRepository.save(s15);
		StudentCourse sc15 = new StudentCourse(s15, firstPeriod);
		studentCourseRepository.save(sc15);
		Student s16 = new Student("Kendra", "Fowler", "j.tracy916@gmail.com");
		studentRepository.save(s16);
		StudentCourse sc16 = new StudentCourse(s16, firstPeriod);
		studentCourseRepository.save(sc16);
		Student s17 = new Student("Kayla", "Harris", "j.tracy916@gmail.com");
		studentRepository.save(s17);
		StudentCourse sc17 = new StudentCourse(s17, firstPeriod);
		studentCourseRepository.save(sc17);
		Student s18 = new Student("Jessica", "Hillman", "j.tracy916@gmail.com");
		studentRepository.save(s18);
		StudentCourse sc18 = new StudentCourse(s18, firstPeriod);
		studentCourseRepository.save(sc18);
		Student s19 = new Student("Hunter", "Ingram", "j.tracy916@gmail.com");
		studentRepository.save(s19);
		StudentCourse sc19 = new StudentCourse(s19, firstPeriod);
		studentCourseRepository.save(sc19);
		Student s110 = new Student("Jacob", "Kelley", "j.tracy916@gmail.com");
		studentRepository.save(s110);
		StudentCourse sc110 = new StudentCourse(s110, firstPeriod);
		studentCourseRepository.save(sc110);
		Student s111 = new Student("Brandon", "Kidd", "j.tracy916@gmail.com");
		studentRepository.save(s111);
		StudentCourse sc111 = new StudentCourse(s111, firstPeriod);
		studentCourseRepository.save(sc111);
		Student s112 = new Student("Shea", "Landrum", "j.tracy916@gmail.com");
		studentRepository.save(s112);
		StudentCourse sc112 = new StudentCourse(s112, firstPeriod);
		studentCourseRepository.save(sc112);
		Student s113 = new Student("Chaz", "Leathers", "j.tracy916@gmail.com");
		studentRepository.save(s113);
		StudentCourse sc113 = new StudentCourse(s113, firstPeriod);
		studentCourseRepository.save(sc113);
		Student s114 = new Student("D'Lante", "McClain", "j.tracy916@gmail.com");
		studentRepository.save(s114);
		StudentCourse sc114 = new StudentCourse(s114, firstPeriod);
		studentCourseRepository.save(sc114);
		Student s115 = new Student("Dakota", "McDowell", "j.tracy916@gmail.com");
		studentRepository.save(s115);
		StudentCourse sc115 = new StudentCourse(s115, firstPeriod);
		studentCourseRepository.save(sc115);
		Student s116 = new Student("Rashard", "Overton", "j.tracy916@gmail.com");
		studentRepository.save(s116);
		StudentCourse sc116 = new StudentCourse(s116, firstPeriod);
		studentCourseRepository.save(sc116);
		Student s117 = new Student("Justice", "Stamey", "j.tracy916@gmail.com");
		studentRepository.save(s117);
		StudentCourse sc117 = new StudentCourse(s117, firstPeriod);
		studentCourseRepository.save(sc117);
		Student s118 = new Student("Matthew", "Sweat", "j.tracy916@gmail.com");
		studentRepository.save(s118);
		StudentCourse sc118 = new StudentCourse(s118, firstPeriod);
		studentCourseRepository.save(sc118);
		Student s119 = new Student("Dominique", "Vucek", "j.tracy916@gmail.com");
		studentRepository.save(s119);
		StudentCourse sc119 = new StudentCourse(s119, firstPeriod);
		studentCourseRepository.save(sc119);
		Student s120 = new Student("Kenny", "Welchel", "j.tracy916@gmail.com");
		studentRepository.save(s120);
		StudentCourse sc120 = new StudentCourse(s120, firstPeriod);
		studentCourseRepository.save(sc120);
		Student s121 = new Student("Nicole", "Wilkerson", "j.tracy916@gmail.com");
		studentRepository.save(s121);
		StudentCourse sc121 = new StudentCourse(s121, firstPeriod);
		studentCourseRepository.save(sc121);
		Student s122 = new Student("Kade", "Wilson", "j.tracy916@gmail.com");
		studentRepository.save(s122);
		StudentCourse sc122 = new StudentCourse(s122, firstPeriod);
		studentCourseRepository.save(sc122);

		Assignment a11 = new Assignment("Similar Triangles HW", "2016-08-14T04:00:00.000Z", firstPeriod);
		assignmentRepository.save(a11);
		StudentAssignment s11a11 = new StudentAssignment(s11, a11, 92);
		OriginalGrade os11a11 = new OriginalGrade(s11, a11, 92);
		studentAssignmentRepository.save(s11a11);
		originalGradeRepository.save(os11a11);
		StudentAssignment s12a11 = new StudentAssignment(s12, a11, 90);
		OriginalGrade os12a11 = new OriginalGrade(s12, a11, 90);
		studentAssignmentRepository.save(s12a11);
		originalGradeRepository.save(os12a11);
		StudentAssignment s13a11 = new StudentAssignment(s13, a11, 0);
		OriginalGrade os13a11 = new OriginalGrade(s13, a11, 0);
		studentAssignmentRepository.save(s13a11);
		originalGradeRepository.save(os13a11);
		StudentAssignment s14a11 = new StudentAssignment(s14, a11, 100);
		OriginalGrade os14a11 = new OriginalGrade(s14, a11, 100);
		studentAssignmentRepository.save(s14a11);
		originalGradeRepository.save(os14a11);
		StudentAssignment s15a11 = new StudentAssignment(s15, a11, 100);
		OriginalGrade os15a11 = new OriginalGrade(s15, a11, 100);
		studentAssignmentRepository.save(s15a11);
		originalGradeRepository.save(os15a11);
		StudentAssignment s16a11 = new StudentAssignment(s16, a11, 95);
		OriginalGrade os16a11 = new OriginalGrade(s16, a11, 95);
		studentAssignmentRepository.save(s16a11);
		originalGradeRepository.save(os16a11);
		StudentAssignment s17a11 = new StudentAssignment(s17, a11, 80);
		OriginalGrade os17a11 = new OriginalGrade(s17, a11, 80);
		studentAssignmentRepository.save(s17a11);
		originalGradeRepository.save(os17a11);
		StudentAssignment s18a11 = new StudentAssignment(s18, a11, 88);
		OriginalGrade os18a11 = new OriginalGrade(s18, a11, 88);
		studentAssignmentRepository.save(s18a11);
		originalGradeRepository.save(os18a11);
		StudentAssignment s19a11 = new StudentAssignment(s19, a11, 50);
		OriginalGrade os19a11 = new OriginalGrade(s19, a11, 50);
		studentAssignmentRepository.save(s19a11);
		originalGradeRepository.save(os19a11);
		StudentAssignment s110a11 = new StudentAssignment(s110, a11, 100);
		OriginalGrade os110a11 = new OriginalGrade(s110, a11, 100);
		studentAssignmentRepository.save(s110a11);
		originalGradeRepository.save(os110a11);
		StudentAssignment s111a11 = new StudentAssignment(s111, a11, 100);
		OriginalGrade os111a11 = new OriginalGrade(s111, a11, 100);
		studentAssignmentRepository.save(s111a11);
		originalGradeRepository.save(os111a11);
		StudentAssignment s112a11 = new StudentAssignment(s112, a11, 100);
		OriginalGrade os112a11 = new OriginalGrade(s112, a11, 100);
		studentAssignmentRepository.save(s112a11);
		originalGradeRepository.save(os112a11);
		StudentAssignment s113a11 = new StudentAssignment(s113, a11, 0);
		OriginalGrade os113a11 = new OriginalGrade(s113, a11, 0);
		studentAssignmentRepository.save(s113a11);
		originalGradeRepository.save(os113a11);
		StudentAssignment s114a11 = new StudentAssignment(s114, a11, 92);
		OriginalGrade os114a11 = new OriginalGrade(s114, a11, 92);
		studentAssignmentRepository.save(s114a11);
		originalGradeRepository.save(os114a11);
		StudentAssignment s115a11 = new StudentAssignment(s115, a11, 78);
		OriginalGrade os115a11 = new OriginalGrade(s115, a11, 78);
		studentAssignmentRepository.save(s115a11);
		originalGradeRepository.save(os115a11);
		StudentAssignment s116a11 = new StudentAssignment(s116, a11, 100);
		OriginalGrade os116a11 = new OriginalGrade(s116, a11, 100);
		studentAssignmentRepository.save(s116a11);
		originalGradeRepository.save(os116a11);
		StudentAssignment s117a11 = new StudentAssignment(s117, a11, 100);
		OriginalGrade os117a11 = new OriginalGrade(s117, a11, 100);
		studentAssignmentRepository.save(s117a11);
		originalGradeRepository.save(os117a11);
		StudentAssignment s118a11 = new StudentAssignment(s118, a11, 75);
		OriginalGrade os118a11 = new OriginalGrade(s118, a11, 75);
		studentAssignmentRepository.save(s118a11);
		originalGradeRepository.save(os118a11);
		StudentAssignment s119a11 = new StudentAssignment(s119, a11, 88);
		OriginalGrade os119a11 = new OriginalGrade(s119, a11, 88);
		studentAssignmentRepository.save(s119a11);
		originalGradeRepository.save(os119a11);
		StudentAssignment s120a11 = new StudentAssignment(s120, a11, 0);
		OriginalGrade os120a11 = new OriginalGrade(s120, a11, 0);
		studentAssignmentRepository.save(s120a11);
		originalGradeRepository.save(os120a11);
		StudentAssignment s121a11 = new StudentAssignment(s121, a11, 98);
		OriginalGrade os121a11 = new OriginalGrade(s121, a11, 98);
		studentAssignmentRepository.save(s121a11);
		originalGradeRepository.save(os121a11);
		StudentAssignment s122a11 = new StudentAssignment(s122, a11, 98);
		OriginalGrade os122a11 = new OriginalGrade(s122, a11, 98);
		studentAssignmentRepository.save(s122a11);
		originalGradeRepository.save(os122a11);

		Assignment a12 = new Assignment("Similar Triangles Quiz", "2016-08-15T04:00:00.000Z", firstPeriod);
		assignmentRepository.save(a12);
		StudentAssignment s11a12 = new StudentAssignment(s11, a12, 60);
		OriginalGrade os11a12 = new OriginalGrade(s11, a12, 60);
		studentAssignmentRepository.save(s11a12);
		originalGradeRepository.save(os11a12);
		StudentAssignment s12a12 = new StudentAssignment(s12, a12, 68);
		OriginalGrade os12a12 = new OriginalGrade(s12, a12, 68);
		studentAssignmentRepository.save(s12a12);
		originalGradeRepository.save(os12a12);
		StudentAssignment s13a12 = new StudentAssignment(s13, a12, 90);
		OriginalGrade os13a12 = new OriginalGrade(s13, a12, 90);
		studentAssignmentRepository.save(s13a12);
		originalGradeRepository.save(os13a12);
		StudentAssignment s14a12 = new StudentAssignment(s14, a12, 82);
		OriginalGrade os14a12 = new OriginalGrade(s14, a12, 82);
		studentAssignmentRepository.save(s14a12);
		originalGradeRepository.save(os14a12);
		StudentAssignment s15a12 = new StudentAssignment(s15, a12, 88);
		OriginalGrade os15a12 = new OriginalGrade(s15, a12, 88);
		studentAssignmentRepository.save(s15a12);
		originalGradeRepository.save(os15a12);
		StudentAssignment s16a12 = new StudentAssignment(s16, a12, 40);
		OriginalGrade os16a12 = new OriginalGrade(s16, a12, 40);
		studentAssignmentRepository.save(s16a12);
		originalGradeRepository.save(os16a12);
		StudentAssignment s17a12 = new StudentAssignment(s17, a12, 73);
		OriginalGrade os17a12 = new OriginalGrade(s17, a12, 73);
		studentAssignmentRepository.save(s17a12);
		originalGradeRepository.save(os17a12);
		StudentAssignment s18a12 = new StudentAssignment(s18, a12, 64);
		OriginalGrade os18a12 = new OriginalGrade(s18, a12, 64);
		studentAssignmentRepository.save(s18a12);
		originalGradeRepository.save(os18a12);
		StudentAssignment s19a12 = new StudentAssignment(s19, a12, 65);
		OriginalGrade os19a12 = new OriginalGrade(s19, a12, 65);
		studentAssignmentRepository.save(s19a12);
		originalGradeRepository.save(os19a12);
		StudentAssignment s110a12 = new StudentAssignment(s110, a12, -1);
		studentAssignmentRepository.save(s110a12);
		StudentAssignment s111a12 = new StudentAssignment(s111, a12, 80);
		OriginalGrade os111a12 = new OriginalGrade(s111, a12, 80);
		studentAssignmentRepository.save(s111a12);
		originalGradeRepository.save(os111a12);
		StudentAssignment s112a12 = new StudentAssignment(s112, a12, 45);
		OriginalGrade os112a12 = new OriginalGrade(s112, a12, 45);
		studentAssignmentRepository.save(s112a12);
		originalGradeRepository.save(os112a12);
		StudentAssignment s113a12 = new StudentAssignment(s113, a12, 45);
		OriginalGrade os113a12 = new OriginalGrade(s113, a12, 45);
		studentAssignmentRepository.save(s113a12);
		originalGradeRepository.save(os113a12);
		StudentAssignment s114a12 = new StudentAssignment(s114, a12, 59);
		OriginalGrade os114a12 = new OriginalGrade(s114, a12, 59);
		studentAssignmentRepository.save(s114a12);
		originalGradeRepository.save(os114a12);
		StudentAssignment s115a12 = new StudentAssignment(s115, a12, 70);
		OriginalGrade os115a12 = new OriginalGrade(s115, a12, 70);
		studentAssignmentRepository.save(s115a12);
		originalGradeRepository.save(os115a12);
		StudentAssignment s116a12 = new StudentAssignment(s116, a12, 68);
		OriginalGrade os116a12 = new OriginalGrade(s116, a12, 68);
		studentAssignmentRepository.save(s116a12);
		originalGradeRepository.save(os116a12);
		StudentAssignment s117a12 = new StudentAssignment(s117, a12, 70);
		OriginalGrade os117a12 = new OriginalGrade(s117, a12, 70);
		studentAssignmentRepository.save(s117a12);
		originalGradeRepository.save(os117a12);
		StudentAssignment s118a12 = new StudentAssignment(s118, a12, 64);
		OriginalGrade os118a12 = new OriginalGrade(s118, a12, 64);
		studentAssignmentRepository.save(s118a12);
		originalGradeRepository.save(os118a12);
		StudentAssignment s119a12 = new StudentAssignment(s119, a12, 84);
		OriginalGrade os119a12 = new OriginalGrade(s119, a12, 84);
		studentAssignmentRepository.save(s119a12);
		originalGradeRepository.save(os119a12);
		StudentAssignment s120a12 = new StudentAssignment(s120, a12, 76);
		OriginalGrade os120a12 = new OriginalGrade(s120, a12, 76);
		studentAssignmentRepository.save(s120a12);
		originalGradeRepository.save(os120a12);
		StudentAssignment s121a12 = new StudentAssignment(s121, a12, 67);
		OriginalGrade os121a12 = new OriginalGrade(s121, a12, 67);
		studentAssignmentRepository.save(s121a12);
		originalGradeRepository.save(os121a12);
		StudentAssignment s122a12 = new StudentAssignment(s122, a12, 30);
		OriginalGrade os122a12 = new OriginalGrade(s122, a12, 30);
		studentAssignmentRepository.save(s122a12);
		originalGradeRepository.save(os122a12);


		Assignment a13 = new Assignment("Congruency Project", "2016-08-20T04:00:00.000Z", firstPeriod);
		assignmentRepository.save(a13);
		StudentAssignment s11a13 = new StudentAssignment(s11, a13, 100);
		OriginalGrade os11a13 = new OriginalGrade(s11, a13, 100);
		studentAssignmentRepository.save(s11a13);
		originalGradeRepository.save(os11a13);
		StudentAssignment s12a13 = new StudentAssignment(s12, a13, 75);
		OriginalGrade os12a13 = new OriginalGrade(s12, a13, 75);
		studentAssignmentRepository.save(s12a13);
		originalGradeRepository.save(os12a13);
		StudentAssignment s13a13 = new StudentAssignment(s13, a13, 75);
		OriginalGrade os13a13 = new OriginalGrade(s13, a13, 75);
		studentAssignmentRepository.save(s13a13);
		originalGradeRepository.save(os13a13);
		StudentAssignment s14a13 = new StudentAssignment(s14, a13, 50);
		OriginalGrade os14a13 = new OriginalGrade(s14, a13, 50);
		studentAssignmentRepository.save(s14a13);
		originalGradeRepository.save(os14a13);
		StudentAssignment s15a13 = new StudentAssignment(s15, a13, 100);
		OriginalGrade os15a13 = new OriginalGrade(s15, a13, 100);
		studentAssignmentRepository.save(s15a13);
		originalGradeRepository.save(os15a13);
		StudentAssignment s16a13 = new StudentAssignment(s16, a13, 95);
		OriginalGrade os16a13 = new OriginalGrade(s16, a13, 95);
		studentAssignmentRepository.save(s16a13);
		originalGradeRepository.save(os16a13);
		StudentAssignment s17a13 = new StudentAssignment(s17, a13, 95);
		OriginalGrade os17a13 = new OriginalGrade(s17, a13, 95);
		studentAssignmentRepository.save(s17a13);
		originalGradeRepository.save(os17a13);
		StudentAssignment s18a13 = new StudentAssignment(s18, a13, 50);
		OriginalGrade os18a13 = new OriginalGrade(s18, a13, 50);
		studentAssignmentRepository.save(s18a13);
		originalGradeRepository.save(os18a13);
		StudentAssignment s19a13 = new StudentAssignment(s19, a13, 75);
		OriginalGrade os19a13 = new OriginalGrade(s19, a13, 75);
		studentAssignmentRepository.save(s19a13);
		originalGradeRepository.save(os19a13);
		StudentAssignment s110a13 = new StudentAssignment(s110, a13, 75);
		OriginalGrade os110a13 = new OriginalGrade(s110, a13, 75);
		studentAssignmentRepository.save(s110a13);
		originalGradeRepository.save(os110a13);
		StudentAssignment s111a13 = new StudentAssignment(s111, a13, 75);
		OriginalGrade os111a13 = new OriginalGrade(s111, a13, 75);
		studentAssignmentRepository.save(s111a13);
		originalGradeRepository.save(os111a13);
		StudentAssignment s112a13 = new StudentAssignment(s112, a13, 100);
		OriginalGrade os112a13 = new OriginalGrade(s112, a13, 100);
		studentAssignmentRepository.save(s112a13);
		originalGradeRepository.save(os112a13);
		StudentAssignment s113a13 = new StudentAssignment(s113, a13, 100);
		OriginalGrade os113a13 = new OriginalGrade(s113, a13, 100);
		studentAssignmentRepository.save(s113a13);
		originalGradeRepository.save(os113a13);
		StudentAssignment s114a13 = new StudentAssignment(s114, a13, 95);
		OriginalGrade os114a13 = new OriginalGrade(s114, a13, 95);
		studentAssignmentRepository.save(s114a13);
		originalGradeRepository.save(os114a13);
		StudentAssignment s115a13 = new StudentAssignment(s115, a13, 95);
		OriginalGrade os115a13 = new OriginalGrade(s115, a13, 95);
		studentAssignmentRepository.save(s115a13);
		originalGradeRepository.save(os115a13);
		StudentAssignment s116a13 = new StudentAssignment(s116, a13, 75);
		OriginalGrade os116a13 = new OriginalGrade(s116, a13, 75);
		studentAssignmentRepository.save(s116a13);
		originalGradeRepository.save(os116a13);
		StudentAssignment s117a13 = new StudentAssignment(s117, a13, 75);
		OriginalGrade os117a13 = new OriginalGrade(s117, a13, 75);
		studentAssignmentRepository.save(s117a13);
		originalGradeRepository.save(os117a13);
		StudentAssignment s118a13 = new StudentAssignment(s118, a13, 75);
		OriginalGrade os118a13 = new OriginalGrade(s118, a13, 75);
		studentAssignmentRepository.save(s118a13);
		originalGradeRepository.save(os118a13);
		StudentAssignment s119a13 = new StudentAssignment(s119, a13, 95);
		OriginalGrade os119a13 = new OriginalGrade(s119, a13, 95);
		studentAssignmentRepository.save(s119a13);
		originalGradeRepository.save(os119a13);
		StudentAssignment s120a13 = new StudentAssignment(s120, a13, 50);
		OriginalGrade os120a13 = new OriginalGrade(s120, a13, 50);
		studentAssignmentRepository.save(s120a13);
		originalGradeRepository.save(os120a13);
		StudentAssignment s121a13 = new StudentAssignment(s121, a13, 0);
		OriginalGrade os121a13 = new OriginalGrade(s121, a13, 0);
		studentAssignmentRepository.save(s121a13);
		originalGradeRepository.save(os121a13);
		StudentAssignment s122a13 = new StudentAssignment(s122, a13, 75);
		OriginalGrade os122a13 = new OriginalGrade(s122, a13, 75);
		studentAssignmentRepository.save(s122a13);
		originalGradeRepository.save(os122a13);

		Assignment a14 = new Assignment("Unit 1 Test", "2016-08-22T04:00:00.000Z", firstPeriod);
		assignmentRepository.save(a14);
		StudentAssignment s11a14 = new StudentAssignment(s11, a14, 72);
		OriginalGrade os11a14 = new OriginalGrade(s11, a14, 72);
		studentAssignmentRepository.save(s11a14);
		originalGradeRepository.save(os11a14);
		StudentAssignment s12a14 = new StudentAssignment(s12, a14, 78);
		OriginalGrade os12a14 = new OriginalGrade(s12, a14, 78);
		studentAssignmentRepository.save(s12a14);
		originalGradeRepository.save(os12a14);
		StudentAssignment s13a14 = new StudentAssignment(s13, a14, 78);
		OriginalGrade os13a14 = new OriginalGrade(s13, a14, 78);
		studentAssignmentRepository.save(s13a14);
		originalGradeRepository.save(os13a14);
		StudentAssignment s14a14 = new StudentAssignment(s14, a14, 88);
		OriginalGrade os14a14 = new OriginalGrade(s14, a14, 88);
		studentAssignmentRepository.save(s14a14);
		originalGradeRepository.save(os14a14);
		StudentAssignment s15a14 = new StudentAssignment(s15, a14, 60);
		OriginalGrade os15a14 = new OriginalGrade(s15, a14, 60);
		studentAssignmentRepository.save(s15a14);
		originalGradeRepository.save(os15a14);
		StudentAssignment s16a14 = new StudentAssignment(s16, a14, 64);
		OriginalGrade os16a14 = new OriginalGrade(s16, a14, 64);
		studentAssignmentRepository.save(s16a14);
		originalGradeRepository.save(os16a14);
		StudentAssignment s17a14 = new StudentAssignment(s17, a14, 58);
		OriginalGrade os17a14 = new OriginalGrade(s17, a14, 58);
		studentAssignmentRepository.save(s17a14);
		originalGradeRepository.save(os17a14);
		StudentAssignment s18a14 = new StudentAssignment(s18, a14, 69);
		OriginalGrade os18a14 = new OriginalGrade(s18, a14, 69);
		studentAssignmentRepository.save(s18a14);
		originalGradeRepository.save(os18a14);
		StudentAssignment s19a14 = new StudentAssignment(s19, a14, 72);
		OriginalGrade os19a14 = new OriginalGrade(s19, a14, 72);
		studentAssignmentRepository.save(s19a14);
		originalGradeRepository.save(os19a14);
		StudentAssignment s110a14 = new StudentAssignment(s110, a14, 71);
		OriginalGrade os110a14 = new OriginalGrade(s110, a14, 71);
		studentAssignmentRepository.save(s110a14);
		originalGradeRepository.save(os110a14);
		StudentAssignment s111a14 = new StudentAssignment(s111, a14, 84);
		OriginalGrade os111a14 = new OriginalGrade(s111, a14, 84);
		studentAssignmentRepository.save(s111a14);
		originalGradeRepository.save(os111a14);
		StudentAssignment s112a14 = new StudentAssignment(s112, a14, 80);
		OriginalGrade os112a14 = new OriginalGrade(s112, a14, 80);
		studentAssignmentRepository.save(s112a14);
		originalGradeRepository.save(os112a14);
		StudentAssignment s113a14 = new StudentAssignment(s113, a14, 78);
		OriginalGrade os113a14 = new OriginalGrade(s113, a14, 78);
		studentAssignmentRepository.save(s113a14);
		originalGradeRepository.save(os113a14);
		StudentAssignment s114a14 = new StudentAssignment(s114, a14, 42);
		OriginalGrade os114a14 = new OriginalGrade(s114, a14, 42);
		studentAssignmentRepository.save(s114a14);
		originalGradeRepository.save(os114a14);
		StudentAssignment s115a14 = new StudentAssignment(s115, a14, 81);
		OriginalGrade os115a14 = new OriginalGrade(s115, a14, 81);
		studentAssignmentRepository.save(s115a14);
		originalGradeRepository.save(os115a14);
		StudentAssignment s116a14 = new StudentAssignment(s116, a14, 74);
		OriginalGrade os116a14 = new OriginalGrade(s116, a14, 74);
		studentAssignmentRepository.save(s116a14);
		originalGradeRepository.save(os116a14);
		StudentAssignment s117a14 = new StudentAssignment(s117, a14, 78);
		OriginalGrade os117a14 = new OriginalGrade(s117, a14, 78);
		studentAssignmentRepository.save(s117a14);
		originalGradeRepository.save(os117a14);
		StudentAssignment s118a14 = new StudentAssignment(s118, a14, 70);
		OriginalGrade os118a14 = new OriginalGrade(s118, a14, 70);
		studentAssignmentRepository.save(s118a14);
		originalGradeRepository.save(os118a14);
		StudentAssignment s119a14 = new StudentAssignment(s119, a14, 65);
		OriginalGrade os119a14 = new OriginalGrade(s119, a14, 65);
		studentAssignmentRepository.save(s119a14);
		originalGradeRepository.save(os119a14);
		StudentAssignment s120a14 = new StudentAssignment(s120, a14, 68);
		OriginalGrade os120a14 = new OriginalGrade(s120, a14, 68);
		studentAssignmentRepository.save(s120a14);
		originalGradeRepository.save(os120a14);
		StudentAssignment s121a14 = new StudentAssignment(s121, a14, 72);
		OriginalGrade os121a14 = new OriginalGrade(s121, a14, 72);
		studentAssignmentRepository.save(s121a14);
		originalGradeRepository.save(os121a14);
		StudentAssignment s122a14 = new StudentAssignment(s122, a14, 77);
		OriginalGrade os122a14 = new OriginalGrade(s122, a14, 77);
		studentAssignmentRepository.save(s122a14);
		originalGradeRepository.save(os122a14);



		Course secondPeriod = new Course("Math Support", "Math", 11, teacher);
		courseRepository.save(secondPeriod);
		Student s21 = new Student("Quincy", "Allen", "j.tracy916@gmail.com");
		studentRepository.save(s21);
		StudentCourse sc21 = new StudentCourse(s21, secondPeriod);
		studentCourseRepository.save(sc21);
		Student s22 = new Student("Ke'Wane", "Barnes", "j.tracy916@gmail.com");
		studentRepository.save(s22);
		StudentCourse sc22 = new StudentCourse(s22, secondPeriod);
		studentCourseRepository.save(sc22);
		Student s23 = new Student("Seth", "Burnley", "j.tracy916@gmail.com");
		studentRepository.save(s23);
		StudentCourse sc23 = new StudentCourse(s23, secondPeriod);
		studentCourseRepository.save(sc23);
		Student s24 = new Student("Taylor", "Cargle", "j.tracy916@gmail.com");
		studentRepository.save(s24);
		StudentCourse sc24 = new StudentCourse(s24, secondPeriod);
		studentCourseRepository.save(sc24);
		Student s25 = new Student("Chase", "Clemons", "j.tracy916@gmail.com");
		studentRepository.save(s25);
		StudentCourse sc25 = new StudentCourse(s25, secondPeriod);
		studentCourseRepository.save(sc25);
		Student s26 = new Student("Harley", "Cook", "j.tracy916@gmail.com");
		studentRepository.save(s26);
		StudentCourse sc26 = new StudentCourse(s26, secondPeriod);
		studentCourseRepository.save(sc26);
		Student s27 = new Student("Faith", "Croker", "j.tracy916@gmail.com");
		studentRepository.save(s27);
		StudentCourse sc27 = new StudentCourse(s27, secondPeriod);
		studentCourseRepository.save(sc27);
		Student s28 = new Student("Allie", "Dunn", "j.tracy916@gmail.com");
		studentRepository.save(s28);
		StudentCourse sc28 = new StudentCourse(s28, secondPeriod);
		studentCourseRepository.save(sc28);
		Student s29 = new Student("Hunter", "Forsyth", "j.tracy916@gmail.com");
		studentRepository.save(s29);
		StudentCourse sc29 = new StudentCourse(s29, secondPeriod);
		studentCourseRepository.save(sc29);
		Student s210 = new Student("Abbie", "Godfrey", "j.tracy916@gmail.com");
		studentRepository.save(s210);
		StudentCourse sc210 = new StudentCourse(s210, secondPeriod);
		studentCourseRepository.save(sc210);
		Student s211 = new Student("Garrett", "Harris", "j.tracy916@gmail.com");
		studentRepository.save(s211);
		StudentCourse sc211 = new StudentCourse(s211, secondPeriod);
		studentCourseRepository.save(sc211);
		Student s212 = new Student("Thomas", "Kofile", "j.tracy916@gmail.com");
		studentRepository.save(s212);
		StudentCourse sc212 = new StudentCourse(s212, secondPeriod);
		studentCourseRepository.save(sc212);
		Student s213 = new Student("Brody", "McElwee", "j.tracy916@gmail.com");
		studentRepository.save(s213);
		StudentCourse sc213 = new StudentCourse(s213, secondPeriod);
		studentCourseRepository.save(sc213);
		Student s214 = new Student("Dallas", "O'Connor", "j.tracy916@gmail.com");
		studentRepository.save(s214);
		StudentCourse sc214 = new StudentCourse(s214, secondPeriod);
		studentCourseRepository.save(sc214);
		Student s215 = new Student("Dylan", "Smith", "j.tracy916@gmail.com");
		studentRepository.save(s215);
		StudentCourse sc215 = new StudentCourse(s215, secondPeriod);
		studentCourseRepository.save(sc215);
		Student s216 = new Student("Kera", "Wynn", "j.tracy916@gmail.com");
		studentRepository.save(s216);
		StudentCourse sc216 = new StudentCourse(s216, secondPeriod);
		studentCourseRepository.save(sc216);

		Assignment a21 = new Assignment("Math Journal", "2016-08-09T04:00:00.000Z", secondPeriod);
		assignmentRepository.save(a21);
		StudentAssignment s21a21 = new StudentAssignment(s21, a21, 90);
		OriginalGrade os21a21 = new OriginalGrade(s21, a21, 90);
		studentAssignmentRepository.save(s21a21);
		originalGradeRepository.save(os21a21);
		StudentAssignment s22a21 = new StudentAssignment(s22, a21, 95);
		OriginalGrade os22a21 = new OriginalGrade(s22, a21, 95);
		studentAssignmentRepository.save(s22a21);
		originalGradeRepository.save(os22a21);
		StudentAssignment s23a21 = new StudentAssignment(s23, a21, 0);
		OriginalGrade os23a21 = new OriginalGrade(s23, a21, 0);
		studentAssignmentRepository.save(s23a21);
		originalGradeRepository.save(os23a21);
		StudentAssignment s24a21 = new StudentAssignment(s24, a21, 0);
		OriginalGrade os24a21 = new OriginalGrade(s24, a21, 0);
		studentAssignmentRepository.save(s24a21);
		originalGradeRepository.save(os24a21);
		StudentAssignment s25a21 = new StudentAssignment(s25, a21, 90);
		OriginalGrade os25a21 = new OriginalGrade(s25, a21, 90);
		studentAssignmentRepository.save(s25a21);
		originalGradeRepository.save(os25a21);
		StudentAssignment s26a21 = new StudentAssignment(s26, a21, 92);
		OriginalGrade os26a21 = new OriginalGrade(s26, a21, 92);
		studentAssignmentRepository.save(s26a21);
		originalGradeRepository.save(os26a21);
		StudentAssignment s27a21 = new StudentAssignment(s27, a21, 86);
		OriginalGrade os27a21 = new OriginalGrade(s27, a21, 86);
		studentAssignmentRepository.save(s27a21);
		originalGradeRepository.save(os27a21);
		StudentAssignment s28a21 = new StudentAssignment(s28, a21, 75);
		OriginalGrade os28a21 = new OriginalGrade(s28, a21, 75);
		studentAssignmentRepository.save(s28a21);
		originalGradeRepository.save(os28a21);
		StudentAssignment s29a21 = new StudentAssignment(s29, a21, 70);
		OriginalGrade os29a21 = new OriginalGrade(s29, a21, 70);
		studentAssignmentRepository.save(s29a21);
		originalGradeRepository.save(os29a21);
		StudentAssignment s210a21 = new StudentAssignment(s210, a21, 88);
		OriginalGrade os210a21 = new OriginalGrade(s210, a21, 88);
		studentAssignmentRepository.save(s210a21);
		originalGradeRepository.save(os210a21);
		StudentAssignment s211a21 = new StudentAssignment(s211, a21, 92);
		OriginalGrade os211a21 = new OriginalGrade(s211, a21, 92);
		studentAssignmentRepository.save(s211a21);
		originalGradeRepository.save(os211a21);
		StudentAssignment s212a21 = new StudentAssignment(s212, a21, 98);
		OriginalGrade os212a21 = new OriginalGrade(s212, a21, 98);
		studentAssignmentRepository.save(s212a21);
		originalGradeRepository.save(os212a21);
		StudentAssignment s213a21 = new StudentAssignment(s213, a21, 98);
		OriginalGrade os213a21 = new OriginalGrade(s213, a21, 98);
		studentAssignmentRepository.save(s213a21);
		originalGradeRepository.save(os213a21);
		StudentAssignment s214a21 = new StudentAssignment(s214, a21, 80);
		OriginalGrade os214a21 = new OriginalGrade(s214, a21, 80);
		studentAssignmentRepository.save(s214a21);
		originalGradeRepository.save(os214a21);
		StudentAssignment s215a21 = new StudentAssignment(s215, a21, 70);
		OriginalGrade os215a21 = new OriginalGrade(s215, a21, 70);
		studentAssignmentRepository.save(s215a21);
		originalGradeRepository.save(os215a21);
		StudentAssignment s216a21 = new StudentAssignment(s216, a21, 94);
		OriginalGrade os216a21 = new OriginalGrade(s216, a21, 94);
		studentAssignmentRepository.save(s216a21);
		originalGradeRepository.save(os216a21);

		Assignment a22 = new Assignment("Polygons Quiz", "2016-08-16T04:00:00.000Z", secondPeriod);
		assignmentRepository.save(a22);
		StudentAssignment s21a22 = new StudentAssignment(s21, a22, 40);
		OriginalGrade os21a22 = new OriginalGrade(s21, a22, 40);
		studentAssignmentRepository.save(s21a22);
		originalGradeRepository.save(os21a22);
		StudentAssignment s22a22 = new StudentAssignment(s22, a22, 42);
		OriginalGrade os22a22 = new OriginalGrade(s22, a22, 42);
		studentAssignmentRepository.save(s22a22);
		originalGradeRepository.save(os22a22);
		StudentAssignment s23a22 = new StudentAssignment(s23, a22, 39);
		OriginalGrade os23a22 = new OriginalGrade(s23, a22, 39);
		studentAssignmentRepository.save(s23a22);
		originalGradeRepository.save(os23a22);
		StudentAssignment s24a22 = new StudentAssignment(s24, a22, 36);
		OriginalGrade os24a22 = new OriginalGrade(s24, a22, 36);
		studentAssignmentRepository.save(s24a22);
		originalGradeRepository.save(os24a22);
		StudentAssignment s25a22 = new StudentAssignment(s25, a22, 49);
		OriginalGrade os25a22 = new OriginalGrade(s25, a22, 49);
		studentAssignmentRepository.save(s25a22);
		originalGradeRepository.save(os25a22);
		StudentAssignment s26a22 = new StudentAssignment(s26, a22, 45);
		OriginalGrade os26a22 = new OriginalGrade(s26, a22, 45);
		studentAssignmentRepository.save(s26a22);
		originalGradeRepository.save(os26a22);
		StudentAssignment s27a22 = new StudentAssignment(s27, a22, 85);
		OriginalGrade os27a22 = new OriginalGrade(s27, a22, 85);
		studentAssignmentRepository.save(s27a22);
		originalGradeRepository.save(os27a22);
		StudentAssignment s28a22 = new StudentAssignment(s28, a22, 76);
		OriginalGrade os28a22 = new OriginalGrade(s28, a22, 76);
		studentAssignmentRepository.save(s28a22);
		originalGradeRepository.save(os28a22);
		StudentAssignment s29a22 = new StudentAssignment(s29, a22, 82);
		OriginalGrade os29a22 = new OriginalGrade(s29, a22, 82);
		studentAssignmentRepository.save(s29a22);
		originalGradeRepository.save(os29a22);
		StudentAssignment s210a22 = new StudentAssignment(s210, a22, 50);
		OriginalGrade os210a22 = new OriginalGrade(s210, a22, 50);
		studentAssignmentRepository.save(s210a22);
		originalGradeRepository.save(os210a22);
		StudentAssignment s211a22 = new StudentAssignment(s211, a22, 60);
		OriginalGrade os211a22 = new OriginalGrade(s211, a22, 60);
		studentAssignmentRepository.save(s211a22);
		originalGradeRepository.save(os211a22);
		StudentAssignment s212a22 = new StudentAssignment(s212, a22, 55);
		OriginalGrade os212a22 = new OriginalGrade(s212, a22, 55);
		studentAssignmentRepository.save(s212a22);
		originalGradeRepository.save(os212a22);
		StudentAssignment s213a22 = new StudentAssignment(s213, a22, 58);
		OriginalGrade os213a22 = new OriginalGrade(s213, a22, 58);
		studentAssignmentRepository.save(s213a22);
		originalGradeRepository.save(os213a22);
		StudentAssignment s214a22 = new StudentAssignment(s214, a22, 50);
		OriginalGrade os214a22 = new OriginalGrade(s214, a22, 50);
		studentAssignmentRepository.save(s214a22);
		originalGradeRepository.save(os214a22);
		StudentAssignment s215a22 = new StudentAssignment(s215, a22, 67);
		OriginalGrade os215a22 = new OriginalGrade(s215, a22, 67);
		studentAssignmentRepository.save(s215a22);
		originalGradeRepository.save(os215a22);
		StudentAssignment s216a22 = new StudentAssignment(s216, a22, 52);
		OriginalGrade os216a22 = new OriginalGrade(s216, a22, 52);
		studentAssignmentRepository.save(s216a22);
		originalGradeRepository.save(os216a22);

		Assignment a23 = new Assignment("Triangle Presentations", "2016-08-09T04:00:00.000Z", secondPeriod);
		assignmentRepository.save(a23);
		StudentAssignment s21a23 = new StudentAssignment(s21, a23, 80);
		OriginalGrade os21a23 = new OriginalGrade(s21, a23, 80);
		studentAssignmentRepository.save(s21a23);
		originalGradeRepository.save(os21a23);
		StudentAssignment s22a23 = new StudentAssignment(s22, a23, 90);
		OriginalGrade os22a23 = new OriginalGrade(s22, a23, 90);
		studentAssignmentRepository.save(s22a23);
		originalGradeRepository.save(os22a23);
		StudentAssignment s23a23 = new StudentAssignment(s23, a23, 95);
		OriginalGrade os23a23 = new OriginalGrade(s23, a23, 95);
		studentAssignmentRepository.save(s23a23);
		originalGradeRepository.save(os23a23);
		StudentAssignment s24a23 = new StudentAssignment(s24, a23, 90);
		OriginalGrade os24a23 = new OriginalGrade(s24, a23, 90);
		studentAssignmentRepository.save(s24a23);
		originalGradeRepository.save(os24a23);
		StudentAssignment s25a23 = new StudentAssignment(s25, a23, 80);
		OriginalGrade os25a23 = new OriginalGrade(s25, a23, 80);
		studentAssignmentRepository.save(s25a23);
		originalGradeRepository.save(os25a23);
		StudentAssignment s26a23 = new StudentAssignment(s26, a23, 80);
		OriginalGrade os26a23 = new OriginalGrade(s26, a23, 80);
		studentAssignmentRepository.save(s26a23);
		originalGradeRepository.save(os26a23);
		StudentAssignment s27a23 = new StudentAssignment(s27, a23, 50);
		OriginalGrade os27a23 = new OriginalGrade(s27, a23, 50);
		studentAssignmentRepository.save(s27a23);
		originalGradeRepository.save(os27a23);
		StudentAssignment s28a23 = new StudentAssignment(s28, a23, 85);
		OriginalGrade os28a23 = new OriginalGrade(s28, a23, 85);
		studentAssignmentRepository.save(s28a23);
		originalGradeRepository.save(os28a23);
		StudentAssignment s29a23 = new StudentAssignment(s29, a23, 95);
		OriginalGrade os29a23 = new OriginalGrade(s29, a23, 95);
		studentAssignmentRepository.save(s29a23);
		originalGradeRepository.save(os29a23);
		StudentAssignment s210a23 = new StudentAssignment(s210, a23, 90);
		OriginalGrade os210a23 = new OriginalGrade(s210, a23, 90);
		studentAssignmentRepository.save(s210a23);
		originalGradeRepository.save(os210a23);
		StudentAssignment s211a23 = new StudentAssignment(s211, a23, 90);
		OriginalGrade os211a23 = new OriginalGrade(s211, a23, 90);
		studentAssignmentRepository.save(s211a23);
		originalGradeRepository.save(os211a23);
		StudentAssignment s212a23 = new StudentAssignment(s212, a23, 75);
		OriginalGrade os212a23 = new OriginalGrade(s212, a23, 75);
		studentAssignmentRepository.save(s212a23);
		originalGradeRepository.save(os212a23);
		StudentAssignment s213a23 = new StudentAssignment(s213, a23, 80);
		OriginalGrade os213a23 = new OriginalGrade(s213, a23, 80);
		studentAssignmentRepository.save(s213a23);
		originalGradeRepository.save(os213a23);
		StudentAssignment s214a23 = new StudentAssignment(s214, a23, 85);
		OriginalGrade os214a23 = new OriginalGrade(s214, a23, 85);
		studentAssignmentRepository.save(s214a23);
		originalGradeRepository.save(os214a23);
		StudentAssignment s215a23 = new StudentAssignment(s215, a23, 60);
		OriginalGrade os215a23 = new OriginalGrade(s215, a23, 60);
		studentAssignmentRepository.save(s215a23);
		originalGradeRepository.save(os215a23);
		StudentAssignment s216a23 = new StudentAssignment(s216, a23, 90);
		OriginalGrade os216a23 = new OriginalGrade(s216, a23, 90);
		studentAssignmentRepository.save(s216a23);
		originalGradeRepository.save(os216a23);


		Course thirdPeriod = new Course("Accelerated Advanced Algebra II", "Math", 10, teacher);
		courseRepository.save(thirdPeriod);
		Student s41 = new Student("Carson", "Brown", "j.tracy916@gmail.com");
		studentRepository.save(s41);
		StudentCourse sc41 = new StudentCourse(s41, thirdPeriod);
		studentCourseRepository.save(sc41);
		Student s42 = new Student("Taylor", "Camp", "j.tracy916@gmail.com");
		studentRepository.save(s42);
		StudentCourse sc42 = new StudentCourse(s42, thirdPeriod);
		studentCourseRepository.save(sc42);
		Student s43 = new Student("Alexis", "Clanton", "j.tracy916@gmail.com");
		studentRepository.save(s43);
		StudentCourse sc43 = new StudentCourse(s43, thirdPeriod);
		studentCourseRepository.save(sc43);
		Student s44 = new Student("Steven", "Duarte", "j.tracy916@gmail.com");
		studentRepository.save(s44);
		StudentCourse sc44 = new StudentCourse(s44, thirdPeriod);
		studentCourseRepository.save(sc44);
		Student s45 = new Student("Diego", "Edwards", "j.tracy916@gmail.com");
		studentRepository.save(s45);
		StudentCourse sc45 = new StudentCourse(s45, thirdPeriod);
		studentCourseRepository.save(sc45);
		Student s46 = new Student("Dawson", "Elrod", "j.tracy916@gmail.com");
		studentRepository.save(s46);
		StudentCourse sc46 = new StudentCourse(s46, thirdPeriod);
		studentCourseRepository.save(sc46);
		Student s47 = new Student("Lacey", "Etheridge", "j.tracy916@gmail.com");
		studentRepository.save(s47);
		StudentCourse sc47 = new StudentCourse(s47, thirdPeriod);
		studentCourseRepository.save(sc47);
		Student s48 = new Student("Michael", "Gay", "j.tracy916@gmail.com");
		studentRepository.save(s48);
		StudentCourse sc48 = new StudentCourse(s48, thirdPeriod);
		studentCourseRepository.save(sc48);
		Student s49 = new Student("Ethan", "Gober", "j.tracy916@gmail.com");
		studentRepository.save(s49);
		StudentCourse sc49 = new StudentCourse(s49, thirdPeriod);
		studentCourseRepository.save(sc49);
		Student s410 = new Student("Belle", "Hudson", "j.tracy916@gmail.com");
		studentRepository.save(s410);
		StudentCourse sc410 = new StudentCourse(s410, thirdPeriod);
		studentCourseRepository.save(sc410);
		Student s411 = new Student("Robyn", "James", "j.tracy916@gmail.com");
		studentRepository.save(s411);
		StudentCourse sc411 = new StudentCourse(s411, thirdPeriod);
		studentCourseRepository.save(sc411);
		Student s412 = new Student("Ashton", "Kellogg", "j.tracy916@gmail.com");
		studentRepository.save(s412);
		StudentCourse sc412 = new StudentCourse(s412, thirdPeriod);
		studentCourseRepository.save(sc412);
		Student s413 = new Student("Halle", "Maxwell", "j.tracy916@gmail.com");
		studentRepository.save(s413);
		StudentCourse sc413 = new StudentCourse(s413, thirdPeriod);
		studentCourseRepository.save(sc413);
		Student s414 = new Student("Taylor", "McVey", "j.tracy916@gmail.com");
		studentRepository.save(s414);
		StudentCourse sc414 = new StudentCourse(s414, thirdPeriod);
		studentCourseRepository.save(sc414);
		Student s415 = new Student("Riha", "Momin", "j.tracy916@gmail.com");
		studentRepository.save(s415);
		StudentCourse sc415 = new StudentCourse(s415, thirdPeriod);
		studentCourseRepository.save(sc415);
		Student s416 = new Student("Jonah", "Roberson", "j.tracy916@gmail.com");
		studentRepository.save(s416);
		StudentCourse sc416 = new StudentCourse(s416, thirdPeriod);
		studentCourseRepository.save(sc416);
		Student s417 = new Student("Courtney", "Smith", "j.tracy916@gmail.com");
		studentRepository.save(s417);
		StudentCourse sc417 = new StudentCourse(s417, thirdPeriod);
		studentCourseRepository.save(sc417);
		Student s418 = new Student("Alli", "Streetman", "j.tracy916@gmail.com");
		studentRepository.save(s418);
		StudentCourse sc418 = new StudentCourse(s418, thirdPeriod);
		studentCourseRepository.save(sc418);

		Assignment a41 = new Assignment("\"How Odd\" Investigation", "2016-08-12T04:00:00.000Z", thirdPeriod);
		assignmentRepository.save(a41);
		StudentAssignment s41a41 = new StudentAssignment(s41, a41,-1);
		studentAssignmentRepository.save(s41a41);
		StudentAssignment s42a41 = new StudentAssignment(s42, a41, -1);
		studentAssignmentRepository.save(s42a41);
		StudentAssignment s43a41 = new StudentAssignment(s43, a41, -1);
		studentAssignmentRepository.save(s43a41);
		StudentAssignment s44a41 = new StudentAssignment(s44, a41, 80);
		OriginalGrade os44a41 = new OriginalGrade(s44, a41, 80);
		studentAssignmentRepository.save(s44a41);
		originalGradeRepository.save(os44a41);
		StudentAssignment s45a41 = new StudentAssignment(s45, a41, 85);
		OriginalGrade os45a41 = new OriginalGrade(s45, a41, 85);
		studentAssignmentRepository.save(s45a41);
		originalGradeRepository.save(os45a41);
		StudentAssignment s46a41 = new StudentAssignment(s46, a41, -1);
		studentAssignmentRepository.save(s46a41);
		StudentAssignment s47a41 = new StudentAssignment(s47, a41, -1);
		studentAssignmentRepository.save(s47a41);
		StudentAssignment s48a41 = new StudentAssignment(s48, a41, 90);
		OriginalGrade os48a41 = new OriginalGrade(s48, a41, 90);
		studentAssignmentRepository.save(s48a41);
		originalGradeRepository.save(os48a41);
		StudentAssignment s49a41 = new StudentAssignment(s49, a41, 80);
		OriginalGrade os49a41 = new OriginalGrade(s49, a41, 80);
		studentAssignmentRepository.save(s49a41);
		originalGradeRepository.save(os49a41);
		StudentAssignment s410a41 = new StudentAssignment(s410, a41, 85);
		OriginalGrade os410a41 = new OriginalGrade(s410, a41, 85);
		studentAssignmentRepository.save(s410a41);
		originalGradeRepository.save(os410a41);
		StudentAssignment s411a41 = new StudentAssignment(s411, a41, 75);
		OriginalGrade os411a41 = new OriginalGrade(s411, a41, 75);
		studentAssignmentRepository.save(s411a41);
		originalGradeRepository.save(os411a41);
		StudentAssignment s412a41 = new StudentAssignment(s412, a41, 70);
		OriginalGrade os412a41 = new OriginalGrade(s412, a41, 70);
		studentAssignmentRepository.save(s412a41);
		originalGradeRepository.save(os412a41);
		StudentAssignment s413a41 = new StudentAssignment(s413, a41, 90);
		OriginalGrade os413a41 = new OriginalGrade(s413, a41, 90);
		studentAssignmentRepository.save(s413a41);
		originalGradeRepository.save(os413a41);
		StudentAssignment s414a41 = new StudentAssignment(s414, a41, 90);
		OriginalGrade os414a41 = new OriginalGrade(s414, a41, 90);
		studentAssignmentRepository.save(s414a41);
		originalGradeRepository.save(os414a41);
		StudentAssignment s415a41 = new StudentAssignment(s415, a41, 92);
		OriginalGrade os415a41 = new OriginalGrade(s415, a41, 92);
		studentAssignmentRepository.save(s415a41);
		originalGradeRepository.save(os415a41);
		StudentAssignment s416a41 = new StudentAssignment(s416, a41, 78);
		OriginalGrade os416a41 = new OriginalGrade(s416, a41, 78);
		studentAssignmentRepository.save(s416a41);
		originalGradeRepository.save(os416a41);
		StudentAssignment s417a41 = new StudentAssignment(s417, a41, 88);
		OriginalGrade os417a41 = new OriginalGrade(s417, a41, 88);
		studentAssignmentRepository.save(s417a41);
		originalGradeRepository.save(os417a41);
		StudentAssignment s418a41 = new StudentAssignment(s418, a41, 88);
		OriginalGrade os418a41 = new OriginalGrade(s418, a41, 88);
		studentAssignmentRepository.save(s418a41);
		originalGradeRepository.save(os418a41);

		Assignment a42 = new Assignment("Probability Unit Test", "2016-08-20T04:00:00.000Z", thirdPeriod);
		assignmentRepository.save(a42);
		StudentAssignment s41a42 = new StudentAssignment(s41, a42, 96);
		OriginalGrade os41a42 = new OriginalGrade(s41, a42, 96);
		studentAssignmentRepository.save(s41a42);
		originalGradeRepository.save(os41a42);
		StudentAssignment s42a42 = new StudentAssignment(s42, a42, 90);
		OriginalGrade os42a42 = new OriginalGrade(s42, a42, 90);
		studentAssignmentRepository.save(s42a42);
		originalGradeRepository.save(os42a42);
		StudentAssignment s43a42 = new StudentAssignment(s43, a42, 80);
		OriginalGrade os43a42 = new OriginalGrade(s43, a42, 80);
		studentAssignmentRepository.save(s43a42);
		originalGradeRepository.save(os43a42);
		StudentAssignment s44a42 = new StudentAssignment(s44, a42, 72);
		OriginalGrade os44a42 = new OriginalGrade(s44, a42, 72);
		studentAssignmentRepository.save(s44a42);
		originalGradeRepository.save(os44a42);
		StudentAssignment s45a42 = new StudentAssignment(s45, a42, 68);
		OriginalGrade os45a42 = new OriginalGrade(s45, a42, 68);
		studentAssignmentRepository.save(s45a42);
		originalGradeRepository.save(os45a42);
		StudentAssignment s46a42 = new StudentAssignment(s46, a42, 72);
		OriginalGrade os46a42 = new OriginalGrade(s46, a42, 72);
		studentAssignmentRepository.save(s46a42);
		originalGradeRepository.save(os46a42);
		StudentAssignment s47a42 = new StudentAssignment(s47, a42, 94);
		OriginalGrade os47a42 = new OriginalGrade(s47, a42, 94);
		studentAssignmentRepository.save(s47a42);
		originalGradeRepository.save(os47a42);
		StudentAssignment s48a42 = new StudentAssignment(s48, a42, 90);
		OriginalGrade os48a42 = new OriginalGrade(s48, a42, 90);
		studentAssignmentRepository.save(s48a42);
		originalGradeRepository.save(os48a42);
		StudentAssignment s49a42 = new StudentAssignment(s49, a42, 89);
		OriginalGrade os49a42 = new OriginalGrade(s49, a42, 89);
		studentAssignmentRepository.save(s49a42);
		originalGradeRepository.save(os49a42);
		StudentAssignment s410a42 = new StudentAssignment(s410, a42, 79);
		OriginalGrade os410a42 = new OriginalGrade(s410, a42, 79);
		studentAssignmentRepository.save(s410a42);
		originalGradeRepository.save(os410a42);
		StudentAssignment s411a42 = new StudentAssignment(s411, a42, 70);
		OriginalGrade os411a42 = new OriginalGrade(s411, a42, 70);
		studentAssignmentRepository.save(s411a42);
		originalGradeRepository.save(os411a42);
		StudentAssignment s412a42 = new StudentAssignment(s412, a42, 94);
		OriginalGrade os412a42 = new OriginalGrade(s412, a42, 94);
		studentAssignmentRepository.save(s412a42);
		originalGradeRepository.save(os412a42);
		StudentAssignment s413a42 = new StudentAssignment(s413, a42, 81);
		OriginalGrade os413a42 = new OriginalGrade(s413, a42, 81);
		studentAssignmentRepository.save(s413a42);
		originalGradeRepository.save(os413a42);
		StudentAssignment s414a42 = new StudentAssignment(s414, a42, 88);
		OriginalGrade os414a42 = new OriginalGrade(s414, a42, 88);
		studentAssignmentRepository.save(s414a42);
		originalGradeRepository.save(os414a42);
		StudentAssignment s415a42 = new StudentAssignment(s415, a42, 78);
		OriginalGrade os415a42 = new OriginalGrade(s415, a42, 78);
		studentAssignmentRepository.save(s415a42);
		originalGradeRepository.save(os415a42);
		StudentAssignment s416a42 = new StudentAssignment(s416, a42, 92);
		OriginalGrade os416a42 = new OriginalGrade(s416, a42, 92);
		studentAssignmentRepository.save(s416a42);
		originalGradeRepository.save(os416a42);
		StudentAssignment s417a42 = new StudentAssignment(s417, a42, 90);
		OriginalGrade os417a42 = new OriginalGrade(s417, a42, 90);
		studentAssignmentRepository.save(s417a42);
		originalGradeRepository.save(os417a42);
		StudentAssignment s418a42 = new StudentAssignment(s418, a42, 82);
		OriginalGrade os418a42 = new OriginalGrade(s418, a42, 82);
		studentAssignmentRepository.save(s418a42);
		originalGradeRepository.save(os418a42);
	}



}
