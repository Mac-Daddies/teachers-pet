package com.tiy;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/7/16.
 */
public interface StudentCourseRepository extends CrudRepository<StudentCourse, Integer> {
    ArrayList<StudentCourse> findAllByStudent(Student student);
    ArrayList<StudentCourse> findAllByCourse(Course course);
}
