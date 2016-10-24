package com.tiy;

/**
 * Created by jessicatracy on 10/14/16.
 */
public class AssignmentAndAverageContainer {
    Assignment assignment;
    int currentAverage;
    int originalAverage;

    public AssignmentAndAverageContainer() {
    }

    public AssignmentAndAverageContainer(Assignment assignment, int currentAverage, int originalAverage) {
        this.assignment = assignment;
        this.currentAverage = currentAverage;
        this.originalAverage = originalAverage;
    }

    //Getters and setters
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public int getCurrentAverage() {
        return currentAverage;
    }

    public void setCurrentAverage(int currentAverage) {
        this.currentAverage = currentAverage;
    }

    public int getOriginalAverage() {
        return originalAverage;
    }

    public void setOriginalAverage(int originalAverage) {
        this.originalAverage = originalAverage;
    }

}
