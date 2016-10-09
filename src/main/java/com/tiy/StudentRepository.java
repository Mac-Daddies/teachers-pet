package com.tiy;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/7/16.
 */
public interface StudentRepository extends CrudRepository<Student, Integer> {
    ArrayList<Student> findAllByCourse(Course course);
}
