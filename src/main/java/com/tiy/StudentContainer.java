package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/11/16.
 */
// This is for populating the gradebook.
public class StudentContainer {
    Student student;
    ArrayList<StudentAssignment> studentAssignments;

    public StudentContainer() {
    }

    public StudentContainer(Student student, ArrayList<StudentAssignment> studentAssignments) {
        this.student = student;
        this.studentAssignments = studentAssignments;
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
}
