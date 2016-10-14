package com.tiy;

/**
 * Created by jessicatracy on 10/14/16.
 */
public class AssignmentAndAverageContainer {
    Assignment assignment;
    int average;

    public AssignmentAndAverageContainer() {
    }

    public AssignmentAndAverageContainer(Assignment assignment, int average) {
        this.assignment = assignment;
        this.average = average;
    }

    //Getters and setters
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }
}
