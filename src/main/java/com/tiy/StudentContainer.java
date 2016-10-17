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
    int average;


    public StudentContainer() {
    }

    public StudentContainer(Student student, ArrayList<StudentAssignment> studentAssignments, int average) {
        this.student = student;
        this.studentAssignments = studentAssignments;
        this.average = average;
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

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }
}
