package com.tiy;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/7/16.
 */
public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
    ArrayList<Assignment> findAllByCourseId(int courseId);
}
