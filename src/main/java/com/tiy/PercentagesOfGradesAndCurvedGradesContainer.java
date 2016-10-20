package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/20/16.
 */
public class PercentagesOfGradesAndCurvedGradesContainer {
    ArrayList<Double> percentagesOfOriginalGrades;
    ArrayList<Double> percentagesOfCurvedGrades;

    public PercentagesOfGradesAndCurvedGradesContainer() {
    }

    public PercentagesOfGradesAndCurvedGradesContainer(ArrayList<Double> percentagesOfOriginalGrades, ArrayList<Double> percentagesOfCurvedGrades) {
        this.percentagesOfOriginalGrades = percentagesOfOriginalGrades;
        this.percentagesOfCurvedGrades = percentagesOfCurvedGrades;
    }

    //Getters and setters
    public ArrayList<Double> getPercentagesOfOriginalGrades() {
        return percentagesOfOriginalGrades;
    }

    public void setPercentagesOfOriginalGrades(ArrayList<Double> percentagesOfOriginalGrades) {
        this.percentagesOfOriginalGrades = percentagesOfOriginalGrades;
    }

    public ArrayList<Double> getPercentagesOfCurvedGrades() {
        return percentagesOfCurvedGrades;
    }

    public void setPercentagesOfCurvedGrades(ArrayList<Double> percentagesOfCurvedGrades) {
        this.percentagesOfCurvedGrades = percentagesOfCurvedGrades;
    }
}
