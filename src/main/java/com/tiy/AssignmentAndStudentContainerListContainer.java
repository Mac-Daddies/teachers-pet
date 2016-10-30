package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/12/16.
 * Instances of this class are requested from the posts from our angular controller any time we want to update grades on a
 * single assignment. Holds the target assignment and all of the students and grades.
 */
public class AssignmentAndStudentContainerListContainer {
    Assignment assignment;
    ArrayList<StudentContainer> studentContainers;

    public AssignmentAndStudentContainerListContainer() {
    }

    public AssignmentAndStudentContainerListContainer(Assignment assignment, ArrayList<StudentContainer> studentContainers) {
        this.assignment = assignment;
        this.studentContainers = studentContainers;
    }

    //Getters and setters
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public ArrayList<StudentContainer> getStudentContainers() {
        return studentContainers;
    }

    public void setStudentContainers(ArrayList<StudentContainer> studentContainers) {
        this.studentContainers = studentContainers;
    }
}
