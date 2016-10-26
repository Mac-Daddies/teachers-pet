package com.tiy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/10/16.
 */
@org.springframework.stereotype.Controller
public class Controller {

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

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
//        model.addAttribute("loggedIn", false);
        if (session.getAttribute("teacherWhoIsLoggedIn") == null) {
            model.addAttribute("teacherWhoIsLoggedIn", false);
        } else {
            model.addAttribute("teacherWhoIsLoggedIn", session.getAttribute("teacherWhoIsLoggedIn"));
        }

        return "home";
    }

    @RequestMapping(path = "/classList", method = RequestMethod.GET)
    public String classList(int teacherId, Model model) {
        model.addAttribute("teacherId", teacherId);
        return "classList";
    }

//    @RequestMapping(path = "/backToHome", method = RequestMethod.GET)
//    public String backToHome(int teacherId, Model model) {
//        model.addAttribute("teacherId", teacherId);
//        return "home";
//    }

//    @RequestMapping(path = "/backToHome", method = RequestMethod.POST)
//    public String backToHome(@RequestBody Teacher teacher, Model model) {
//        model.addAttribute("teacherWhoIsLoggedIn", teacher);
//        return "home";
//    }

    @RequestMapping(path = "/gradebook", method = RequestMethod.GET)
    public String gradebook(int courseId,Model model){
        model.addAttribute("courseId", courseId);
        return "gradebook";
    }

