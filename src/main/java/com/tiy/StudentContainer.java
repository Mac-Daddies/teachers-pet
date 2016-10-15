package com.tiy;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/11/16.
 */
// This is for populating the gradebook.
public class StudentContainer {
//    @Autowired
//    StudentAssignmentRepository studentAssignmentRepository;


    Student student;
    ArrayList<StudentAssignment> studentAssignments;
//    int overallGrade;


    public StudentContainer() {
    }

    public StudentContainer(Student student, ArrayList<StudentAssignment> studentAssignments) {
        this.student = student;
        this.studentAssignments = studentAssignments;
//        this.overallGrade = getOverallGrade();
    }

    //Getters and setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ArrayList<StudentAssignment> getStudentAssignments() {
        return studentAssignments;
    }

    public void setStudentAssignments(ArrayList<StudentAssignment> studentAssignments) {
        this.studentAssignments = studentAssignments;
    }

//    public int getOverallGrade() {
//        CurveMyScores myCurver = new CurveMyScores();
//        ArrayList<StudentAssignment> allMyStudentAssignments = studentAssignmentRepository.findAllByStudent(this.student);
//        ArrayList<Integer> myGrades = new ArrayList<>();
//        for (StudentAssignment currentStudentAssignment : allMyStudentAssignments) {
//            myGrades.add(currentStudentAssignment.getGrade());
//        }
//        int average = myCurver.getAverage(myGrades);
//        return average;
//    }
}
