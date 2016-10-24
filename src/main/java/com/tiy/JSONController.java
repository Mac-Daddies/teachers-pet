package com.tiy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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

    @Autowired
    OriginalGradeRepository originalGradeRepository;

    CurveMyScores myCurver = new CurveMyScores();
    EmailCustomContent myEmailer = new EmailCustomContent();



    @RequestMapping(path = "/register.json", method = RequestMethod.POST)
    public LoginContainer register(@RequestBody Teacher newTeacher, HttpSession session){
        teacherRepository.save(newTeacher);

        Teacher retrievedTeacher = newTeacher;
        LoginContainer loginContainer;

        if(newTeacher == null){
            loginContainer = new LoginContainer("Error adding you!", null, null);
        }else{
            loginContainer = new LoginContainer(null,retrievedTeacher,courseRepository.findAllByTeacher(retrievedTeacher));
            session.setAttribute("loggedInTeacher", loginContainer.getTeacher());
        }

        return loginContainer;
    }

    @RequestMapping(path = "/login.json", method = RequestMethod.POST)
        public LoginContainer login(@RequestBody emailAndPasswordContainer emailAndPasswordContainer, HttpSession session){
        Teacher returnTeacher;
        LoginContainer loginContainer;
        String email = emailAndPasswordContainer.email;
        String password = emailAndPasswordContainer.password;
        returnTeacher = teacherRepository.findByEmailAndPassword(email,password);
        if(returnTeacher == null){
            loginContainer = new LoginContainer("User not found", null,null);

        }else{
            loginContainer = new LoginContainer(null,returnTeacher,courseRepository.findAllByTeacher(returnTeacher));
            session.setAttribute("loggedInTeacher", loginContainer.getTeacher());
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
    public AssignmentAndStudentAssignmentContainer gradebookJSON(@RequestBody int courseId){
        System.out.println("IN GRADEBOOK.................................................");
        Course course = courseRepository.findOne(courseId);
        System.out.println("Course ID: " + courseId);
        System.out.println("Course retrieved from db: " + course.getName());

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(course.getId());

        //Get a list of all students in the course
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(course);
        ArrayList<Student> studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        System.out.print("THESE are the students in allStudentCoursesByCourse:\n*");
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            System.out.print(" " + studentCourse.getStudent().getFirstName() + ",");
        }
        System.out.println();

        System.out.print("THESE are the students in studentArrayList:\n*");
        for (Student student : studentArrayList) {
            System.out.print(" " + student.getFirstName() + ",");
        }
        System.out.println();

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentArrayList);

        //print just for testing
        for (StudentContainer currentStudentContainer : myArrayListOfStudentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("Grade on " + currentStudentAssignment.getStudent().getFirstName() + "'s " + currentStudentAssignment.getAssignment().getName() + ": " + currentStudentAssignment.getGrade());
            }
        }

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        System.out.print("Again, THESE are the students in studentArrayList:\n*");
        for (Student student : studentArrayList) {
            System.out.print(" " + student.getFirstName() + ",");
        }
        System.out.println();

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentArrayList);
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
    }

    @RequestMapping(path = "/graph.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer graphJSON(@RequestBody int courseId){
        Course course = courseRepository.findOne(courseId);

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(course.getId());

        //Get a list of all students in the course
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(course);
        ArrayList<Student> studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentArrayList);

        //print just for testing
        for (StudentContainer currentStudentContainer : myArrayListOfStudentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("Grade on " + currentStudentAssignment.getStudent().getFirstName() + "'s " + currentStudentAssignment.getAssignment().getName() + ": " + currentStudentAssignment.getGrade());
            }
        }

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentArrayList);
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
    }

    /**This method makes a list of student containers. Each student container hold a student in the course and
     * an arraylist of all of that student's studentAssignments.
     * (Returns the first parameter needed in the AssignmentAndStudentAssignmentContainer that is returned in every
     * method that updates the gradebook) */
    public ArrayList<StudentContainer> prepareArrayListOfStudentContainersToReturn(ArrayList<Student> arrayListOfStudents) {
        //first order the students by last name IN A NEW ARRAYLIST
        ArrayList<Student> orderedArrayListOfStudents = sortStudentsAlphabeticallyByLastName(arrayListOfStudents);

        ArrayList<StudentContainer> myArrayListOfStudentContainers= new ArrayList<>();

        //For each student in the course, make  a student container, and add it to the arrayList of student containers
        for (Student currentStudent : orderedArrayListOfStudents) {
            //find all of their student assignments
            ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudent);
            //ORDER THE ASSIGNMENTS BY DATE (so it will display them in the correct order on front end)
            allMyStudentAssignments = orderStudentAssignmentsByDate(allMyStudentAssignments);

            //make an arraylist of all of the student's grades on all of their student assignments
            ArrayList<Integer> myAssignmentGrades = new ArrayList<>();
            for (StudentAssignment currentStudentAssignment : allMyStudentAssignments) {
                myAssignmentGrades.add(currentStudentAssignment.getGrade());
            }
            //get the average of all the student's grades
            int average = myCurver.getAverage(myAssignmentGrades);
            //save the student, list of assignments, and average to a new student container and add to arraylist to return
            StudentContainer newStudentContainer = new StudentContainer(currentStudent, allMyStudentAssignments, average);
            myArrayListOfStudentContainers.add(newStudentContainer);
        }

        return myArrayListOfStudentContainers;
    }


    /** This method makes a list of AssignmentAndAverageContainers. Each container object holds an assignment
     * and the average score of every student on that assignment.
     * (Returns the second parameter needed in the AssignmentAndStudentAssignmentContainer that is returned in
     * every method that updates the gradebook) */
    public ArrayList<AssignmentAndAverageContainer> prepareArrayListOfAssignmentAndAverageContainerToReturn(ArrayList<Assignment> arrayListOfAssignments, ArrayList<Student> arrayListOfStudents) {
        System.out.println("IN PREPAREARRAYLISTOFBLAHBLAH.............................");
//        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = new ArrayList<>();
//
//        for (Assignment currentAssignment : arrayListOfAssignments) {
//            AssignmentAndAverageContainer currentAssignmentAndAverageContainer;
//            ArrayList<Integer> gradesToCurve = new ArrayList<>();
//            for (Student currentStudent : arrayListOfStudents) {
//                StudentAssignment currentStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
//                int currentGrade = currentStudentAssignment.getGrade();
//                gradesToCurve.add(currentGrade);
//            }
//            int average = myCurver.getAverage(gradesToCurve);
//            currentAssignmentAndAverageContainer = new AssignmentAndAverageContainer(currentAssignment, average);
//            myAssignmentAndAverageContainers.add(currentAssignmentAndAverageContainer);
//        }


        System.out.print("THESE are the students in arrayListOfStudents:\n*");
        for (Student student : arrayListOfStudents) {
            System.out.print(" " + student.getFirstName() + ",");
        }
        System.out.println();

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = new ArrayList<>();

        for (Assignment currentAssignment : arrayListOfAssignments) {
            System.out.println("* current assignment: " + currentAssignment.getName());
            AssignmentAndAverageContainer currentAssignmentAndAverageContainer;
            ArrayList<Integer> gradesToCurve = new ArrayList<>();
            for (Student currentStudent : arrayListOfStudents) {
                StudentAssignment currentStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
                if (currentStudentAssignment != null) {
                    System.out.println("** current student assignment: " + currentStudentAssignment.getStudent() + ", " + currentStudentAssignment.getAssignment() + ", " + currentStudentAssignment.getGrade());
                    int currentGrade = currentStudentAssignment.getGrade();
                    gradesToCurve.add(currentGrade);
                    System.out.println("** Added to grades list");
                }
            }
            int average = myCurver.getAverage(gradesToCurve);
            System.out.println("* average of grades on " + currentAssignment.getName() + ": " + average);
            currentAssignmentAndAverageContainer = new AssignmentAndAverageContainer(currentAssignment, average);
            myAssignmentAndAverageContainers.add(currentAssignmentAndAverageContainer);
        }

        return myAssignmentAndAverageContainers;
    }

    public ArrayList<Assignment> orderAssignmentsByDate(ArrayList<Assignment> allAssignments) {

        for (int counter = 0; counter < allAssignments.size() - 1; counter++) {
            for (int dateStringIndex = 0; dateStringIndex < 10; dateStringIndex++) {
                //If at any index the value is greater than that of the next due date, switch the order, and then break to the next assignment.
                if (Integer.valueOf(allAssignments.get(counter).getDueDate().charAt(dateStringIndex)) != Integer.valueOf(allAssignments.get(counter + 1).getDueDate().charAt(dateStringIndex))) {
                    if (Integer.valueOf(allAssignments.get(counter).getDueDate().charAt(dateStringIndex)) < Integer.valueOf(allAssignments.get(counter + 1).getDueDate().charAt(dateStringIndex))) {
                        //We know we don't want to switch, so break!
                        break;
                    } else {
                        //else it must be larger, so switch them and break!
                        Assignment temporary = allAssignments.get(counter);
                        allAssignments.set(counter, allAssignments.get(counter + 1));
                        allAssignments.set(counter + 1, temporary);
                        counter = -1;
                        break;
                    }
                }
            }
        }
        return allAssignments;
    }

    public ArrayList<StudentAssignment> orderStudentAssignmentsByDate(ArrayList<StudentAssignment> allStudentAssignments) {

        for (int counter = 0; counter < allStudentAssignments.size() - 1; counter++) {
            for (int dateStringIndex = 0; dateStringIndex < 10; dateStringIndex++) {
                //If at any index the value is greater than that of the next due date, switch the order, and then break to the next assignment.
                if (Integer.valueOf(allStudentAssignments.get(counter).getAssignment().getDueDate().charAt(dateStringIndex)) != Integer.valueOf(allStudentAssignments.get(counter + 1).getAssignment().getDueDate().charAt(dateStringIndex))) {
                    if (Integer.valueOf(allStudentAssignments.get(counter).getAssignment().getDueDate().charAt(dateStringIndex)) < Integer.valueOf(allStudentAssignments.get(counter + 1).getAssignment().getDueDate().charAt(dateStringIndex))) {
                        //We know we don't want to switch, so break!
                        break;
                    } else {
                        //else it must be larger, so switch them and break!
                        StudentAssignment temporary = allStudentAssignments.get(counter);
                        allStudentAssignments.set(counter, allStudentAssignments.get(counter + 1));
                        allStudentAssignments.set(counter + 1, temporary);
                        counter = -1;
                        break;
                    }
                }
            }
        }
        return allStudentAssignments;
    }

    public ArrayList<Student> sortStudentsAlphabeticallyByLastName(ArrayList<Student> students) {
        //Make a new arraylist so we don't actually alter the student list when we remove
        ArrayList<Student> holderOfStudents = students;
        ArrayList<String> lastNames = new ArrayList<>();
        for (Student currentStudent : holderOfStudents) {
            lastNames.add(currentStudent.getLastName());
        }
        java.util.Collections.sort(lastNames);

//        for (String lastName : lastNames) {
//            System.out.println("* " + lastName);
//        }
        //order student assignments in same order as lastNAmes are in
        ArrayList<Student> orderedStudents = new ArrayList<>();

        for (String lastName : lastNames) {
            int currentIndex = 0;
            while (!(holderOfStudents.get(currentIndex).getLastName().equals(lastName))) {
                currentIndex++;
            }
            //when it gets out of loop, it means they are the same, so add to the ordered list, remove that element from the list (in case of duplicate last names), and move to the next last name!
            orderedStudents.add(holderOfStudents.get(currentIndex));
            holderOfStudents.remove(holderOfStudents.get(currentIndex));
        }
        return orderedStudents;
    }

    public AssignmentAndStudentAssignmentContainer gradebook(Course course){

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(course.getId());

        //For each student in the course, make  a student container
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(course);
        ArrayList<Student> studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentArrayList);

        //print just for testing
        for (StudentContainer currentStudentContainer : myArrayListOfStudentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println("Grade on " + currentStudentAssignment.getStudent().getFirstName() + "'s " + currentStudentAssignment.getAssignment().getName() + ": " + currentStudentAssignment.getGrade());
            }
        }

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentArrayList);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
        return returnContainer;
    }

    //This endpoint returns the course given the course id. Needed to add students and add assignments from the new gradebook html page.
    @RequestMapping(path = "/getCurrentClass.json", method = RequestMethod.POST)
    public Course getCurrentClass(@RequestBody int courseId) {
        Course currentClass = courseRepository.findOne(courseId);
        return currentClass;
    }

    //Returns gradebook data for ALL classes (so that we can populate tables in bootstrap tabs)
