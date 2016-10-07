package com.tiy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeachersPetApplicationTests {
	@Autowired
	TeacherRepository teacherRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testInsertTeacher() {
		Teacher testTeacher = new Teacher("test_first_name2", "test_last_name1", "test_email2", "test_password", "test_school");
		teacherRepository.save(testTeacher);

		Teacher retrievedTeacher = teacherRepository.findOne(testTeacher.getId());
		assertNotNull(retrievedTeacher);

		teacherRepository.delete(testTeacher);
		retrievedTeacher = teacherRepository.findOne(testTeacher.getId());
		assertNull(retrievedTeacher);
	}

}
