package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/12/16.
 */
public class ExtraCreditContainer {
    Assignment assignment;
    ArrayList<StudentContainer> studentContainers;
    int extraCreditAmount;

    public ExtraCreditContainer() {
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

    public int getExtraCreditAmount() {
        return extraCreditAmount;
    }

    public void setExtraCreditAmount(int extraCreditAmount) {
        this.extraCreditAmount = extraCreditAmount;
    }
}