//    @RequestMapping(path = "/allGradebooks.json", method = RequestMethod.POST)
//    public ArrayList<AssignmentAndStudentAssignmentContainer> allGradebooks(@RequestBody CourseListContainer courselistContainer) {
//        ArrayList<Course> allCourses = courselistContainer.allcourses;
//        ArrayList<AssignmentAndStudentAssignmentContainer> arrayListOfReturnContainers = new ArrayList<>();
//        for (Course currentCourse : allCourses) {
//            arrayListOfReturnContainers.add(gradebook(currentCourse));
//        }
//        return arrayListOfReturnContainers;
//    }

    @RequestMapping(path = "/getTeacherWhoIsLoggedIn.json", method = RequestMethod.POST)
    public TeacherAndCourseContainer getTeacherWhoIsLoggedIn(@RequestBody int teacherId) {
        Teacher currentTeacher = teacherRepository.findOne(teacherId);
        ArrayList<Course> courses = courseRepository.findAllByTeacher(currentTeacher);
        TeacherAndCourseContainer teacherAndCourseContainer = new TeacherAndCourseContainer(currentTeacher, courses);
        return teacherAndCourseContainer;
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
            newStudentAssignment = new StudentAssignment(currentStudent, assignment, -1);
            studentAssignmentRepository.save(newStudentAssignment);
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(assignment.course.getId());

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);

