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
    public AssignmentAndStudentAssignmentContainer gradebookJSON(@RequestBody int courseId){
        Course course = courseRepository.findOne(courseId);

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(course.getId());

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

        //print just for testing
        for (StudentContainer currentStudentContainer : myArrayListOfStudentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("Grade on " + currentStudentAssignment.getStudent().getFirstName() + "'s " + currentStudentAssignment.getAssignment().getName() + ": " + currentStudentAssignment.getGrade());
            }
        }

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, allAssignments);
        return returnContainer;
    }

    public AssignmentAndStudentAssignmentContainer gradebook(Course course){

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(course.getId());

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

        //print just for testing
        for (StudentContainer currentStudentContainer : myArrayListOfStudentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("Grade on " + currentStudentAssignment.getStudent().getFirstName() + "'s " + currentStudentAssignment.getAssignment().getName() + ": " + currentStudentAssignment.getGrade());
            }
        }

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, allAssignments);
        return returnContainer;
    }

    //This endpoint returns the course given the course id. Needed to add students and add assignments from the new gradebook html page.
    @RequestMapping(path = "/getCurrentClass.json", method = RequestMethod.POST)
    public Course getCurrentClass(@RequestBody int courseId) {
        Course currentClass = courseRepository.findOne(courseId);
        return currentClass;
    }

    //Returns gradebook data for ALL classes (so that we can populate tables in bootstrap tabs)
    @RequestMapping(path = "/allGradebooks.json", method = RequestMethod.POST)
    public ArrayList<AssignmentAndStudentAssignmentContainer> allGradebooks(@RequestBody CourseListContainer courselistContainer) {
        ArrayList<Course> allCourses = courselistContainer.allcourses;
        ArrayList<AssignmentAndStudentAssignmentContainer> arrayListOfReturnContainers = new ArrayList<>();
        for (Course currentCourse : allCourses) {
            arrayListOfReturnContainers.add(gradebook(currentCourse));
        }
        return arrayListOfReturnContainers;
    }


    @RequestMapping(path = "/addAss.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addAss(@RequestBody Assignment assignment){
        //Need to add it for every student in the course!
        assignmentRepository.save(assignment);

        //find all students in course and make a studentAssignment
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(assignment.course);
        ArrayList<Student> allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        StudentAssignment newStudentAssignment;
        for (Student currentStudent : allStudentsInCourse) {
            newStudentAssignment = new StudentAssignment(currentStudent, assignment, 0);
            studentAssignmentRepository.save(newStudentAssignment);
        }

        ArrayList<Assignment> assignmentArrayList = assignmentRepository.findAllByCourseId(assignment.course.getId());
        ArrayList<StudentContainer> myArrayListOfStudentContainers = new ArrayList<>();

        for (Student currentStudent : allStudentsInCourse) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudent);
            StudentContainer newStudentContainer = new StudentContainer(currentStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, assignmentArrayList);
        return returnContainer;
    }

    @RequestMapping(path = "/addstudent.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addStudents(@RequestBody StudentCourse studentCourse){
        Student newStudent = studentCourse.getStudent();
        Course currentCourse = studentCourse.getCourse();



        studentRepository.save(newStudent);
        studentCourseRepository.save(studentCourse);

        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(studentCourse.course);
        ArrayList<Student> allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }
         ArrayList <Assignment> currentAssignments = assignmentRepository.findAllByCourseId(currentCourse.id);

        StudentAssignment newStudentAssignment;
        for (Assignment currentAssignment : currentAssignments) {
            newStudentAssignment = new StudentAssignment(newStudent, currentAssignment, 0);
            studentAssignmentRepository.save(newStudentAssignment);

        }

        ArrayList<Assignment> assignmentArrayList = assignmentRepository.findAllByCourseId(currentCourse.id);
        ArrayList<StudentContainer> myArrayListOfStudentContainers = new ArrayList<>();

        for (Student currentStudent : allStudentsInCourse) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudent);
            StudentContainer newStudentContainer = new StudentContainer(currentStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, assignmentArrayList);
        return returnContainer;
    }

    @RequestMapping(path = "/addGrade.json", method = RequestMethod.POST)
    public ArrayList<StudentAssignment> addGrade(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer){
        // ^ We're getting a container holding an assignment and a list of student containers. Each student container in the list has a student and a list of student assignments.
        // For each student, we need to get out their list of studentAssignments and update the studentAssignment for just that assignment.

        System.out.println("In addGrade endpoint!!!");
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        for (StudentContainer currentStudentContainer : studentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("GRDE: " + currentStudentAssignment.getGrade());
            }
        }

        Student currentStudent;

        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            //find the index of the assignment we want

            int indexOfAssignment = -1;
            for (int index = 0; index < currentStudentContainer.getStudentAssignments().size(); index++) {
                if (currentStudentContainer.getStudentAssignments().get(index).getAssignment().getId() == currentAssignment.getId()) {
                    indexOfAssignment = index;
                }
            }

            System.out.println("This should be the NEW GRADE that's about to be saved: " + currentStudentContainer.getStudentAssignments().get(indexOfAssignment).getGrade());

           int newGrade = currentStudentContainer.getStudentAssignments().get(indexOfAssignment).getGrade();
            retrievedStudentAssignment.setGrade(newGrade);
            studentAssignmentRepository.save(retrievedStudentAssignment);

        }


        //return all StudentAssignments for this assignment

            ArrayList<StudentAssignment> listOfStudentAssmtsByAssmt = studentAssignmentRepository.findAllByAssignment(currentAssignment);

        return listOfStudentAssmtsByAssmt ;
    }


    @RequestMapping(path="/curveFlat.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addFlatCurve(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        System.out.println("In flat curve endpoint!!!");

        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        System.out.println("The assignment we received is: " + currentAssignment.getName());
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            //find the index of the assignment we want

//            int indexOfAssignment = -1;
//            for (int index = 0; index < currentStudentContainer.getStudentAssignments().size(); index++) {
//                if (currentStudentContainer.getStudentAssignments().get(index).getAssignment().getId() == currentAssignment.getId()) {
//                    indexOfAssignment = index;
//                }
//            }

//            int newGrade = currentStudentContainer.getStudentAssignments().get(indexOfAssignment).getGrade();
////            retrievedStudentAssignment.setGrade(newGrade);
////            studentAssignmentRepository.save(retrievedStudentAssignment);
//
//            System.out.println("This is the grade for **" + currentStudent.getFirstName() + "** that's about to be added to the arrayList: " + newGrade);
//            oldGradeArrayList.add(newGrade);
            System.out.println("This is the grade for **" + currentStudent.getFirstName() + "** that's about to be added to the arrayList: " + retrievedStudentAssignment.getGrade());
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());

        }

        System.out.print("*** Grades in oldGradeArrayList: ");
        for (int currentGrade : oldGradeArrayList) {
            System.out.print(currentGrade + " ");
        }
        System.out.println();

        System.out.print("*** Grades in updatedGrades after curveFlat: ");

        ArrayList<Integer> updatedGrades =  myCurver.curveFlat(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            System.out.print(retrievedStudentAssignment1.getGrade() + " ");
            //CHECK: will this make a new one? Or update old one? Should update old one.
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }
        System.out.println();




        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        ArrayList<StudentContainer> myArrayListOfStudentContainers = new ArrayList<>();
        for (Student thisStudent : studentsInCourse) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(thisStudent);
            StudentContainer newStudentContainer = new StudentContainer(thisStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }



        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, allAssignments);
        return returnContainer;

    }
    @RequestMapping(path="/curveAsPercentageOfHighestGrade.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer curveAsPercentageOfHighestGrade(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        System.out.println("In highest percentage curve endpoint!!!");

        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        System.out.println("The assignment we received is: " + currentAssignment.getName());
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);

            System.out.println("This is the grade for **" + currentStudent.getFirstName() + "** that's about to be added to the arrayList: " + retrievedStudentAssignment.getGrade());
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());

        }

        System.out.print("*** Grades in oldGradeArrayList: ");
        for (int currentGrade : oldGradeArrayList) {
            System.out.print(currentGrade + " ");
        }
        System.out.println();

        System.out.print("*** Grades in updatedGrades after curve of highestpercentage: ");

        ArrayList<Integer> updatedGrades =  myCurver.curveAsPercentageOfHighestGrade(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            System.out.print(retrievedStudentAssignment1.getGrade() + " ");
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }
        System.out.println();


        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        ArrayList<StudentContainer> myArrayListOfStudentContainers = new ArrayList<>();
        for (Student thisStudent : studentsInCourse) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(thisStudent);
            StudentContainer newStudentContainer = new StudentContainer(thisStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }



        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, allAssignments);
        return returnContainer;

    }

    @RequestMapping(path="/curveByTakingRoot.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer curveByTakingRoot(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        System.out.println("In square root curve endpoint!!!");

        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        System.out.println("The assignment we received is: " + currentAssignment.getName());
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);

            System.out.println("This is the grade for **" + currentStudent.getFirstName() + "** that's about to be added to the arrayList: " + retrievedStudentAssignment.getGrade());
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());

        }

        System.out.print("*** Grades in oldGradeArrayList: ");
        for (int currentGrade : oldGradeArrayList) {
            System.out.print(currentGrade + " ");
        }
        System.out.println();

        System.out.print("*** Grades in updatedGrades after curve of highestpercentage: ");

        ArrayList<Integer> updatedGrades =  myCurver.curveByTakingRoot(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            System.out.print(retrievedStudentAssignment1.getGrade() + " ");
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }
        System.out.println();


        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        ArrayList<StudentContainer> myArrayListOfStudentContainers = new ArrayList<>();
        for (Student thisStudent : studentsInCourse) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(thisStudent);
            StudentContainer newStudentContainer = new StudentContainer(thisStudent,allMyStudentAssignments);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }



        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, allAssignments);
        return returnContainer;

    }







}
