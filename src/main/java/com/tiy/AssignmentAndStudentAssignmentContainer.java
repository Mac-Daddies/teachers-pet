package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/11/16.
 */
public class AssignmentAndStudentAssignmentContainer {
//    ArrayList<Assignment> assignments;
//    ArrayList<StudentAssignment> studentAssignments;
//
//    public AssignmentAndStudentAssignmentContainer() {
//    }
//
//    public AssignmentAndStudentAssignmentContainer(ArrayList<Assignment> assignments, ArrayList<StudentAssignment> studentAssignments) {
//        this.assignments = assignments;
//        this.studentAssignments = studentAssignments;
//    }
//
//    //Getters and setters
//    public ArrayList<Assignment> getAssignments() {
//        return assignments;
//    }
//
//    public void setAssignments(ArrayList<Assignment> assignments) {
//        this.assignments = assignments;
//    }
//
//    public ArrayList<StudentAssignment> getStudentAssignments() {
//        return studentAssignments;
//    }
//
//    public void setStudentAssignments(ArrayList<StudentAssignment> studentAssignments) {
//        this.studentAssignments = studentAssignments;
//    }

    ArrayList<StudentContainer> studentContainers;
//    ArrayList<Assignment> assignments;
    ArrayList<AssignmentAndAverageContainer> assignmentAndAverageContainers;
    int highAverage;

    public AssignmentAndStudentAssignmentContainer() {
    }

//    public AssignmentAndStudentAssignmentContainer(ArrayList<StudentContainer> studentContainers, ArrayList<Assignment> assignments) {
//        this.studentContainers = studentContainers;
//        this.assignments = assignments;
//    }


    public AssignmentAndStudentAssignmentContainer(ArrayList<StudentContainer> studentContainers, ArrayList<AssignmentAndAverageContainer> assignmentAndAverageContainers, int highAverage) {
        this.studentContainers = studentContainers;
        this.assignmentAndAverageContainers = assignmentAndAverageContainers;
        this.highAverage = highAverage;
    }

    //Getters and setters
    public ArrayList<StudentContainer> getStudentContainers() {
        return studentContainers;
    }

    public void setStudentContainers(ArrayList<StudentContainer> studentContainers) {
        this.studentContainers = studentContainers;
    }

    public ArrayList<AssignmentAndAverageContainer> getAssignmentAndAverageContainers() {
        return assignmentAndAverageContainers;
    }

    public void setAssignmentAndAverageContainers(ArrayList<AssignmentAndAverageContainer> assignmentAndAverageContainers) {
        this.assignmentAndAverageContainers = assignmentAndAverageContainers;
    }

    public int getHighAverage() {
        return highAverage;
    }

    public void setHighAverage(int highAverage) {
        this.highAverage = highAverage;
    }
}
