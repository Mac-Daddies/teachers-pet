package com.tiy;

import java.util.ArrayList;

/**
 * Created by Yehia830 on 10/10/16.
 */
public class AssignmentStudentContainer {
    ArrayList<Student> studentArrayList;
    ArrayList<Assignment> assignmentArrayList;

    public AssignmentStudentContainer(ArrayList<Student> studentArrayList, ArrayList<Assignment> assignmentArrayList) {
        this.studentArrayList = studentArrayList;
        this.assignmentArrayList = assignmentArrayList;
    }

    public ArrayList<Student> getStudentArrayList() {
        return studentArrayList;
    }

    public void setStudentArrayList(ArrayList<Student> studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    public ArrayList<Assignment> getAssignmentArrayList() {
        return assignmentArrayList;
    }

    public void setAssignmentArrayList(ArrayList<Assignment> assignmentArrayList) {
        this.assignmentArrayList = assignmentArrayList;
    }
}
