package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/11/16.
 * Instances of this class are returned when we update the gradebook. Holds a list of all student containers (which hold students
 * and their grades) as well as lists of assignment containers which also hold the assignment averages.
 * Also returns highAverage so that we can display updated button to send email out if the user changes the amount from the default
 * of 95.
 */
public class AssignmentAndStudentAssignmentContainer {

    ArrayList<StudentContainer> studentContainers;
    ArrayList<AssignmentAndAverageContainer> assignmentAndAverageContainers;
    int highAverage;

    //Constructors
    public AssignmentAndStudentAssignmentContainer() {
    }

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
