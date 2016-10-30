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
    EmailCustomContent emailCustomContent = new EmailCustomContent();

    @RequestMapping(path = "/register.json", method = RequestMethod.POST)
    public LoginContainer register(@RequestBody Teacher newTeacher, HttpSession session){
        teacherRepository.save(newTeacher);

        Teacher retrievedTeacher = newTeacher;
        LoginContainer loginContainer;

        if(newTeacher == null){
            loginContainer = new LoginContainer("Error adding you!", null, null);
        } else {
            loginContainer = new LoginContainer(null,retrievedTeacher,courseRepository.findAllByTeacher(retrievedTeacher));
            session.setAttribute("loggedInTeacher", loginContainer.getTeacher());
        }

        return loginContainer;
    }

    @RequestMapping(path = "/login.json", method = RequestMethod.POST)
        public LoginContainer login(@RequestBody EmailAndPasswordContainer emailAndPasswordContainer, HttpSession session){
        Teacher returnTeacher;
        LoginContainer loginContainer;
        String email = emailAndPasswordContainer.email;
        String password = emailAndPasswordContainer.password;
        returnTeacher = teacherRepository.findByEmailAndPassword(email,password);
        if(returnTeacher == null){
            loginContainer = new LoginContainer("User not found", null, null);

        } else {
            loginContainer = new LoginContainer(null,returnTeacher,courseRepository.findAllByTeacher(returnTeacher));
            session.setAttribute("loggedInTeacher", loginContainer.getTeacher());
        }

        return loginContainer;
    }

    @RequestMapping(path = "/addclass.json", method = RequestMethod.POST)
    public ArrayList<Course> addCourse(@RequestBody Course course){
        courseRepository.save(course);

        ArrayList<Course> courseArrayList = new ArrayList<Course>();

        Iterable<Course> courseIterable = courseRepository.findAllByTeacher(course.getTeacher());

        for(Course thisClass : courseIterable){
            courseArrayList.add(thisClass);
        }

        return courseArrayList;
    }

    @RequestMapping(path = "/deleteClass.json", method = RequestMethod.POST)
    public ArrayList<Course> deleteClass(@RequestBody Course courseToDelete){
        //delete all student courses
        ArrayList<StudentCourse> studentCoursesForThisCourse = studentCourseRepository.findAllByCourse(courseToDelete);
        for (StudentCourse studentCourse : studentCoursesForThisCourse) {
            studentCourseRepository.delete(studentCourse);
        }

        //delete all assignments in the course
        ArrayList<Assignment> allAssignmentsInCourse = assignmentRepository.findAllByCourseId(courseToDelete.getId());
        ArrayList<StudentAssignment> allStudentAssignmentsForThatAssignment;
        for (Assignment assignment : allAssignmentsInCourse) {
            allStudentAssignmentsForThatAssignment = studentAssignmentRepository.findAllByAssignment(assignment);
            for (StudentAssignment studentAssignment : allStudentAssignmentsForThatAssignment) {
                studentAssignmentRepository.delete(studentAssignment);
            }
            assignmentRepository.delete(assignment);
        }

        //delete course
        Course retrievedCourse = courseRepository.findOne(courseToDelete.getId());
        courseRepository.delete(retrievedCourse);

        //return all of the teachers courses (the one we just deleted should be gone)
        ArrayList<Course> courseArrayList = new ArrayList<Course>();

        Iterable<Course> courseIterable = courseRepository.findAllByTeacher(courseToDelete.getTeacher());

        for(Course thisClass : courseIterable){
            courseArrayList.add(thisClass);
        }

        return courseArrayList;
    }

    /**Returns the info needed to display what the current email settings are from the "Edit email settings" pop-up
     * on the class list page */
    @RequestMapping(path = "/getCurrentEmailInfo.json", method = RequestMethod.POST)
    public EmailContentContainer getCurrentEmailInfo(@RequestBody Teacher teacher) {
        String emailSignature = emailCustomContent.getEmailSignature(teacher);
        EmailContentContainer emailContentContainer = new EmailContentContainer(EmailCustomContent.highAverageAmount, emailSignature);
        return emailContentContainer;
    }

    @RequestMapping(path = "/setNewHighAverageAmount.json", method = RequestMethod.POST)
    public int setNewHighAverageAmount(@RequestBody int newHighAverage) {
        EmailCustomContent.setHighAverageAmount(newHighAverage);
        System.out.println("New high average in EmailCustomContentClass: " + EmailCustomContent.highAverageAmount);
        return EmailCustomContent.highAverageAmount;
    }

    @RequestMapping(path = "/setNewEmailSignature.json", method = RequestMethod.POST)
    public StringContainer setNewEmailSignature(@RequestBody StringContainer stringContainer) {
        emailCustomContent.setEmailSignature(stringContainer.getMessage());
        return stringContainer;
    }

    /**Changes the email signature back to the default one provided by the program */
    @RequestMapping(path = "/resetEmailSignature.json", method = RequestMethod.POST)
    public StringContainer resetEmailSignature(@RequestBody Teacher teacher) {
        emailCustomContent.setEmailSignature(null);
        String emailSignature = emailCustomContent.getEmailSignature(teacher);
        StringContainer stringContainer = new StringContainer(emailSignature);
        return stringContainer;
    }

    /** Call this method when we first load the gradebook screen. Returns all assignment info and student
     * containers for the given course to populate the gradebook table */
    @RequestMapping(path = "/gradebook.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer gradebookJSON(@RequestBody int courseId){
        System.out.println("In gradebook method in JSON controller");
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

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentArrayList);

        //Repopulate arraylist of students
        studentArrayList = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            studentArrayList.add(currentStudentCourse.getStudent());
        }

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentArrayList);
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Returns the course given the course id. Needed to add students and add assignments from the new gradebook
     * html page. */
    @RequestMapping(path = "/getCurrentClass.json", method = RequestMethod.POST)
    public Course getCurrentClass(@RequestBody int courseId) {
        Course currentClass = courseRepository.findOne(courseId);
        return currentClass;
    }

    /**Returns the teacher given the teacher id. Needed in classlist-ng-controller */
    @RequestMapping(path = "/getTeacherWhoIsLoggedIn.json", method = RequestMethod.POST)
    public TeacherAndCourseContainer getTeacherWhoIsLoggedIn(@RequestBody int teacherId) {
        Teacher currentTeacher = teacherRepository.findOne(teacherId);
        ArrayList<Course> courses = courseRepository.findAllByTeacher(currentTeacher);
        TeacherAndCourseContainer teacherAndCourseContainer = new TeacherAndCourseContainer(currentTeacher, courses);
        return teacherAndCourseContainer;
    }

    /**Creates a new assignment and gives every student in the course a studentAssignment for that assignment
     * with a grade of -1 (will show up empty in gradebook - no grade data) */
    @RequestMapping(path = "/addAss.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addAss(@RequestBody Assignment assignment){
        assignmentRepository.save(assignment);

        //find all students in course and make a studentAssignment
        ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(assignment.course);
        ArrayList<Student> allStudentsInCourse = new ArrayList<>();
        StudentAssignment newStudentAssignment;
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            //Add student to arrayList of students
            allStudentsInCourse.add(currentStudentCourse.getStudent());
            //Make a new studentAssignment for that student
            newStudentAssignment = new StudentAssignment(currentStudentCourse.getStudent(), assignment, -1);
            studentAssignmentRepository.save(newStudentAssignment);
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(assignment.course.getId());

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Repopulate arraylist of students
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());
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

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());
        return returnContainer;
    }

    /**Creates a new student, a new StudentCourse to link them to the correct course, and new StudentAssignments with a grade
     * of -1 (no grade data) for that student for each assignment that is already in the course */
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

        //Give the new student each assignment that is already in the course (give a grade of -1 for no grade data)
        StudentAssignment newStudentAssignment;
        for (Assignment currentAssignment : allAssignments) {
            newStudentAssignment = new StudentAssignment(newStudent, currentAssignment, -1);
            studentAssignmentRepository.save(newStudentAssignment);
        }

        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Repopulate arraylist of students
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(currentStudentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());
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

        //Repopulate list of students
        allStudents = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudents.add(studentCourse.getStudent());
        }

        ArrayList<Assignment> allAssignmentsInCourse = assignmentRepository.findAllByCourseId(thisStudentCourse.get(0).getCourse().getId());
        allAssignmentsInCourse = orderAssignmentsByDate(allAssignmentsInCourse);

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignmentsInCourse, allStudents);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());
        return returnContainer;
    }


    /**Updates the StudentAssignments with the new grades that a user types in for a given assignment. Launched when user
     * presses "Update Grades" underneath an assignment.
     * IF this is the first time the grade is entered, we also store the data as an "original grade"
     * The parameter is a container holding an assignment and a list of student containers. Each student container in the list
     * has a student and a list of student assignments.*/
    @RequestMapping(path = "/addGrades.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addGrade(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer){
        // For each student, we need to get out their list of studentAssignments and update the studentAssignment for just that assignment.
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        ArrayList<Student> allStudents = new ArrayList<>();
        Student currentStudent;
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
                } else {
                    System.out.println("Cannot store original grade for " + currentStudentAssignment.getStudent().getFirstName() + " on " + currentStudentAssignment.getAssignment().getName() + " because no grade was entered (-1).");
                }
            }
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudents);

        //Repopulate arraylist of students
        allStudents = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            allStudents.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudents);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Replaces all StudentAssignment grades for the given assignment with the grade with the given amount of extra
     * credit added */
    @RequestMapping(path="/addExtraCredit.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addExtraCredit(@RequestBody ExtraCreditContainer extraCreditContainer) {
        Assignment currentAssignment = extraCreditContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = extraCreditContainer.getStudentContainers();
        int extraCreditAmount = extraCreditContainer.getExtraCreditAmount();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        //Make an arraylist of all the current grades (pre-extra credit)
        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());
        }

        //Curve the old grades and replace all student assignments with the new curved grade
        ArrayList<Integer> updatedGrades =  myCurver.curveByAddingExtraCredit(oldGradeArrayList, extraCreditAmount);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Repopulate arraylist of students
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Replaces all StudentAssignment grades for the given assignment with the grades curved using the flat curve*/
    @RequestMapping(path="/curveFlat.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer addFlatCurve(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        //Make an arraylist of all the current grades (pre-curve)
        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());
        }

        //Curve the old grades and replace all student assignments with the new curved grade
        ArrayList<Integer> updatedGrades =  myCurver.curveFlat(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Repopulate arraylist of students
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Replaces all StudentAssignment grades for the given assignment with the grades curved using the percentage of
     * highest curve*/
    @RequestMapping(path="/curveAsPercentageOfHighestGrade.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer curveAsPercentageOfHighestGrade(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        //Make an arraylist of all the current grades (pre-curve)
        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());
        }

        //Curve the old grades and replace all student assignments with the new curved grade
        ArrayList<Integer> updatedGrades =  myCurver.curveAsPercentageOfHighestGrade(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Repopulate arraylist of students
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());
        return returnContainer;

    }

    /**Replaces all StudentAssignment grades for the given assignment with the grades curved using the root curve*/
    @RequestMapping(path="/curveByTakingRoot.json", method = RequestMethod.POST)
    public AssignmentAndStudentAssignmentContainer curveByTakingRoot(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) {
        Assignment currentAssignment = assignmentAndStudentContainerListContainer.getAssignment();
        ArrayList<StudentContainer> studentContainers = assignmentAndStudentContainerListContainer.getStudentContainers();

        Student currentStudent;
        ArrayList<Integer> oldGradeArrayList = new ArrayList<>();
        ArrayList<Student> studentsInCourse = new ArrayList<>();

        //Make an arraylist of all the current grades (pre-curve)
        for (StudentContainer currentStudentContainer : studentContainers) {
            currentStudent = currentStudentContainer.getStudent();
            studentsInCourse.add(currentStudent);
            StudentAssignment retrievedStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
            oldGradeArrayList.add(retrievedStudentAssignment.getGrade());
        }

        //Curve the old grades and replace all student assignments with the new curved grade
        ArrayList<Integer> updatedGrades =  myCurver.curveByTakingRoot(oldGradeArrayList);
        int counter = 0;
        for (StudentContainer student : studentContainers) {
            Student nowStudent = student.getStudent();
            StudentAssignment retrievedStudentAssignment1 = studentAssignmentRepository.findByStudentAndAssignment(nowStudent, currentAssignment);
            retrievedStudentAssignment1.setGrade(updatedGrades.get(counter));
            studentAssignmentRepository.save(retrievedStudentAssignment1);
            counter++;
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(currentAssignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(studentsInCourse);

        //Repopulate arraylist of students
        studentsInCourse = new ArrayList<>();
        for (StudentContainer currentStudentContainer : studentContainers) {
            studentsInCourse.add(currentStudentContainer.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, studentsInCourse);

        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Replaces all StudentAssignment grades for the given assignment with the original grades for that assignment (if they exist)*/
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
                myStudentAssignment.setGrade(myOriginalGrade.getGrade());
                studentAssignmentRepository.save(myStudentAssignment);
            } else {
                System.out.println("No original grade set yet for " + currentStudent.getFirstName() + " on " + myStudentAssignment.getAssignment().getName() + ".");
            }
        }

        ArrayList<Assignment> allAssignments = assignmentRepository.findAllByCourseId(assignment.getCourse().getId());
        allAssignments = orderAssignmentsByDate(allAssignments);

        ArrayList<StudentContainer> myArrayListOfStudentContainers = prepareArrayListOfStudentContainersToReturn(allStudentsInCourse);

        //Repopulate arraylist of students
        allStudentsInCourse = new ArrayList<>();
        for (StudentCourse studentCourse : allStudentCoursesByCourse) {
            allStudentsInCourse.add(studentCourse.getStudent());
        }

        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = prepareArrayListOfAssignmentAndAverageContainerToReturn(allAssignments, allStudentsInCourse);
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /**Calls to the EmailCustomContent class to send an email to one individual student's parents with a progress report*/
    @RequestMapping(path = "/sendEmailOneStudent.json", method = RequestMethod.POST)
    public StringContainer sendEmailOneStudent(@RequestBody StudentContainer studentContainer) throws IOException {
        String returnString;
        if (studentContainer.getStudentAssignments().size() > 0) {
            emailCustomContent.sendEmailOneStudent(studentContainer.getStudentAssignments().get(0).getAssignment().getCourse(), studentContainer.getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), studentContainer, studentAssignmentRepository);
            returnString = "Email sent for " + studentContainer.getStudent().getFirstName() + " to email " + studentContainer.getStudent().getParentEmail();
        } else {
            returnString = "Error: email not sent because the student has no assignment data yet. Enter assignments first.";
        }
        StringContainer myMessage = new StringContainer(returnString);

        return myMessage;
    }

    /**Calls to the EmailCustomContent class to send an emails for all students who have at least one zero*/
    @RequestMapping(path = "/sendEmailForAllZeros.json", method = RequestMethod.POST)
    public StringContainer sendEmailForAllZeros(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) throws IOException {
        String returnString;
        if (assignmentAndStudentContainerListContainer.getStudentContainers().size() > 0) {
            if (assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().size() > 0) {
                emailCustomContent.sendEmailForAllZeros(assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse(), assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), assignmentAndStudentContainerListContainer.getStudentContainers(), studentAssignmentRepository);
                returnString = "Emails sent for all students with zeros!";
            } else {
                returnString = "Error: emails not sent because there is no assignment data yet. Enter assignments first.";
            }
        } else {
            returnString = "Error: emails not sent because there are no students yet. Add students first.";
        }
        StringContainer myMessage = new StringContainer(returnString);

        return myMessage;
    }

    /**Calls to the EmailCustomContent class to send an emails for all students who have over the high average
     * amount (95 default)*/
    @RequestMapping(path = "/sendEmailForAllHighAverages.json", method = RequestMethod.POST)
    public StringContainer sendEmailForAllHighAverages(@RequestBody AssignmentAndStudentContainerListContainer assignmentAndStudentContainerListContainer) throws IOException {
        String returnString;
        if (assignmentAndStudentContainerListContainer.getStudentContainers().size() > 0) {
            if (assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().size() > 0) {
                emailCustomContent.sendEmailForAllHighAverages(assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse(), assignmentAndStudentContainerListContainer.getStudentContainers().get(0).getStudentAssignments().get(0).getAssignment().getCourse().getTeacher(), assignmentAndStudentContainerListContainer.getStudentContainers(), studentAssignmentRepository);
                returnString = "Emails sent for all students with average above " + EmailCustomContent.highAverageAmount + "!";
            } else {
                returnString = "Error: emails not sent because there is no assignment data yet. Enter assignments first.";
            }
        } else {
            returnString = "Error: emails not sent because there are no students yet. Add students first.";
        }
        //put returnstring in new string container to return
        StringContainer myMessage = new StringContainer(returnString);

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

    /**Populates lists needed for ordered pairs to use on the distribution graphs.
     * This endpoint will send back:
     * (1) an arraylist of percentages of students who got in each range (for original grades)
     * (2) an arraylist of percentages of students who got in each range (for current grades)
     * Ranges used in gradebook-ng-controller on chart:
     * 0-9, 10-19, 20-29, 30-39, 40-49, 50-59, 60-69, 70-79, 80-89, 90-99, 100+ */
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

    /** Used for testing with graph-ng-controller.js */
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
        AssignmentAndStudentAssignmentContainer returnContainer = new AssignmentAndStudentAssignmentContainer(myArrayListOfStudentContainers, myAssignmentAndAverageContainers, emailCustomContent.getHighAverageAmount());

        return returnContainer;
    }

    /*********************************************** Utility Methods ***********************************************/

    /**This method makes a list of student containers. Each student container hold a student in the course,
     * an arraylist of all of that student's studentAssignments, and the student's average
     * (Returns the first parameter needed in the AssignmentAndStudentAssignmentContainer that is returned in every
     * method that updates the gradebook) */
    public ArrayList<StudentContainer> prepareArrayListOfStudentContainersToReturn(ArrayList<Student> arrayListOfStudents) {
        //first order the students by last name in a new arraylist
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
        ArrayList<AssignmentAndAverageContainer> myAssignmentAndAverageContainers = new ArrayList<>();

        for (Assignment currentAssignment : arrayListOfAssignments) {
            AssignmentAndAverageContainer currentAssignmentAndAverageContainer;
            ArrayList<Integer> currentGradesToCurve = new ArrayList<>();
            ArrayList<Integer> originalGradesToCurve = new ArrayList<>();
            for (Student currentStudent : arrayListOfStudents) {
                StudentAssignment currentStudentAssignment = studentAssignmentRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
                OriginalGrade currentOriginalGrade = originalGradeRepository.findByStudentAndAssignment(currentStudent, currentAssignment);
                if (currentStudentAssignment != null) {
                    int currentGrade = currentStudentAssignment.getGrade();
                    currentGradesToCurve.add(currentGrade);
                }
                if (currentOriginalGrade != null) {
                    int currentOriginalGradeInt = currentOriginalGrade.getGrade();
                    originalGradesToCurve.add(currentOriginalGradeInt);
                }
            }
            int currentAverage = myCurver.getAverage(currentGradesToCurve);

            int originalAverage = myCurver.getAverage(originalGradesToCurve);

            currentAssignmentAndAverageContainer = new AssignmentAndAverageContainer(currentAssignment, currentAverage, originalAverage);
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

                percentagesOnCurrentGrades.add(percentage);
            }
        }
        return percentagesOnCurrentGrades;
    }







}