//        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = getAssignmentAndAverageContainerToReturn(allAssignments, studentArrayList);

//        System.out.println("*****IN ADDASS.JSON - This is the list of assignments I'm sending back: " );
//        for (AssignmentAndAverageContainer currentContainer : myAssignmentAndAverageContainers) {
//            System.out.println(currentContainer.getAssignment().getName());
//        }
//        System.out.println();

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
        return returnContainer;
    }

    @RequestMapping(path = "/deleteAss.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer deleteAss(@RequestBody Assignment assignment){
        //delete all studentAssignments attached to that assignment
        ArrayList<StudentAssignment> allStudentAssignmentsByAssignment = studentAssignmentRepository.findAllByAssignment(assignment);
        for (StudentAssignment studentAssignment : allStudentAssignmentsByAssignment) {
            studentAssignmentRepository.delete(studentAssignment);
        }

        //delete all original grades attached to that assignment
        ArrayList<OriginalGrade> allOriginalGradesByAssignment = originalGradeRepository.findAllByAssignment(assignment);
        for (OriginalGrade originalGrade : allOriginalGradesByAssignment) {
            originalGradeRepository.delete(originalGrade);
        }

        //delete assignment
        Assignment retrievedAssignment = assignmentRepository.findOne(assignment.getId());
        assignmentRepository.delete(retrievedAssignment);

        ArrayList<Student> allStudents = new ArrayList<>();
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(assignment.getCourse());
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudents.add(studentCourse.getStudent());
        }

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudents);

        //repopulate (until I find out why it's deleting)
        allStudents = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudents.add(studentCourse.getStudent());
        }

        ArrayList<Assignment> allAssignmentsInCourse = assignmentRepository.findAllByCourseId(assignment.getCourse().getId());
        allAssignmentsInCourse = orderAssignmentsByDate(allAssignmentsInCourse);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignmentsInCourse, allStudents);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
        return returnContainer;
    }

    @RequestMapping(path = "/addstudent.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addStudent(@RequestBody StudentCourse studentCourse){
        Student newStudent = studentCourse.getStudent();
        Course currentCourse = studentCourse.getCourse();

        studentRepository.save(newStudent);
        studentCourseRepository.save(studentCourse);

        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(studentCourse.course);
        ArrayList<Student> allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        //If this student is the first student added to the course, the order of assignments does not matter (only one ordering)
        if (allStudentsInCourse.size() == 1) {
            ArrayList<Assignment> currentAssignments = assignmentRepository.findAllByCourseId(currentCourse.id);
        }

        System.out.println("Order of assignments:");
        //If there are already students, make sure this student's assignments are saved in the same order as the ones that are already in there!!
        ArrayList<Assignment> allAssignments = new ArrayList<>();
        ArrayList<StudentAssignment> studentAssignmentsOfStudentAlreadyInTable = studentAssignmentRepository.findAllByStudent(allStudentsInCourse.get(0));
        int counter = 1;
        for (StudentAssignment currentStudentAssignment : studentAssignmentsOfStudentAlreadyInTable) {
            allAssignments.add(currentStudentAssignment.getAssignment());
            System.out.println(counter + ". " + currentStudentAssignment.getAssignment().getName());
            counter++;
        }

        //Give the new student each assignment that is already in the course (give a grade of zero for now)
        StudentAssignment newStudentAssignment;
        for (Assignment currentAssignment : allAssignments) {
            newStudentAssignment = new StudentAssignment(newStudent, currentAssignment, -1);
            studentAssignmentRepository.save(newStudentAssignment);

        }

//        ArrayList<Assignment> assignmentArrayList = assignmentRepository.findAllByCourseId(currentCourse.id);
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
        return returnContainer;
    }

    @RequestMapping(path = "/deleteStudent.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer deleteStudent(@RequestBody Student studentToDelete){
        ArrayList<StudentCourse> thisStudentCourse = studentCourseRepository.findAllByStudent(studentToDelete);

        //delete all studentAssignments attached to that student
        ArrayList<StudentAssignment> studentAssignments = studentAssignmentRepository.findAllByStudent(studentToDelete);
        for (StudentAssignment studentAssignment : studentAssignments) {
            studentAssignmentRepository.delete(studentAssignment);
        }

        //delete all originalGrades attached to that student
        ArrayList<OriginalGrade> originalGrades = originalGradeRepository.findAllByStudent(studentToDelete);
        for (OriginalGrade originalGrade : originalGrades) {
            originalGradeRepository.delete(originalGrade);
        }

        //delete all studentCourses attached to that student
        ArrayList<StudentCourse> studentCourses = studentCourseRepository.findAllByStudent(studentToDelete);
        for (StudentCourse studentCourse : studentCourses) {
            studentCourseRepository.delete(studentCourse);
        }

        //delete student
        Student retrievedStudent = studentRepository.findOne(studentToDelete.getId());
        studentRepository.delete(retrievedStudent);

        ArrayList<Student> allStudents = new ArrayList<>();
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(thisStudentCourse.get(0).getCourse());
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudents.add(studentCourse.getStudent());
        }

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudents);

        //repopulate (until I find out why it's deleting)
        allStudents = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudents.add(studentCourse.getStudent());
        }

        ArrayList<Assignment> allAssignmentsInCourse = assignmentRepository.findAllByCourseId(thisStudentCourse.get(0).getCourse().getId());
        allAssignmentsInCourse = orderAssignmentsByDate(allAssignmentsInCourse);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignmentsInCourse, allStudents);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
        return returnContainer;
    }


        @RequestMapping(path = "/addGrades.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addGrade(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer){
        // ^ We're getting a container holding an assignment and a list of student containers. Each student container in the list has a student and a list of student assignments.
        // For each student, we need to get out their list of studentAssignments and update the studentAssignment for just that assignment.

        System.out.println("In addGrades endpoint!!!");
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        for (StudentContainer currentStudentContainer : studentContainers) {
            for (StudentAssignment currentStudentAssignment : currentStudentContainer.getStudentAssignments()) {
                System.out.println(currentStudentContainer.getStudent().getFirstName() + "'s grade on " + currentStudentAssignment.getAssignment().getName()+ ": " + currentStudentAssignment.getGrade());
            }
        }

        Student currentStudent;
        ArrayList<Student> allStudents = new ArrayList<>();

        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            allStudents.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            //find the index of the assignment we want

            int indexOfAssignment = -1;
            for (int index = 0; index < currentStudentContainer.getStudentAssignments().size(); index++) {
                if (currentStudentContainer.getStudentAssignments().get(index).getAssignment().getId() == currentAssignment.getId()) {
                    indexOfAssignment = index;
                }
            }

            System.out.println("This should be the NEW GRADE that's about to be saved for assignment " + currentAssignment.getName() + " for " + currentStudentContainer.getStudent().getFirstName() + ": " + currentStudentContainer.getStudentAssignments().get(indexOfAssignment).getGrade());

            int newGrade = currentStudentContainer.getStudentAssignments().get(indexOfAssignment).getGrade();
            retrievedStudentAssignment.setGrade(newGrade);
            studentAssignmentRepository.save(retrievedStudentAssignment);

        }

        //Store original grades (only if there are no original grades already in the db)
        ArrayList<StudentAssignment> allStudentAssignmentsForThisAssignment = studentAssignmentRepository.findAllByAssignment(currentAssignment);
        for (StudentAssignment currentStudentAssignment : allStudentAssignmentsForThisAssignment) {
            OriginalGrade retrievedOriginalGrade = originalGradeRepository.findByStudentAndAssignment(currentStudentAssignment.getStudent(), currentStudentAssignment.getAssignment());
            //if the retrieved OriginalGrade object is null, it means there is no originalGrade for that student on that assignment, so save one.
            if (retrievedOriginalGrade == null) {
                if (currentStudentAssignment.getGrade() != -1) {
                    OriginalGrade newOriginalGrade = new OriginalGrade(currentStudentAssignment.getStudent(), currentStudentAssignment.getAssignment(), currentStudentAssignment.getGrade());
                    originalGradeRepository.save(newOriginalGrade);
                    System.out.println("Original grade stored for " + newOriginalGrade.getStudent().getFirstName() + " on " + newOriginalGrade.getAssignment().getName() + ": " + newOriginalGrade.getGrade());
                } else {
                    System.out.println("Cannot store original grade for " + currentStudentAssignment.getStudent().getFirstName() + " on " + currentStudentAssignment.getAssignment().getName() + " because no grade was entered (-1).");
                }
            }
        }


