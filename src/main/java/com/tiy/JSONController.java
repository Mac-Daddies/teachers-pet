package com.tiy;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@RestController

public class JSONController {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentAssignmentRepository studentAssignmentRepository;

    @RequestMapping(path = "/register.json", method = RequestMethod.POST)
    public LoginContainer register(@RequestBody Teacher newTeacher){
        teacherRepository.save(newTeacher);

        Teacher retrievedTeacher = newTeacher;

        LoginContainer loginContainer;

        if(newTeacher == null){
            loginContainer = new LoginContainer("Error adding you!", null, null);
        }else{
            loginContainer = new LoginContainer(null,retrievedTeacher,courseRepository.findAllByTeacher(retrievedTeacher));
        }

        return loginContainer;
    }

    @RequestMapping(path = "/login.json", method = RequestMethod.POST)
        public Teacher login(@RequestBody emailAndPasswordContainer emailAndPasswordContainer){
        Teacher returnTeacher;
        String email = emailAndPasswordContainer.email;
        String password = emailAndPasswordContainer.password;
        returnTeacher = teacherRepository.findByEmailAndPassword(email,password);

        System.out.println(returnTeacher.email + returnTeacher.password);

        return  returnTeacher;
    }








}
