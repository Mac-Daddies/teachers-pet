package com.tiy;

/**
 * Created by jessicatracy on 10/14/16.
 * Container to hold an assignment and both its average and original average, which are displayed at the bottom of the gradebook table
 * so that teachers can decide how much the curve affected the average for that assignment.
 * We make a list of AssignmentAndAverageContainer to return when the gradebook is updated.
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