//    @RequestMapping(path = "/gradebook", method = RequestMethod.POST)
//    public String gradebook(@RequestBody Course course, Model model, HttpSession session){
//        model.addAttribute("course", course);
////        session.setAttribute("currentCourse", course);
//        return "gradebook";
//    }

    @RequestMapping(path = "/assignment", method = RequestMethod.GET)
    public String addAss(){
        return "addass";
    }

    @RequestMapping(path = "oldhome", method = RequestMethod.GET)
    public String oldhome(){
        return "oldhome";
    }

    @RequestMapping(path = "/graph", method = RequestMethod.GET)
    public String graph(int courseId,Model model){
        model.addAttribute("courseId", courseId);
        return "graph";
    }

    @RequestMapping(path = "/deleteTeacher", method = RequestMethod.GET)
    public String deleteTeacher(int teacherId) {
        Teacher teacher = teacherRepository.findOne(teacherId);

        ArrayList<Course> allCourses = courseRepository.findAllByTeacher(teacher);
        for (Course currentCourse : allCourses) {
            ArrayList<Assignment> allAssignmentsInCourse = assignmentRepository.findAllByCourseId(currentCourse.getId());
            for (Assignment currentAssignment : allAssignmentsInCourse) {
                // (1) delete all studentAssignments
                ArrayList<StudentAssignment> allStudentAssignmentsForCurrentAssignment = studentAssignmentRepository.findAllByAssignment(currentAssignment);
                for (StudentAssignment currentStudentAssignment : allStudentAssignmentsForCurrentAssignment) {
                    studentAssignmentRepository.delete(currentStudentAssignment);
                }
                // (2) delete all originalGrades
                ArrayList<OriginalGrade> allOriginalGradesForCurrentAssignment = originalGradeRepository.findAllByAssignment(currentAssignment);
                for (OriginalGrade currentOriginalGrade : allOriginalGradesForCurrentAssignment) {
                    originalGradeRepository.delete(currentOriginalGrade);
                }
                // (3) delete the current assignment
                assignmentRepository.delete(currentAssignment);
            }
            //(4) delete all student courses and students
            ArrayList<StudentCourse> allStudentCoursesByCourse = studentCourseRepository.findAllByCourse(currentCourse);
            for (StudentCourse currentStudentCourse : allStudentCoursesByCourse) {
                studentCourseRepository.delete(currentStudentCourse);
                studentRepository.delete(currentStudentCourse.getStudent());
            }
            // (5) delete course
            courseRepository.delete(currentCourse);
        }
        // (6) delete teacher
        teacherRepository.delete(teacher);

        return "redirect:/";
    }


    @RequestMapping(path = "/generateDemoData", method = RequestMethod.GET)
    public String generateDemoData() {
        Teacher teacher = new Teacher("Yehia", "Abdullah", "teacher.teacherspet@gmail.com", "password", "THS");
        Teacher retrievedTeacher = teacherRepository.findByEmailAndPassword(teacher.getEmail(), teacher.getPassword());
        if (retrievedTeacher == null) {
            teacherRepository.save(teacher);

            Course firstPeriod = new Course("Analytic Geometry", "Math", 10, teacher);
            Course retrievedFirstPeriod = courseRepository.findOne(firstPeriod.getId());
            courseRepository.save(firstPeriod);

            Student s11 = new Student("Brenda", "Adkison", "teacher.teacherspet@gmail.com");
            studentRepository.save(s11);
            StudentCourse sc11 = new StudentCourse(s11, firstPeriod);
            studentCourseRepository.save(sc11);
            Student s12 = new Student("Chase", "Barnett", "teacher.teacherspet@gmail.com");
            studentRepository.save(s12);
            StudentCourse sc12 = new StudentCourse(s12, firstPeriod);
            studentCourseRepository.save(sc12);
            Student s13 = new Student("Star", "Blaylock", "teacher.teacherspet@gmail.com");
            studentRepository.save(s13);
            StudentCourse sc13 = new StudentCourse(s13, firstPeriod);
            studentCourseRepository.save(sc13);
            Student s14 = new Student("Truett", "Bowman", "teacher.teacherspet@gmail.com");
            studentRepository.save(s14);
            StudentCourse sc14 = new StudentCourse(s14, firstPeriod);
            studentCourseRepository.save(sc14);
            Student s15 = new Student("Cameron", "Dover", "teacher.teacherspet@gmail.com");
            studentRepository.save(s15);
            StudentCourse sc15 = new StudentCourse(s15, firstPeriod);
            studentCourseRepository.save(sc15);
            Student s16 = new Student("Kendra", "Fowler", "teacher.teacherspet@gmail.com");
            studentRepository.save(s16);
            StudentCourse sc16 = new StudentCourse(s16, firstPeriod);
            studentCourseRepository.save(sc16);
            Student s17 = new Student("Kayla", "Harris", "teacher.teacherspet@gmail.com");
            studentRepository.save(s17);
            StudentCourse sc17 = new StudentCourse(s17, firstPeriod);
            studentCourseRepository.save(sc17);
            Student s18 = new Student("Jessica", "Hillman", "teacher.teacherspet@gmail.com");
            studentRepository.save(s18);
            StudentCourse sc18 = new StudentCourse(s18, firstPeriod);
            studentCourseRepository.save(sc18);
            Student s19 = new Student("Hunter", "Ingram", "teacher.teacherspet@gmail.com");
            studentRepository.save(s19);
            StudentCourse sc19 = new StudentCourse(s19, firstPeriod);
            studentCourseRepository.save(sc19);
            Student s110 = new Student("Jacob", "Kelley", "teacher.teacherspet@gmail.com");
            studentRepository.save(s110);
            StudentCourse sc110 = new StudentCourse(s110, firstPeriod);
            studentCourseRepository.save(sc110);
            Student s111 = new Student("Brandon", "Kidd", "teacher.teacherspet@gmail.com");
            studentRepository.save(s111);
            StudentCourse sc111 = new StudentCourse(s111, firstPeriod);
            studentCourseRepository.save(sc111);
            Student s112 = new Student("Shea", "Landrum", "teacher.teacherspet@gmail.com");
            studentRepository.save(s112);
            StudentCourse sc112 = new StudentCourse(s112, firstPeriod);
            studentCourseRepository.save(sc112);
            Student s113 = new Student("Chaz", "Leathers", "teacher.teacherspet@gmail.com");
            studentRepository.save(s113);
            StudentCourse sc113 = new StudentCourse(s113, firstPeriod);
            studentCourseRepository.save(sc113);
            Student s114 = new Student("D'Lante", "McClain", "teacher.teacherspet@gmail.com");
            studentRepository.save(s114);
            StudentCourse sc114 = new StudentCourse(s114, firstPeriod);
            studentCourseRepository.save(sc114);
            Student s115 = new Student("Dakota", "McDowell", "teacher.teacherspet@gmail.com");
            studentRepository.save(s115);
            StudentCourse sc115 = new StudentCourse(s115, firstPeriod);
            studentCourseRepository.save(sc115);
            Student s116 = new Student("Rashard", "Overton", "teacher.teacherspet@gmail.com");
            studentRepository.save(s116);
            StudentCourse sc116 = new StudentCourse(s116, firstPeriod);
            studentCourseRepository.save(sc116);
            Student s117 = new Student("Justice", "Stamey", "teacher.teacherspet@gmail.com");
            studentRepository.save(s117);
            StudentCourse sc117 = new StudentCourse(s117, firstPeriod);
            studentCourseRepository.save(sc117);
            Student s118 = new Student("Matthew", "Sweat", "teacher.teacherspet@gmail.com");
            studentRepository.save(s118);
            StudentCourse sc118 = new StudentCourse(s118, firstPeriod);
            studentCourseRepository.save(sc118);
            Student s119 = new Student("Dominique", "Vucek", "teacher.teacherspet@gmail.com");
            studentRepository.save(s119);
            StudentCourse sc119 = new StudentCourse(s119, firstPeriod);
            studentCourseRepository.save(sc119);
            Student s120 = new Student("Kenny", "Welchel", "teacher.teacherspet@gmail.com");
            studentRepository.save(s120);
            StudentCourse sc120 = new StudentCourse(s120, firstPeriod);
            studentCourseRepository.save(sc120);
            Student s121 = new Student("Nicole", "Wilkerson", "teacher.teacherspet@gmail.com");
            studentRepository.save(s121);
            StudentCourse sc121 = new StudentCourse(s121, firstPeriod);
            studentCourseRepository.save(sc121);
            Student s122 = new Student("Kade", "Wilson", "teacher.teacherspet@gmail.com");
            studentRepository.save(s122);
            StudentCourse sc122 = new StudentCourse(s122, firstPeriod);
            studentCourseRepository.save(sc122);

            Assignment a11 = new Assignment("Similar Triangles HW", "2016-08-14T04:00:00.000Z", firstPeriod);
            assignmentRepository.save(a11);
            StudentAssignment s11a11 = new StudentAssignment(s11, a11, 92);
            OriginalGrade os11a11 = new OriginalGrade(s11, a11, 92);
            studentAssignmentRepository.save(s11a11);
            originalGradeRepository.save(os11a11);
            StudentAssignment s12a11 = new StudentAssignment(s12, a11, 90);
            OriginalGrade os12a11 = new OriginalGrade(s12, a11, 90);
            studentAssignmentRepository.save(s12a11);
            originalGradeRepository.save(os12a11);
            StudentAssignment s13a11 = new StudentAssignment(s13, a11, 0);
            OriginalGrade os13a11 = new OriginalGrade(s13, a11, 0);
            studentAssignmentRepository.save(s13a11);
            originalGradeRepository.save(os13a11);
            StudentAssignment s14a11 = new StudentAssignment(s14, a11, 100);
            OriginalGrade os14a11 = new OriginalGrade(s14, a11, 100);
            studentAssignmentRepository.save(s14a11);
            originalGradeRepository.save(os14a11);
            StudentAssignment s15a11 = new StudentAssignment(s15, a11, 100);
            OriginalGrade os15a11 = new OriginalGrade(s15, a11, 100);
            studentAssignmentRepository.save(s15a11);
            originalGradeRepository.save(os15a11);
            StudentAssignment s16a11 = new StudentAssignment(s16, a11, 95);
            OriginalGrade os16a11 = new OriginalGrade(s16, a11, 95);
            studentAssignmentRepository.save(s16a11);
            originalGradeRepository.save(os16a11);
            StudentAssignment s17a11 = new StudentAssignment(s17, a11, 80);
            OriginalGrade os17a11 = new OriginalGrade(s17, a11, 80);
            studentAssignmentRepository.save(s17a11);
            originalGradeRepository.save(os17a11);
            StudentAssignment s18a11 = new StudentAssignment(s18, a11, 88);
            OriginalGrade os18a11 = new OriginalGrade(s18, a11, 88);
            studentAssignmentRepository.save(s18a11);
            originalGradeRepository.save(os18a11);
            StudentAssignment s19a11 = new StudentAssignment(s19, a11, 50);
            OriginalGrade os19a11 = new OriginalGrade(s19, a11, 50);
            studentAssignmentRepository.save(s19a11);
            originalGradeRepository.save(os19a11);
            StudentAssignment s110a11 = new StudentAssignment(s110, a11, 100);
            OriginalGrade os110a11 = new OriginalGrade(s110, a11, 100);
            studentAssignmentRepository.save(s110a11);
            originalGradeRepository.save(os110a11);
            StudentAssignment s111a11 = new StudentAssignment(s111, a11, 100);
            OriginalGrade os111a11 = new OriginalGrade(s111, a11, 100);
            studentAssignmentRepository.save(s111a11);
            originalGradeRepository.save(os111a11);
            StudentAssignment s112a11 = new StudentAssignment(s112, a11, 100);
            OriginalGrade os112a11 = new OriginalGrade(s112, a11, 100);
            studentAssignmentRepository.save(s112a11);
            originalGradeRepository.save(os112a11);
            StudentAssignment s113a11 = new StudentAssignment(s113, a11, 0);
            OriginalGrade os113a11 = new OriginalGrade(s113, a11, 0);
            studentAssignmentRepository.save(s113a11);
            originalGradeRepository.save(os113a11);
            StudentAssignment s114a11 = new StudentAssignment(s114, a11, 92);
            OriginalGrade os114a11 = new OriginalGrade(s114, a11, 92);
            studentAssignmentRepository.save(s114a11);
            originalGradeRepository.save(os114a11);
            StudentAssignment s115a11 = new StudentAssignment(s115, a11, 78);
            OriginalGrade os115a11 = new OriginalGrade(s115, a11, 78);
            studentAssignmentRepository.save(s115a11);
            originalGradeRepository.save(os115a11);
            StudentAssignment s116a11 = new StudentAssignment(s116, a11, 100);
            OriginalGrade os116a11 = new OriginalGrade(s116, a11, 100);
            studentAssignmentRepository.save(s116a11);
            originalGradeRepository.save(os116a11);
            StudentAssignment s117a11 = new StudentAssignment(s117, a11, 100);
            OriginalGrade os117a11 = new OriginalGrade(s117, a11, 100);
            studentAssignmentRepository.save(s117a11);
            originalGradeRepository.save(os117a11);
            StudentAssignment s118a11 = new StudentAssignment(s118, a11, 75);
            OriginalGrade os118a11 = new OriginalGrade(s118, a11, 75);
            studentAssignmentRepository.save(s118a11);
            originalGradeRepository.save(os118a11);
            StudentAssignment s119a11 = new StudentAssignment(s119, a11, 88);
            OriginalGrade os119a11 = new OriginalGrade(s119, a11, 88);
            studentAssignmentRepository.save(s119a11);
            originalGradeRepository.save(os119a11);
            StudentAssignment s120a11 = new StudentAssignment(s120, a11, 0);
            OriginalGrade os120a11 = new OriginalGrade(s120, a11, 0);
            studentAssignmentRepository.save(s120a11);
            originalGradeRepository.save(os120a11);
            StudentAssignment s121a11 = new StudentAssignment(s121, a11, 98);
            OriginalGrade os121a11 = new OriginalGrade(s121, a11, 98);
            studentAssignmentRepository.save(s121a11);
            originalGradeRepository.save(os121a11);
            StudentAssignment s122a11 = new StudentAssignment(s122, a11, 98);
            OriginalGrade os122a11 = new OriginalGrade(s122, a11, 98);
            studentAssignmentRepository.save(s122a11);
            originalGradeRepository.save(os122a11);

            Assignment a12 = new Assignment("Similar Triangles Quiz", "2016-08-15T04:00:00.000Z", firstPeriod);
            assignmentRepository.save(a12);
            StudentAssignment s11a12 = new StudentAssignment(s11, a12, 60);
            OriginalGrade os11a12 = new OriginalGrade(s11, a12, 60);
            studentAssignmentRepository.save(s11a12);
            originalGradeRepository.save(os11a12);
            StudentAssignment s12a12 = new StudentAssignment(s12, a12, 68);
            OriginalGrade os12a12 = new OriginalGrade(s12, a12, 68);
            studentAssignmentRepository.save(s12a12);
            originalGradeRepository.save(os12a12);
            StudentAssignment s13a12 = new StudentAssignment(s13, a12, 90);
            OriginalGrade os13a12 = new OriginalGrade(s13, a12, 90);
            studentAssignmentRepository.save(s13a12);
            originalGradeRepository.save(os13a12);
            StudentAssignment s14a12 = new StudentAssignment(s14, a12, 82);
            OriginalGrade os14a12 = new OriginalGrade(s14, a12, 82);
            studentAssignmentRepository.save(s14a12);
            originalGradeRepository.save(os14a12);
            StudentAssignment s15a12 = new StudentAssignment(s15, a12, 88);
            OriginalGrade os15a12 = new OriginalGrade(s15, a12, 88);
            studentAssignmentRepository.save(s15a12);
            originalGradeRepository.save(os15a12);
            StudentAssignment s16a12 = new StudentAssignment(s16, a12, 40);
            OriginalGrade os16a12 = new OriginalGrade(s16, a12, 40);
            studentAssignmentRepository.save(s16a12);
            originalGradeRepository.save(os16a12);
            StudentAssignment s17a12 = new StudentAssignment(s17, a12, 73);
            OriginalGrade os17a12 = new OriginalGrade(s17, a12, 73);
            studentAssignmentRepository.save(s17a12);
            originalGradeRepository.save(os17a12);
            StudentAssignment s18a12 = new StudentAssignment(s18, a12, 64);
            OriginalGrade os18a12 = new OriginalGrade(s18, a12, 64);
            studentAssignmentRepository.save(s18a12);
            originalGradeRepository.save(os18a12);
            StudentAssignment s19a12 = new StudentAssignment(s19, a12, 65);
            OriginalGrade os19a12 = new OriginalGrade(s19, a12, 65);
            studentAssignmentRepository.save(s19a12);
            originalGradeRepository.save(os19a12);
            StudentAssignment s110a12 = new StudentAssignment(s110, a12, -1);
            studentAssignmentRepository.save(s110a12);
            StudentAssignment s111a12 = new StudentAssignment(s111, a12, 80);
            OriginalGrade os111a12 = new OriginalGrade(s111, a12, 80);
            studentAssignmentRepository.save(s111a12);
            originalGradeRepository.save(os111a12);
            StudentAssignment s112a12 = new StudentAssignment(s112, a12, 45);
            OriginalGrade os112a12 = new OriginalGrade(s112, a12, 45);
            studentAssignmentRepository.save(s112a12);
            originalGradeRepository.save(os112a12);
            StudentAssignment s113a12 = new StudentAssignment(s113, a12, 63);
            OriginalGrade os113a12 = new OriginalGrade(s113, a12, 63);
            studentAssignmentRepository.save(s113a12);
            originalGradeRepository.save(os113a12);
            StudentAssignment s114a12 = new StudentAssignment(s114, a12, 59);
            OriginalGrade os114a12 = new OriginalGrade(s114, a12, 59);
            studentAssignmentRepository.save(s114a12);
            originalGradeRepository.save(os114a12);
            StudentAssignment s115a12 = new StudentAssignment(s115, a12, 70);
            OriginalGrade os115a12 = new OriginalGrade(s115, a12, 70);
            studentAssignmentRepository.save(s115a12);
            originalGradeRepository.save(os115a12);
            StudentAssignment s116a12 = new StudentAssignment(s116, a12, 68);
            OriginalGrade os116a12 = new OriginalGrade(s116, a12, 68);
            studentAssignmentRepository.save(s116a12);
            originalGradeRepository.save(os116a12);
            StudentAssignment s117a12 = new StudentAssignment(s117, a12, 70);
            OriginalGrade os117a12 = new OriginalGrade(s117, a12, 70);
            studentAssignmentRepository.save(s117a12);
            originalGradeRepository.save(os117a12);
            StudentAssignment s118a12 = new StudentAssignment(s118, a12, 64);
            OriginalGrade os118a12 = new OriginalGrade(s118, a12, 64);
            studentAssignmentRepository.save(s118a12);
            originalGradeRepository.save(os118a12);
            StudentAssignment s119a12 = new StudentAssignment(s119, a12, 84);
            OriginalGrade os119a12 = new OriginalGrade(s119, a12, 84);
            studentAssignmentRepository.save(s119a12);
            originalGradeRepository.save(os119a12);
            StudentAssignment s120a12 = new StudentAssignment(s120, a12, 76);
            OriginalGrade os120a12 = new OriginalGrade(s120, a12, 76);
            studentAssignmentRepository.save(s120a12);
            originalGradeRepository.save(os120a12);
            StudentAssignment s121a12 = new StudentAssignment(s121, a12, 67);
            OriginalGrade os121a12 = new OriginalGrade(s121, a12, 67);
            studentAssignmentRepository.save(s121a12);
            originalGradeRepository.save(os121a12);
            StudentAssignment s122a12 = new StudentAssignment(s122, a12, 40);
            OriginalGrade os122a12 = new OriginalGrade(s122, a12, 40);
            studentAssignmentRepository.save(s122a12);
            originalGradeRepository.save(os122a12);


            Assignment a13 = new Assignment("Congruency Project", "2016-08-20T04:00:00.000Z", firstPeriod);
            assignmentRepository.save(a13);
            StudentAssignment s11a13 = new StudentAssignment(s11, a13, 100);
            OriginalGrade os11a13 = new OriginalGrade(s11, a13, 100);
            studentAssignmentRepository.save(s11a13);
            originalGradeRepository.save(os11a13);
            StudentAssignment s12a13 = new StudentAssignment(s12, a13, 75);
            OriginalGrade os12a13 = new OriginalGrade(s12, a13, 75);
            studentAssignmentRepository.save(s12a13);
            originalGradeRepository.save(os12a13);
            StudentAssignment s13a13 = new StudentAssignment(s13, a13, 75);
            OriginalGrade os13a13 = new OriginalGrade(s13, a13, 75);
            studentAssignmentRepository.save(s13a13);
            originalGradeRepository.save(os13a13);
            StudentAssignment s14a13 = new StudentAssignment(s14, a13, 50);
            OriginalGrade os14a13 = new OriginalGrade(s14, a13, 50);
            studentAssignmentRepository.save(s14a13);
            originalGradeRepository.save(os14a13);
            StudentAssignment s15a13 = new StudentAssignment(s15, a13, 100);
            OriginalGrade os15a13 = new OriginalGrade(s15, a13, 100);
            studentAssignmentRepository.save(s15a13);
            originalGradeRepository.save(os15a13);
            StudentAssignment s16a13 = new StudentAssignment(s16, a13, 95);
            OriginalGrade os16a13 = new OriginalGrade(s16, a13, 95);
            studentAssignmentRepository.save(s16a13);
            originalGradeRepository.save(os16a13);
            StudentAssignment s17a13 = new StudentAssignment(s17, a13, 95);
            OriginalGrade os17a13 = new OriginalGrade(s17, a13, 95);
            studentAssignmentRepository.save(s17a13);
            originalGradeRepository.save(os17a13);
            StudentAssignment s18a13 = new StudentAssignment(s18, a13, 50);
            OriginalGrade os18a13 = new OriginalGrade(s18, a13, 50);
            studentAssignmentRepository.save(s18a13);
            originalGradeRepository.save(os18a13);
            StudentAssignment s19a13 = new StudentAssignment(s19, a13, 75);
            OriginalGrade os19a13 = new OriginalGrade(s19, a13, 75);
            studentAssignmentRepository.save(s19a13);
            originalGradeRepository.save(os19a13);
            StudentAssignment s110a13 = new StudentAssignment(s110, a13, 75);
            OriginalGrade os110a13 = new OriginalGrade(s110, a13, 75);
            studentAssignmentRepository.save(s110a13);
            originalGradeRepository.save(os110a13);
            StudentAssignment s111a13 = new StudentAssignment(s111, a13, 75);
            OriginalGrade os111a13 = new OriginalGrade(s111, a13, 75);
            studentAssignmentRepository.save(s111a13);
            originalGradeRepository.save(os111a13);
            StudentAssignment s112a13 = new StudentAssignment(s112, a13, 100);
            OriginalGrade os112a13 = new OriginalGrade(s112, a13, 100);
            studentAssignmentRepository.save(s112a13);
            originalGradeRepository.save(os112a13);
            StudentAssignment s113a13 = new StudentAssignment(s113, a13, 0);
            OriginalGrade os113a13 = new OriginalGrade(s113, a13, 0);
            studentAssignmentRepository.save(s113a13);
            originalGradeRepository.save(os113a13);
            StudentAssignment s114a13 = new StudentAssignment(s114, a13, 95);
            OriginalGrade os114a13 = new OriginalGrade(s114, a13, 95);
            studentAssignmentRepository.save(s114a13);
            originalGradeRepository.save(os114a13);
            StudentAssignment s115a13 = new StudentAssignment(s115, a13, 95);
            OriginalGrade os115a13 = new OriginalGrade(s115, a13, 95);
            studentAssignmentRepository.save(s115a13);
            originalGradeRepository.save(os115a13);
            StudentAssignment s116a13 = new StudentAssignment(s116, a13, 75);
            OriginalGrade os116a13 = new OriginalGrade(s116, a13, 75);
            studentAssignmentRepository.save(s116a13);
            originalGradeRepository.save(os116a13);
            StudentAssignment s117a13 = new StudentAssignment(s117, a13, 75);
            OriginalGrade os117a13 = new OriginalGrade(s117, a13, 75);
            studentAssignmentRepository.save(s117a13);
            originalGradeRepository.save(os117a13);
            StudentAssignment s118a13 = new StudentAssignment(s118, a13, 75);
            OriginalGrade os118a13 = new OriginalGrade(s118, a13, 75);
            studentAssignmentRepository.save(s118a13);
            originalGradeRepository.save(os118a13);
            StudentAssignment s119a13 = new StudentAssignment(s119, a13, 95);
            OriginalGrade os119a13 = new OriginalGrade(s119, a13, 95);
            studentAssignmentRepository.save(s119a13);
            originalGradeRepository.save(os119a13);
            StudentAssignment s120a13 = new StudentAssignment(s120, a13, 50);
            OriginalGrade os120a13 = new OriginalGrade(s120, a13, 50);
            studentAssignmentRepository.save(s120a13);
            originalGradeRepository.save(os120a13);
            StudentAssignment s121a13 = new StudentAssignment(s121, a13, 0);
            OriginalGrade os121a13 = new OriginalGrade(s121, a13, 0);
            studentAssignmentRepository.save(s121a13);
            originalGradeRepository.save(os121a13);
            StudentAssignment s122a13 = new StudentAssignment(s122, a13, 95);
            OriginalGrade os122a13 = new OriginalGrade(s122, a13, 95);
            studentAssignmentRepository.save(s122a13);
            originalGradeRepository.save(os122a13);

            Assignment a14 = new Assignment("Unit 1 Test", "2016-08-22T04:00:00.000Z", firstPeriod);
            assignmentRepository.save(a14);
            StudentAssignment s11a14 = new StudentAssignment(s11, a14, 72);
            OriginalGrade os11a14 = new OriginalGrade(s11, a14, 72);
            studentAssignmentRepository.save(s11a14);
            originalGradeRepository.save(os11a14);
            StudentAssignment s12a14 = new StudentAssignment(s12, a14, 78);
            OriginalGrade os12a14 = new OriginalGrade(s12, a14, 78);
            studentAssignmentRepository.save(s12a14);
            originalGradeRepository.save(os12a14);
            StudentAssignment s13a14 = new StudentAssignment(s13, a14, 78);
            OriginalGrade os13a14 = new OriginalGrade(s13, a14, 78);
            studentAssignmentRepository.save(s13a14);
            originalGradeRepository.save(os13a14);
            StudentAssignment s14a14 = new StudentAssignment(s14, a14, 88);
            OriginalGrade os14a14 = new OriginalGrade(s14, a14, 88);
            studentAssignmentRepository.save(s14a14);
            originalGradeRepository.save(os14a14);
            StudentAssignment s15a14 = new StudentAssignment(s15, a14, 60);
            OriginalGrade os15a14 = new OriginalGrade(s15, a14, 60);
            studentAssignmentRepository.save(s15a14);
            originalGradeRepository.save(os15a14);
            StudentAssignment s16a14 = new StudentAssignment(s16, a14, 64);
            OriginalGrade os16a14 = new OriginalGrade(s16, a14, 64);
            studentAssignmentRepository.save(s16a14);
            originalGradeRepository.save(os16a14);
            StudentAssignment s17a14 = new StudentAssignment(s17, a14, 58);
            OriginalGrade os17a14 = new OriginalGrade(s17, a14, 58);
            studentAssignmentRepository.save(s17a14);
            originalGradeRepository.save(os17a14);
            StudentAssignment s18a14 = new StudentAssignment(s18, a14, 69);
            OriginalGrade os18a14 = new OriginalGrade(s18, a14, 69);
            studentAssignmentRepository.save(s18a14);
            originalGradeRepository.save(os18a14);
            StudentAssignment s19a14 = new StudentAssignment(s19, a14, 72);
            OriginalGrade os19a14 = new OriginalGrade(s19, a14, 72);
            studentAssignmentRepository.save(s19a14);
            originalGradeRepository.save(os19a14);
            StudentAssignment s110a14 = new StudentAssignment(s110, a14, 71);
            OriginalGrade os110a14 = new OriginalGrade(s110, a14, 71);
            studentAssignmentRepository.save(s110a14);
            originalGradeRepository.save(os110a14);
            StudentAssignment s111a14 = new StudentAssignment(s111, a14, 84);
            OriginalGrade os111a14 = new OriginalGrade(s111, a14, 84);
            studentAssignmentRepository.save(s111a14);
            originalGradeRepository.save(os111a14);
            StudentAssignment s112a14 = new StudentAssignment(s112, a14, 80);
            OriginalGrade os112a14 = new OriginalGrade(s112, a14, 80);
            studentAssignmentRepository.save(s112a14);
            originalGradeRepository.save(os112a14);
            StudentAssignment s113a14 = new StudentAssignment(s113, a14, 78);
            OriginalGrade os113a14 = new OriginalGrade(s113, a14, 78);
            studentAssignmentRepository.save(s113a14);
            originalGradeRepository.save(os113a14);
            StudentAssignment s114a14 = new StudentAssignment(s114, a14, 42);
            OriginalGrade os114a14 = new OriginalGrade(s114, a14, 42);
            studentAssignmentRepository.save(s114a14);
            originalGradeRepository.save(os114a14);
            StudentAssignment s115a14 = new StudentAssignment(s115, a14, 81);
            OriginalGrade os115a14 = new OriginalGrade(s115, a14, 81);
            studentAssignmentRepository.save(s115a14);
            originalGradeRepository.save(os115a14);
            StudentAssignment s116a14 = new StudentAssignment(s116, a14, 74);
            OriginalGrade os116a14 = new OriginalGrade(s116, a14, 74);
            studentAssignmentRepository.save(s116a14);
            originalGradeRepository.save(os116a14);
            StudentAssignment s117a14 = new StudentAssignment(s117, a14, 78);
            OriginalGrade os117a14 = new OriginalGrade(s117, a14, 78);
            studentAssignmentRepository.save(s117a14);
            originalGradeRepository.save(os117a14);
            StudentAssignment s118a14 = new StudentAssignment(s118, a14, 70);
            OriginalGrade os118a14 = new OriginalGrade(s118, a14, 70);
            studentAssignmentRepository.save(s118a14);
            originalGradeRepository.save(os118a14);
            StudentAssignment s119a14 = new StudentAssignment(s119, a14, 65);
            OriginalGrade os119a14 = new OriginalGrade(s119, a14, 65);
            studentAssignmentRepository.save(s119a14);
            originalGradeRepository.save(os119a14);
            StudentAssignment s120a14 = new StudentAssignment(s120, a14, 68);
            OriginalGrade os120a14 = new OriginalGrade(s120, a14, 68);
            studentAssignmentRepository.save(s120a14);
            originalGradeRepository.save(os120a14);
            StudentAssignment s121a14 = new StudentAssignment(s121, a14, 72);
            OriginalGrade os121a14 = new OriginalGrade(s121, a14, 72);
            studentAssignmentRepository.save(s121a14);
            originalGradeRepository.save(os121a14);
            StudentAssignment s122a14 = new StudentAssignment(s122, a14, 87);
            OriginalGrade os122a14 = new OriginalGrade(s122, a14, 87);
            studentAssignmentRepository.save(s122a14);
            originalGradeRepository.save(os122a14);


            Course secondPeriod = new Course("Math Support", "Math", 11, teacher);
            courseRepository.save(secondPeriod);
            Student s21 = new Student("Quincy", "Allen", "teacher.teacherspet@gmail.com");
            studentRepository.save(s21);
            StudentCourse sc21 = new StudentCourse(s21, secondPeriod);
            studentCourseRepository.save(sc21);
            Student s22 = new Student("Ke'Wane", "Barnes", "teacher.teacherspet@gmail.com");
            studentRepository.save(s22);
            StudentCourse sc22 = new StudentCourse(s22, secondPeriod);
            studentCourseRepository.save(sc22);
            Student s23 = new Student("Seth", "Burnley", "teacher.teacherspet@gmail.com");
            studentRepository.save(s23);
            StudentCourse sc23 = new StudentCourse(s23, secondPeriod);
            studentCourseRepository.save(sc23);
            Student s24 = new Student("Taylor", "Cargle", "teacher.teacherspet@gmail.com");
            studentRepository.save(s24);
            StudentCourse sc24 = new StudentCourse(s24, secondPeriod);
            studentCourseRepository.save(sc24);
            Student s25 = new Student("Chase", "Clemons", "teacher.teacherspet@gmail.com");
            studentRepository.save(s25);
            StudentCourse sc25 = new StudentCourse(s25, secondPeriod);
            studentCourseRepository.save(sc25);
            Student s26 = new Student("Harley", "Cook", "teacher.teacherspet@gmail.com");
            studentRepository.save(s26);
            StudentCourse sc26 = new StudentCourse(s26, secondPeriod);
            studentCourseRepository.save(sc26);
            Student s27 = new Student("Faith", "Croker", "teacher.teacherspet@gmail.com");
            studentRepository.save(s27);
            StudentCourse sc27 = new StudentCourse(s27, secondPeriod);
            studentCourseRepository.save(sc27);
            Student s28 = new Student("Allie", "Dunn", "teacher.teacherspet@gmail.com");
            studentRepository.save(s28);
            StudentCourse sc28 = new StudentCourse(s28, secondPeriod);
            studentCourseRepository.save(sc28);
            Student s29 = new Student("Hunter", "Forsyth", "teacher.teacherspet@gmail.com");
            studentRepository.save(s29);
            StudentCourse sc29 = new StudentCourse(s29, secondPeriod);
            studentCourseRepository.save(sc29);
            Student s210 = new Student("Abbie", "Godfrey", "teacher.teacherspet@gmail.com");
            studentRepository.save(s210);
            StudentCourse sc210 = new StudentCourse(s210, secondPeriod);
            studentCourseRepository.save(sc210);
            Student s211 = new Student("Garrett", "Harris", "teacher.teacherspet@gmail.com");
            studentRepository.save(s211);
            StudentCourse sc211 = new StudentCourse(s211, secondPeriod);
            studentCourseRepository.save(sc211);
            Student s212 = new Student("Thomas", "Kofile", "teacher.teacherspet@gmail.com");
            studentRepository.save(s212);
            StudentCourse sc212 = new StudentCourse(s212, secondPeriod);
            studentCourseRepository.save(sc212);
            Student s213 = new Student("Brody", "McElwee", "teacher.teacherspet@gmail.com");
            studentRepository.save(s213);
            StudentCourse sc213 = new StudentCourse(s213, secondPeriod);
            studentCourseRepository.save(sc213);
            Student s214 = new Student("Dallas", "O'Connor", "teacher.teacherspet@gmail.com");
            studentRepository.save(s214);
            StudentCourse sc214 = new StudentCourse(s214, secondPeriod);
            studentCourseRepository.save(sc214);
            Student s215 = new Student("Dylan", "Smith", "teacher.teacherspet@gmail.com");
            studentRepository.save(s215);
            StudentCourse sc215 = new StudentCourse(s215, secondPeriod);
            studentCourseRepository.save(sc215);
            Student s216 = new Student("Kera", "Wynn", "teacher.teacherspet@gmail.com");
            studentRepository.save(s216);
            StudentCourse sc216 = new StudentCourse(s216, secondPeriod);
            studentCourseRepository.save(sc216);

            Assignment a21 = new Assignment("Math Journal", "2016-08-09T04:00:00.000Z", secondPeriod);
            assignmentRepository.save(a21);
            StudentAssignment s21a21 = new StudentAssignment(s21, a21, 90);
            OriginalGrade os21a21 = new OriginalGrade(s21, a21, 90);
            studentAssignmentRepository.save(s21a21);
            originalGradeRepository.save(os21a21);
            StudentAssignment s22a21 = new StudentAssignment(s22, a21, 95);
            OriginalGrade os22a21 = new OriginalGrade(s22, a21, 95);
            studentAssignmentRepository.save(s22a21);
            originalGradeRepository.save(os22a21);
            StudentAssignment s23a21 = new StudentAssignment(s23, a21, 0);
            OriginalGrade os23a21 = new OriginalGrade(s23, a21, 0);
            studentAssignmentRepository.save(s23a21);
            originalGradeRepository.save(os23a21);
            StudentAssignment s24a21 = new StudentAssignment(s24, a21, 0);
            OriginalGrade os24a21 = new OriginalGrade(s24, a21, 0);
            studentAssignmentRepository.save(s24a21);
            originalGradeRepository.save(os24a21);
            StudentAssignment s25a21 = new StudentAssignment(s25, a21, 90);
            OriginalGrade os25a21 = new OriginalGrade(s25, a21, 90);
            studentAssignmentRepository.save(s25a21);
            originalGradeRepository.save(os25a21);
            StudentAssignment s26a21 = new StudentAssignment(s26, a21, 92);
            OriginalGrade os26a21 = new OriginalGrade(s26, a21, 92);
            studentAssignmentRepository.save(s26a21);
            originalGradeRepository.save(os26a21);
            StudentAssignment s27a21 = new StudentAssignment(s27, a21, 86);
            OriginalGrade os27a21 = new OriginalGrade(s27, a21, 86);
            studentAssignmentRepository.save(s27a21);
            originalGradeRepository.save(os27a21);
            StudentAssignment s28a21 = new StudentAssignment(s28, a21, 75);
            OriginalGrade os28a21 = new OriginalGrade(s28, a21, 75);
            studentAssignmentRepository.save(s28a21);
            originalGradeRepository.save(os28a21);
            StudentAssignment s29a21 = new StudentAssignment(s29, a21, 70);
            OriginalGrade os29a21 = new OriginalGrade(s29, a21, 70);
            studentAssignmentRepository.save(s29a21);
            originalGradeRepository.save(os29a21);
            StudentAssignment s210a21 = new StudentAssignment(s210, a21, 88);
            OriginalGrade os210a21 = new OriginalGrade(s210, a21, 88);
            studentAssignmentRepository.save(s210a21);
            originalGradeRepository.save(os210a21);
            StudentAssignment s211a21 = new StudentAssignment(s211, a21, 92);
            OriginalGrade os211a21 = new OriginalGrade(s211, a21, 92);
            studentAssignmentRepository.save(s211a21);
            originalGradeRepository.save(os211a21);
            StudentAssignment s212a21 = new StudentAssignment(s212, a21, 98);
            OriginalGrade os212a21 = new OriginalGrade(s212, a21, 98);
            studentAssignmentRepository.save(s212a21);
            originalGradeRepository.save(os212a21);
            StudentAssignment s213a21 = new StudentAssignment(s213, a21, 98);
            OriginalGrade os213a21 = new OriginalGrade(s213, a21, 98);
            studentAssignmentRepository.save(s213a21);
            originalGradeRepository.save(os213a21);
            StudentAssignment s214a21 = new StudentAssignment(s214, a21, 80);
            OriginalGrade os214a21 = new OriginalGrade(s214, a21, 80);
            studentAssignmentRepository.save(s214a21);
            originalGradeRepository.save(os214a21);
            StudentAssignment s215a21 = new StudentAssignment(s215, a21, 70);
            OriginalGrade os215a21 = new OriginalGrade(s215, a21, 70);
            studentAssignmentRepository.save(s215a21);
            originalGradeRepository.save(os215a21);
            StudentAssignment s216a21 = new StudentAssignment(s216, a21, 94);
            OriginalGrade os216a21 = new OriginalGrade(s216, a21, 94);
            studentAssignmentRepository.save(s216a21);
            originalGradeRepository.save(os216a21);

            Assignment a22 = new Assignment("Polygons Quiz", "2016-08-16T04:00:00.000Z", secondPeriod);
            assignmentRepository.save(a22);
            StudentAssignment s21a22 = new StudentAssignment(s21, a22, 40);
            OriginalGrade os21a22 = new OriginalGrade(s21, a22, 40);
            studentAssignmentRepository.save(s21a22);
            originalGradeRepository.save(os21a22);
            StudentAssignment s22a22 = new StudentAssignment(s22, a22, 42);
            OriginalGrade os22a22 = new OriginalGrade(s22, a22, 42);
            studentAssignmentRepository.save(s22a22);
            originalGradeRepository.save(os22a22);
            StudentAssignment s23a22 = new StudentAssignment(s23, a22, 39);
            OriginalGrade os23a22 = new OriginalGrade(s23, a22, 39);
            studentAssignmentRepository.save(s23a22);
            originalGradeRepository.save(os23a22);
            StudentAssignment s24a22 = new StudentAssignment(s24, a22, 36);
            OriginalGrade os24a22 = new OriginalGrade(s24, a22, 36);
            studentAssignmentRepository.save(s24a22);
            originalGradeRepository.save(os24a22);
            StudentAssignment s25a22 = new StudentAssignment(s25, a22, 49);
            OriginalGrade os25a22 = new OriginalGrade(s25, a22, 49);
            studentAssignmentRepository.save(s25a22);
            originalGradeRepository.save(os25a22);
            StudentAssignment s26a22 = new StudentAssignment(s26, a22, 45);
            OriginalGrade os26a22 = new OriginalGrade(s26, a22, 45);
            studentAssignmentRepository.save(s26a22);
            originalGradeRepository.save(os26a22);
            StudentAssignment s27a22 = new StudentAssignment(s27, a22, 85);
            OriginalGrade os27a22 = new OriginalGrade(s27, a22, 85);
            studentAssignmentRepository.save(s27a22);
            originalGradeRepository.save(os27a22);
            StudentAssignment s28a22 = new StudentAssignment(s28, a22, 76);
            OriginalGrade os28a22 = new OriginalGrade(s28, a22, 76);
            studentAssignmentRepository.save(s28a22);
            originalGradeRepository.save(os28a22);
            StudentAssignment s29a22 = new StudentAssignment(s29, a22, 82);
            OriginalGrade os29a22 = new OriginalGrade(s29, a22, 82);
            studentAssignmentRepository.save(s29a22);
            originalGradeRepository.save(os29a22);
            StudentAssignment s210a22 = new StudentAssignment(s210, a22, 50);
            OriginalGrade os210a22 = new OriginalGrade(s210, a22, 50);
            studentAssignmentRepository.save(s210a22);
            originalGradeRepository.save(os210a22);
            StudentAssignment s211a22 = new StudentAssignment(s211, a22, 60);
            OriginalGrade os211a22 = new OriginalGrade(s211, a22, 60);
            studentAssignmentRepository.save(s211a22);
            originalGradeRepository.save(os211a22);
            StudentAssignment s212a22 = new StudentAssignment(s212, a22, 55);
            OriginalGrade os212a22 = new OriginalGrade(s212, a22, 55);
            studentAssignmentRepository.save(s212a22);
            originalGradeRepository.save(os212a22);
            StudentAssignment s213a22 = new StudentAssignment(s213, a22, 58);
            OriginalGrade os213a22 = new OriginalGrade(s213, a22, 58);
            studentAssignmentRepository.save(s213a22);
            originalGradeRepository.save(os213a22);
            StudentAssignment s214a22 = new StudentAssignment(s214, a22, 50);
            OriginalGrade os214a22 = new OriginalGrade(s214, a22, 50);
            studentAssignmentRepository.save(s214a22);
            originalGradeRepository.save(os214a22);
            StudentAssignment s215a22 = new StudentAssignment(s215, a22, 67);
            OriginalGrade os215a22 = new OriginalGrade(s215, a22, 67);
            studentAssignmentRepository.save(s215a22);
            originalGradeRepository.save(os215a22);
            StudentAssignment s216a22 = new StudentAssignment(s216, a22, 52);
            OriginalGrade os216a22 = new OriginalGrade(s216, a22, 52);
            studentAssignmentRepository.save(s216a22);
            originalGradeRepository.save(os216a22);

            Assignment a23 = new Assignment("Triangle Presentations", "2016-08-09T04:00:00.000Z", secondPeriod);
            assignmentRepository.save(a23);
            StudentAssignment s21a23 = new StudentAssignment(s21, a23, 80);
            OriginalGrade os21a23 = new OriginalGrade(s21, a23, 80);
            studentAssignmentRepository.save(s21a23);
            originalGradeRepository.save(os21a23);
            StudentAssignment s22a23 = new StudentAssignment(s22, a23, 90);
            OriginalGrade os22a23 = new OriginalGrade(s22, a23, 90);
            studentAssignmentRepository.save(s22a23);
            originalGradeRepository.save(os22a23);
            StudentAssignment s23a23 = new StudentAssignment(s23, a23, 95);
            OriginalGrade os23a23 = new OriginalGrade(s23, a23, 95);
            studentAssignmentRepository.save(s23a23);
            originalGradeRepository.save(os23a23);
            StudentAssignment s24a23 = new StudentAssignment(s24, a23, 90);
            OriginalGrade os24a23 = new OriginalGrade(s24, a23, 90);
            studentAssignmentRepository.save(s24a23);
            originalGradeRepository.save(os24a23);
            StudentAssignment s25a23 = new StudentAssignment(s25, a23, 80);
            OriginalGrade os25a23 = new OriginalGrade(s25, a23, 80);
            studentAssignmentRepository.save(s25a23);
            originalGradeRepository.save(os25a23);
            StudentAssignment s26a23 = new StudentAssignment(s26, a23, 80);
            OriginalGrade os26a23 = new OriginalGrade(s26, a23, 80);
            studentAssignmentRepository.save(s26a23);
            originalGradeRepository.save(os26a23);
            StudentAssignment s27a23 = new StudentAssignment(s27, a23, 50);
            OriginalGrade os27a23 = new OriginalGrade(s27, a23, 50);
            studentAssignmentRepository.save(s27a23);
            originalGradeRepository.save(os27a23);
            StudentAssignment s28a23 = new StudentAssignment(s28, a23, 85);
            OriginalGrade os28a23 = new OriginalGrade(s28, a23, 85);
            studentAssignmentRepository.save(s28a23);
            originalGradeRepository.save(os28a23);
            StudentAssignment s29a23 = new StudentAssignment(s29, a23, 95);
            OriginalGrade os29a23 = new OriginalGrade(s29, a23, 95);
            studentAssignmentRepository.save(s29a23);
            originalGradeRepository.save(os29a23);
            StudentAssignment s210a23 = new StudentAssignment(s210, a23, 90);
            OriginalGrade os210a23 = new OriginalGrade(s210, a23, 90);
            studentAssignmentRepository.save(s210a23);
            originalGradeRepository.save(os210a23);
            StudentAssignment s211a23 = new StudentAssignment(s211, a23, 90);
            OriginalGrade os211a23 = new OriginalGrade(s211, a23, 90);
            studentAssignmentRepository.save(s211a23);
            originalGradeRepository.save(os211a23);
            StudentAssignment s212a23 = new StudentAssignment(s212, a23, 75);
            OriginalGrade os212a23 = new OriginalGrade(s212, a23, 75);
            studentAssignmentRepository.save(s212a23);
            originalGradeRepository.save(os212a23);
            StudentAssignment s213a23 = new StudentAssignment(s213, a23, 80);
            OriginalGrade os213a23 = new OriginalGrade(s213, a23, 80);
            studentAssignmentRepository.save(s213a23);
            originalGradeRepository.save(os213a23);
            StudentAssignment s214a23 = new StudentAssignment(s214, a23, 85);
            OriginalGrade os214a23 = new OriginalGrade(s214, a23, 85);
            studentAssignmentRepository.save(s214a23);
            originalGradeRepository.save(os214a23);
            StudentAssignment s215a23 = new StudentAssignment(s215, a23, 60);
            OriginalGrade os215a23 = new OriginalGrade(s215, a23, 60);
            studentAssignmentRepository.save(s215a23);
            originalGradeRepository.save(os215a23);
            StudentAssignment s216a23 = new StudentAssignment(s216, a23, 90);
            OriginalGrade os216a23 = new OriginalGrade(s216, a23, 90);
            studentAssignmentRepository.save(s216a23);
            originalGradeRepository.save(os216a23);


            Course thirdPeriod = new Course("Accelerated Advanced Algebra II", "Math", 10, teacher);
            courseRepository.save(thirdPeriod);
            Student s41 = new Student("Carson", "Brown", "teacher.teacherspet@gmail.com");
            studentRepository.save(s41);
            StudentCourse sc41 = new StudentCourse(s41, thirdPeriod);
            studentCourseRepository.save(sc41);
            Student s42 = new Student("Taylor", "Camp", "teacher.teacherspet@gmail.com");
            studentRepository.save(s42);
            StudentCourse sc42 = new StudentCourse(s42, thirdPeriod);
            studentCourseRepository.save(sc42);
            Student s43 = new Student("Alexis", "Clanton", "teacher.teacherspet@gmail.com");
            studentRepository.save(s43);
            StudentCourse sc43 = new StudentCourse(s43, thirdPeriod);
            studentCourseRepository.save(sc43);
            Student s44 = new Student("Steven", "Duarte", "teacher.teacherspet@gmail.com");
            studentRepository.save(s44);
            StudentCourse sc44 = new StudentCourse(s44, thirdPeriod);
            studentCourseRepository.save(sc44);
            Student s45 = new Student("Diego", "Edwards", "teacher.teacherspet@gmail.com");
            studentRepository.save(s45);
            StudentCourse sc45 = new StudentCourse(s45, thirdPeriod);
            studentCourseRepository.save(sc45);
            Student s46 = new Student("Dawson", "Elrod", "teacher.teacherspet@gmail.com");
            studentRepository.save(s46);
            StudentCourse sc46 = new StudentCourse(s46, thirdPeriod);
            studentCourseRepository.save(sc46);
            Student s47 = new Student("Lacey", "Etheridge", "teacher.teacherspet@gmail.com");
            studentRepository.save(s47);
            StudentCourse sc47 = new StudentCourse(s47, thirdPeriod);
            studentCourseRepository.save(sc47);
            Student s48 = new Student("Michael", "Gay", "teacher.teacherspet@gmail.com");
            studentRepository.save(s48);
            StudentCourse sc48 = new StudentCourse(s48, thirdPeriod);
            studentCourseRepository.save(sc48);
            Student s49 = new Student("Ethan", "Gober", "teacher.teacherspet@gmail.com");
            studentRepository.save(s49);
            StudentCourse sc49 = new StudentCourse(s49, thirdPeriod);
            studentCourseRepository.save(sc49);
            Student s410 = new Student("Belle", "Hudson", "teacher.teacherspet@gmail.com");
            studentRepository.save(s410);
            StudentCourse sc410 = new StudentCourse(s410, thirdPeriod);
            studentCourseRepository.save(sc410);
            Student s411 = new Student("Robyn", "James", "teacher.teacherspet@gmail.com");
            studentRepository.save(s411);
            StudentCourse sc411 = new StudentCourse(s411, thirdPeriod);
            studentCourseRepository.save(sc411);
            Student s412 = new Student("Ashton", "Kellogg", "teacher.teacherspet@gmail.com");
            studentRepository.save(s412);
            StudentCourse sc412 = new StudentCourse(s412, thirdPeriod);
            studentCourseRepository.save(sc412);
            Student s413 = new Student("Halle", "Maxwell", "teacher.teacherspet@gmail.com");
            studentRepository.save(s413);
            StudentCourse sc413 = new StudentCourse(s413, thirdPeriod);
            studentCourseRepository.save(sc413);
            Student s414 = new Student("Taylor", "McVey", "teacher.teacherspet@gmail.com");
            studentRepository.save(s414);
            StudentCourse sc414 = new StudentCourse(s414, thirdPeriod);
            studentCourseRepository.save(sc414);
            Student s415 = new Student("Riha", "Momin", "teacher.teacherspet@gmail.com");
            studentRepository.save(s415);
            StudentCourse sc415 = new StudentCourse(s415, thirdPeriod);
            studentCourseRepository.save(sc415);
            Student s416 = new Student("Jonah", "Roberson", "teacher.teacherspet@gmail.com");
            studentRepository.save(s416);
            StudentCourse sc416 = new StudentCourse(s416, thirdPeriod);
            studentCourseRepository.save(sc416);
            Student s417 = new Student("Courtney", "Smith", "teacher.teacherspet@gmail.com");
            studentRepository.save(s417);
            StudentCourse sc417 = new StudentCourse(s417, thirdPeriod);
            studentCourseRepository.save(sc417);
            Student s418 = new Student("Alli", "Streetman", "teacher.teacherspet@gmail.com");
            studentRepository.save(s418);
            StudentCourse sc418 = new StudentCourse(s418, thirdPeriod);
            studentCourseRepository.save(sc418);

            Assignment a41 = new Assignment("\"How Odd\" Investigation", "2016-08-12T04:00:00.000Z", thirdPeriod);
            assignmentRepository.save(a41);
            StudentAssignment s41a41 = new StudentAssignment(s41, a41, -1);
            studentAssignmentRepository.save(s41a41);
            StudentAssignment s42a41 = new StudentAssignment(s42, a41, -1);
            studentAssignmentRepository.save(s42a41);
            StudentAssignment s43a41 = new StudentAssignment(s43, a41, -1);
            studentAssignmentRepository.save(s43a41);
            StudentAssignment s44a41 = new StudentAssignment(s44, a41, 80);
            OriginalGrade os44a41 = new OriginalGrade(s44, a41, 80);
            studentAssignmentRepository.save(s44a41);
            originalGradeRepository.save(os44a41);
            StudentAssignment s45a41 = new StudentAssignment(s45, a41, 94);
            OriginalGrade os45a41 = new OriginalGrade(s45, a41, 94);
            studentAssignmentRepository.save(s45a41);
            originalGradeRepository.save(os45a41);
            StudentAssignment s46a41 = new StudentAssignment(s46, a41, -1);
            studentAssignmentRepository.save(s46a41);
            StudentAssignment s47a41 = new StudentAssignment(s47, a41, -1);
            studentAssignmentRepository.save(s47a41);
            StudentAssignment s48a41 = new StudentAssignment(s48, a41, 90);
            OriginalGrade os48a41 = new OriginalGrade(s48, a41, 90);
            studentAssignmentRepository.save(s48a41);
            originalGradeRepository.save(os48a41);
            StudentAssignment s49a41 = new StudentAssignment(s49, a41, 80);
            OriginalGrade os49a41 = new OriginalGrade(s49, a41, 80);
            studentAssignmentRepository.save(s49a41);
            originalGradeRepository.save(os49a41);
            StudentAssignment s410a41 = new StudentAssignment(s410, a41, 85);
            OriginalGrade os410a41 = new OriginalGrade(s410, a41, 85);
            studentAssignmentRepository.save(s410a41);
            originalGradeRepository.save(os410a41);
            StudentAssignment s411a41 = new StudentAssignment(s411, a41, 75);
            OriginalGrade os411a41 = new OriginalGrade(s411, a41, 75);
            studentAssignmentRepository.save(s411a41);
            originalGradeRepository.save(os411a41);
            StudentAssignment s412a41 = new StudentAssignment(s412, a41, 70);
            OriginalGrade os412a41 = new OriginalGrade(s412, a41, 70);
            studentAssignmentRepository.save(s412a41);
            originalGradeRepository.save(os412a41);
            StudentAssignment s413a41 = new StudentAssignment(s413, a41, 90);
            OriginalGrade os413a41 = new OriginalGrade(s413, a41, 90);
            studentAssignmentRepository.save(s413a41);
            originalGradeRepository.save(os413a41);
            StudentAssignment s414a41 = new StudentAssignment(s414, a41, 90);
            OriginalGrade os414a41 = new OriginalGrade(s414, a41, 90);
            studentAssignmentRepository.save(s414a41);
            originalGradeRepository.save(os414a41);
            StudentAssignment s415a41 = new StudentAssignment(s415, a41, 92);
            OriginalGrade os415a41 = new OriginalGrade(s415, a41, 92);
            studentAssignmentRepository.save(s415a41);
            originalGradeRepository.save(os415a41);
            StudentAssignment s416a41 = new StudentAssignment(s416, a41, 78);
            OriginalGrade os416a41 = new OriginalGrade(s416, a41, 78);
            studentAssignmentRepository.save(s416a41);
            originalGradeRepository.save(os416a41);
            StudentAssignment s417a41 = new StudentAssignment(s417, a41, 88);
            OriginalGrade os417a41 = new OriginalGrade(s417, a41, 88);
            studentAssignmentRepository.save(s417a41);
            originalGradeRepository.save(os417a41);
            StudentAssignment s418a41 = new StudentAssignment(s418, a41, 88);
            OriginalGrade os418a41 = new OriginalGrade(s418, a41, 88);
            studentAssignmentRepository.save(s418a41);
            originalGradeRepository.save(os418a41);

            Assignment a42 = new Assignment("Probability Unit Test", "2016-08-20T04:00:00.000Z", thirdPeriod);
            assignmentRepository.save(a42);
            StudentAssignment s41a42 = new StudentAssignment(s41, a42, 98);
            OriginalGrade os41a42 = new OriginalGrade(s41, a42, 98);
            studentAssignmentRepository.save(s41a42);
            originalGradeRepository.save(os41a42);
            StudentAssignment s42a42 = new StudentAssignment(s42, a42, 90);
            OriginalGrade os42a42 = new OriginalGrade(s42, a42, 90);
            studentAssignmentRepository.save(s42a42);
            originalGradeRepository.save(os42a42);
            StudentAssignment s43a42 = new StudentAssignment(s43, a42, 80);
            OriginalGrade os43a42 = new OriginalGrade(s43, a42, 80);
            studentAssignmentRepository.save(s43a42);
            originalGradeRepository.save(os43a42);
            StudentAssignment s44a42 = new StudentAssignment(s44, a42, 72);
            OriginalGrade os44a42 = new OriginalGrade(s44, a42, 72);
            studentAssignmentRepository.save(s44a42);
            originalGradeRepository.save(os44a42);
            StudentAssignment s45a42 = new StudentAssignment(s45, a42, 98);
            OriginalGrade os45a42 = new OriginalGrade(s45, a42, 98);
            studentAssignmentRepository.save(s45a42);
            originalGradeRepository.save(os45a42);
            StudentAssignment s46a42 = new StudentAssignment(s46, a42, 72);
            OriginalGrade os46a42 = new OriginalGrade(s46, a42, 72);
            studentAssignmentRepository.save(s46a42);
            originalGradeRepository.save(os46a42);
            StudentAssignment s47a42 = new StudentAssignment(s47, a42, 94);
            OriginalGrade os47a42 = new OriginalGrade(s47, a42, 94);
            studentAssignmentRepository.save(s47a42);
            originalGradeRepository.save(os47a42);
            StudentAssignment s48a42 = new StudentAssignment(s48, a42, 90);
            OriginalGrade os48a42 = new OriginalGrade(s48, a42, 90);
            studentAssignmentRepository.save(s48a42);
            originalGradeRepository.save(os48a42);
            StudentAssignment s49a42 = new StudentAssignment(s49, a42, 89);
            OriginalGrade os49a42 = new OriginalGrade(s49, a42, 89);
            studentAssignmentRepository.save(s49a42);
            originalGradeRepository.save(os49a42);
            StudentAssignment s410a42 = new StudentAssignment(s410, a42, 79);
            OriginalGrade os410a42 = new OriginalGrade(s410, a42, 79);
            studentAssignmentRepository.save(s410a42);
            originalGradeRepository.save(os410a42);
            StudentAssignment s411a42 = new StudentAssignment(s411, a42, 70);
            OriginalGrade os411a42 = new OriginalGrade(s411, a42, 70);
            studentAssignmentRepository.save(s411a42);
            originalGradeRepository.save(os411a42);
            StudentAssignment s412a42 = new StudentAssignment(s412, a42, 94);
            OriginalGrade os412a42 = new OriginalGrade(s412, a42, 94);
            studentAssignmentRepository.save(s412a42);
            originalGradeRepository.save(os412a42);
            StudentAssignment s413a42 = new StudentAssignment(s413, a42, 81);
            OriginalGrade os413a42 = new OriginalGrade(s413, a42, 81);
            studentAssignmentRepository.save(s413a42);
            originalGradeRepository.save(os413a42);
            StudentAssignment s414a42 = new StudentAssignment(s414, a42, 88);
            OriginalGrade os414a42 = new OriginalGrade(s414, a42, 88);
            studentAssignmentRepository.save(s414a42);
            originalGradeRepository.save(os414a42);
            StudentAssignment s415a42 = new StudentAssignment(s415, a42, 78);
            OriginalGrade os415a42 = new OriginalGrade(s415, a42, 78);
            studentAssignmentRepository.save(s415a42);
            originalGradeRepository.save(os415a42);
            StudentAssignment s416a42 = new StudentAssignment(s416, a42, 92);
            OriginalGrade os416a42 = new OriginalGrade(s416, a42, 92);
            studentAssignmentRepository.save(s416a42);
            originalGradeRepository.save(os416a42);
            StudentAssignment s417a42 = new StudentAssignment(s417, a42, 90);
            OriginalGrade os417a42 = new OriginalGrade(s417, a42, 90);
            studentAssignmentRepository.save(s417a42);
            originalGradeRepository.save(os417a42);
            StudentAssignment s418a42 = new StudentAssignment(s418, a42, 82);
            OriginalGrade os418a42 = new OriginalGrade(s418, a42, 82);
            studentAssignmentRepository.save(s418a42);
            originalGradeRepository.save(os418a42);
        }
        return "redirect:/";
    }


}
