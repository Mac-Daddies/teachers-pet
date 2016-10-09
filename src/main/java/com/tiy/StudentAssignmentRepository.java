package com.tiy;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/7/16.
 */
public interface StudentAssignmentRepository extends CrudRepository<StudentAssignment, Integer> {
    ArrayList<StudentAssignment> findAllByStudent(Student student);
    ArrayList<StudentAssignment> findAllByAssignment(Assignment assignment);
}
