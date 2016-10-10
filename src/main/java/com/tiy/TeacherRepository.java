package com.tiy;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by jessicatracy on 10/7/16.
 */
public interface TeacherRepository extends CrudRepository<Teacher, Integer> {

    Teacher findByEmailAndPassword(String email, String password);
}