//        ArrayList<StudentAssignment> listOfStudentAssmtsByAssmt = studentAssignmentRepository.findAllByAssignment(currentAssignment);

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudents);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        allStudents = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            allStudents.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudents);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
    }

    @RequestMapping(path="/addExtraCredit.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addExtraCredit(@RequestBody ExtraCreditContainer extraCreditContainer) {
        System.out.println("In addExtraCredit endpoint!!!");

        Assignment currentAssignment = extraCreditContainer.getAssignment();
        System.out.println("The assignment we received is: " + currentAssignment.getName());
        ArrayList<StudentContainer> studentContainers = extraCreditContainer.getStudentContainers();
        int extraCreditAmount = extraCreditContainer.getExtraCreditAmount();

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

        System.out.print("*** Grades in updatedGrades after curveFlat: ");

        ArrayList<Integer> updatedGrades =  myCurver.curveByAddingExtraCredit(oldGradeArrayList, extraCreditAmount);
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
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
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
//            System.out.println("This is the grade for **" + currentStudent.getFirstName() + "** that's about to be added to the arrayList: " + retrievedStudentAssignment.getGrade());
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());

        }

//        System.out.print("*** Grades in oldGradeArrayList: ");
//        for (int currentGrade : oldGradeArrayList) {
//            System.out.print(currentGrade + " ");
//        }
//        System.out.println();

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
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

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
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);
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
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
    }

