package com.tiy;

import org.apache.commons.logging.Log;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;

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
        public LoginContainer login(@RequestBody emailAndPasswordContainer emailAndPasswordContainer){
        Teacher returnTeacher;
        LoginContainer loginContainer;
        String email = emailAndPasswordContainer.email;
        String password = emailAndPasswordContainer.password;
        returnTeacher = teacherRepository.findByEmailAndPassword(email,password);
        if(returnTeacher == null){
            loginContainer = new LoginContainer("User not found", null,null);

        }else{
            loginContainer = new LoginContainer(null,returnTeacher,courseRepository.findAllByTeacher(returnTeacher));
        }



        return loginContainer;
    }
    @RequestMapping(path = "/addclass.json", method = RequestMethod.POST)
    public ArrayList<Course> addcourse(@RequestBody Course course){
        courseRepository.save(course);


        ArrayList<Course> courseArrayList = new ArrayList<Course>();

        Iterable<Course> courseIterable = courseRepository.findAllByTeacher(course.getTeacher());

        for(Course thisClass : courseIterable){
            courseArrayList.add(thisClass);
        }

        return courseArrayList;
    }
    @RequestMapping(path = "/gradebook.json", method = RequestMethod.POST)
    public AssignmentStudentContainer assignmentStudentContainer (@RequestBody Course course){
//        assignmentRepository.save(assignmentRepository.findAllByCourseId(course.getId()));
//        studentRepository.save(studentRepository.findAllByCourse(course));

        ArrayList<Assignment> assignmentArrayList = assignmentRepository.findAllByCourseId(course.getId());

        ArrayList<Student> studentArrayList = studentRepository.findAllByCourse(course);

        AssignmentStudentContainer assignmentStudentContainer = new AssignmentStudentContainer(studentArrayList, assignmentArrayList);

        return assignmentStudentContainer;
    }
    @RequestMapping(path = "/addAss.json", method = RequestMethod.POST)
    public ArrayList<Assignment> addAss(@RequestBody Assignment assignment){
        assignmentRepository.save(assignment);
        Course course = new Course();
        ArrayList<Assignment> assignmentArrayList = new ArrayList<>();

        Iterable<Assignment> assignmentIterable = assignmentRepository.findAll();

        for(Assignment addAssignment: assignmentIterable){
            assignmentArrayList.add(addAssignment);
        }
        return assignmentArrayList;
    }
    @RequestMapping(path = "/addstudent.json", method = RequestMethod.POST)
    public ArrayList<Student> addStudents(@RequestBody Student newStudent){
        studentRepository.save(newStudent);
//
        Iterable<Student> studentIterable = studentRepository.findAll();
        ArrayList<Student> studentArrayList = studentRepository.findAllByCourse(newStudent.course);


        for(Student student: studentIterable){
            studentArrayList.add(student);
        }
        return studentArrayList;
    }








}
