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

    @Autowired
    StudentCourseRepository studentCourseRepository;

    CurveMyScores myCurver = new CurveMyScores();

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
//    @RequestMapping(path = "/gradebook.json", method = RequestMethod.POST)
//    public AssignmentStudentContainer assignmentStudentContainer (@RequestBody Course course){
////        assignmentRepository.save(assignmentRepository.findAllByCourseId(course.getId()));
////        studentRepository.save(studentRepository.findAllByCourse(course));
//
//        ArrayList<Assignment> assignmentArrayList = assignmentRepository.findAllByCourseId(course.getId());
//
//        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(course);
//
//        ArrayList<Student> studentArrayList = new ArrayList<>();
//        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
//            studentArrayList.add(currentStudentCourse.getStudent());
//        }
//
//        AssignmentStudentContainer assignmentStudentContainer = new AssignmentStudentContainer(studentArrayList, assignmentArrayList);
//
//        return assignmentStudentContainer;
//    }
    @RequestMapping(path = "/gradebook.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer assignmentStudentContainer (@RequestBody Course course){

        ArrayList<StudentContainer> myArrayListOfStudentContainers= new ArrayList<>();
        //For each student in the course, make  a student container
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(course);
        ArrayList<Student> studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        for (Student currentStudent : studentArrayList) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudent);
            StudentContainer newStudentContainer = new StudentContainer(currentStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers);
        return returnContainer;
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
    public ArrayList<Student> addStudents(@RequestBody StudentCourse studentCourse){
        Student newStudent = studentCourse.getStudent();
        Course currentCourse = studentCourse.getCourse();

        studentRepository.save(newStudent);

        studentCourseRepository.save(studentCourse);


        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(currentCourse);


//        for(Student student: studentIterable){
//            studentArrayList.add(student);
//        }

        ArrayList<Student> studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }


        return studentArrayList;
    }

    @RequestMapping(path = "/addGrade.json", method = RequestMethod.POST)
    public ArrayList<StudentAssignment> addGrade(@RequestBody StudentAssignment studentAssignment){
        // ^ We're getting a container with the student, the assignment, and the grade

        //We should check that the grade here is valid!

        //check to see if there is already a grade for that student on that assignment. If yes, delete so that we can overwrite it.
        ArrayList<StudentAssignment> allStudentAssmtsByStudentAndAssmt = studentAssignmentRepository.findAllByStudentAndAssignment(studentAssignment.getStudent(), studentAssignment.getAssignment());
        if (allStudentAssmtsByStudentAndAssmt.size() > 0) {
            studentAssignmentRepository.delete(allStudentAssmtsByStudentAndAssmt.get(0));
        }
        //save the grade for that student-assignment connection
        studentAssignmentRepository.save(studentAssignment);

        //return all StudentAssignments for this assignment
        ArrayList<StudentAssignment> listOfStudentAssmtsByAssmt = studentAssignmentRepository.findAllByAssignment(studentAssignment.getAssignment());
        return listOfStudentAssmtsByAssmt;
    }








}