//    @RequestMapping(path = "/getAssignmentAverage.json", method = RequestMethod.POST)
//    public int getAverage(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
//        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
//
//        ArrayList<StudentContainer> listOfStudentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();
//        ArrayList<Integer> gradesToAverage = new ArrayList<>();
//        for (StudentContainer currentStudentContainer : listOfStudentContainers) {
//            Student currentStudent = currentStudentContainer.getStudent();
//            StudentAssignment currentStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
//            gradesToAverage.add(currentStudentAssignment.getGrade());
//        }
//        int average = myCurver.getAverage(gradesToAverage);
//
//        return average;
//    }

    @RequestMapping(path = "/getOriginalGrades.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer getOriginalGrades(@RequestBody Assignment assignment) {
        ArrayList<OriginalGrade> originalGrades = originalGradeRepository.findAllByAssignment(assignment);

        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(assignment.getCourse());
        ArrayList<Student> allStudentsInCourse = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(studentCourse.getStudent());
        }

        for (Student currentStudent : allStudentsInCourse) {
            StudentAssignment myStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, assignment);
            OriginalGrade myOriginalGrade = originalGradeRepository.findByStudentAndAssignment(currentStudent, assignment);
            if(myOriginalGrade != null) {
                System.out.println("Overwriting " + myOriginalGrade.getStudent().getFirstName() + "'s grade on " + myOriginalGrade.getAssignment().getName() + " with original grade: " + myOriginalGrade.getGrade());
                myStudentAssignment.setGrade(myOriginalGrade.getGrade());
                studentAssignmentRepository.save(myStudentAssignment);
            } else {
                System.out.println("Error: no original grade set yet.");
            }
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(assignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Have to repopulate arraylist of students (workaround, try to find out why it's empty later)
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(studentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers);

        return returnContainer;
    }

    @RequestMapping(path = "/sendEmailOneStudent.json", method = RequestMethod.POST)
    public StringContainer sendEmailOneStudent(@RequestBody StudentContainer studentContainer) throws IOException {
        System.out.println("\nIn sendEmailOneStudent method in json controller");
        String returnString;
        if (studentContainer.getStudentAssignments().size() > 0) {
            myEmailer.sendEmailOneStudent(studentContainer.getStudentAssignments().get(0).getAssignment().getCourse(), studentContainer.getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), studentContainer, studentAssignmentRepository);
            returnString = "Email sent for " + studentContainer.getStudent().getFirstName() + " to email " + studentContainer.getStudent().getParentEmail();
        } else {
            System.out.println("Email not sent because the student has no assignment data yet.");
            returnString = "Error: email not sent because the student has no assignment data yet. Enter assignments first.";
        }
        StringContainer myMessage = new StringContainer(returnString);
        System.out.println("BACK IN JSON: " + myMessage.getMessage());

        return myMessage;
    }

    @RequestMapping(path = "/sendEmailForAllZeros.json", method = RequestMethod.POST)
    public StringContainer sendEmailForAllZeros(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) throws IOException {
        System.out.println("\nIn sendEmailForAllZeros method in json controller");
        String returnString;
        if (assignmentAndStudentContainerListContainer.getStudentContainers().size() > 0) {
            if (assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().size() > 0) {

                myEmailer.sendEmailForAllZeros(assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse(), assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), assignmentAndStudentContainerListContainer.getStudentContainers(), studentAssignmentRepository);
                returnString = "Emails sent for all students with zeros!";
            } else {
                returnString = "Error: emails not sent because there is no assignment data yet. Enter assignments first.";
            }
        } else {
            returnString = "Error: emails not sent because there are no students yet. Add students first.";
        }
        StringContainer myMessage = new StringContainer(returnString);
        System.out.println("BACK IN JSON: " + myMessage.getMessage());

        return myMessage;
    }

    @RequestMapping(path = "/sendEmailForAllHighAverages.json", method = RequestMethod.POST)
    public StringContainer sendEmailForAllHighAverages(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) throws IOException {
        System.out.println("\nIn sendEmailForAllHighAverages method in json controller");
        String returnString;
        if (assignmentAndStudentContainerListContainer.getStudentContainers().size() > 0) {
            if (assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().size() > 0) {

                myEmailer.sendEmailForAllHighAverages(assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse(), assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), assignmentAndStudentContainerListContainer.getStudentContainers(), studentAssignmentRepository);
                returnString = "Emails sent for all students with average above " + EmailCustomContent.HIGH_AVERAGE_AMOUNT + "!";
            } else {
                returnString = "Error: emails not sent because there is no assignment data yet. Enter assignments first.";
            }
        } else {
            returnString = "Error: emails not sent because there are no students yet. Add students first.";
        }
        StringContainer myMessage = new StringContainer(returnString);
        System.out.println("BACK IN JSON: " + myMessage.getMessage());

        return myMessage;
    }

    @RequestMapping(path = "/getallassNames.json", method = RequestMethod.POST)
    public ArrayList<String> getAllAss() throws IOException{
        Iterable<Assignment> assignmentIterable = assignmentRepository.findAll();
        ArrayList<String> allAssignmentNamesArrayList = new ArrayList<>();

        for(Assignment assignment: assignmentIterable){
            allAssignmentNamesArrayList.add(assignment.getName());

        }
        return allAssignmentNamesArrayList;
    }

    //the endpoint will send back:
    // (1) an arraylist of percentages of students who got in each range (for original grades)
    // (2) an arraylist of percentages of students who got in each range (for current grades)
    //Ranges used in gradebook-ng-controller on chart:
    // 0-9, 10-19, 20-29, 30-39, 40-49, 50-59, 60-69, 70-79, 80-89, 90-99, 100+

    @RequestMapping(path = "/graphIndividualAssignment.json", method = RequestMethod.POST)
    public PercentagesOfGradesAndCurvedGradesContainer graphIndividualAssignment(@RequestBody Assignment assignment) throws IOException{
        //find all current studentAssignments attached to that assignment
        ArrayList<StudentAssignment> allStudentAssignmentsForThatAssignment = studentAssignmentRepository.findAllByAssignment(assignment);
        ArrayList<Double> percentagesOnCurrentGrades = getArrayListOfPercentagesForGraph(allStudentAssignmentsForThatAssignment);

        //find all original assignments attached to that assignment
        ArrayList<OriginalGrade> allOriginalGradesForThatAssignment = originalGradeRepository.findAllByAssignment(assignment);
        //make them into studentAssignments so I can use method below
        ArrayList<StudentAssignment> fakeStudentAssignments = new ArrayList<>();
        for (OriginalGrade currentOriginalGrade : allOriginalGradesForThatAssignment) {
            StudentAssignment fakeStudentAssignment = new StudentAssignment(currentOriginalGrade.getStudent(), assignment, currentOriginalGrade.getGrade());
            fakeStudentAssignments.add(fakeStudentAssignment);
        }
        ArrayList<Double> percentagesOnOriginalGrades = getArrayListOfPercentagesForGraph(fakeStudentAssignments);

        PercentagesOfGradesAndCurvedGradesContainer percentagesContainer = new PercentagesOfGradesAndCurvedGradesContainer(percentagesOnOriginalGrades, percentagesOnCurrentGrades);

        return percentagesContainer;
    }

    public ArrayList<Double> getArrayListOfPercentagesForGraph(ArrayList<StudentAssignment> studentAssignments) {
        double totalGradeCount = 0;
        Double[] rangeCount = new Double[11];
        for (int counter = 0; counter < 11; counter++) {
            rangeCount[counter] = 0.0;
        }
        for (StudentAssignment currentStudentAssignment : studentAssignments) {
            if (!(currentStudentAssignment.getGrade() < 0)) {
                if (currentStudentAssignment.getGrade() < 10) {
                    rangeCount[0] += 1;
                } else if (currentStudentAssignment.getGrade() < 20) {
                    rangeCount[1] += 1;
                } else if (currentStudentAssignment.getGrade() < 30) {
                    rangeCount[2] += 1;
                } else if (currentStudentAssignment.getGrade() < 40) {
                    rangeCount[3] += 1;
                } else if (currentStudentAssignment.getGrade() < 50) {
                    rangeCount[4] += 1;
                } else if (currentStudentAssignment.getGrade() < 60) {
                    rangeCount[5] += 1;
                } else if (currentStudentAssignment.getGrade() < 70) {
                    rangeCount[6] += 1;
                } else if (currentStudentAssignment.getGrade() < 80) {
                    rangeCount[7] += 1;
                } else if (currentStudentAssignment.getGrade() < 90) {
                    rangeCount[8] += 1;
                } else if (currentStudentAssignment.getGrade() < 100) {
                    rangeCount[9] += 1;
                } else {
                    rangeCount[10] += 1;
                }
                totalGradeCount++;
            }

        }
        ArrayList<Double> percentagesOnCurrentGrades = new ArrayList<>();
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (totalGradeCount != 0) {
            for (int counter = 0; counter < 11; counter++) {
                double percentage = (rangeCount[counter] / totalGradeCount) * 100.0;
                String formattedPercentage = formatter.format(percentage);
                percentage = Double.valueOf(formattedPercentage);

//                percentagesOnCurrentGrades.add((rangeCount[counter] / totalGradeCount) * 100.0);
                percentagesOnCurrentGrades.add(percentage);

            }
        }
        return percentagesOnCurrentGrades;
    }







}
