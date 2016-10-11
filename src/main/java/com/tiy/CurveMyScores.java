package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/11/16.
 */
public class CurveMyScores {

    // Curve 1: Adds any amount of extra credit to each grade
    public ArrayList<Integer> curveByAddingExtraCredit(ArrayList<Integer> gradesToCurve, int amountToAdd) {
        for (int index = 0; index < gradesToCurve.size(); index++) {
            gradesToCurve.set(index, gradesToCurve.get(index) + amountToAdd);
        }

        return gradesToCurve;
    }


    // Curve 2: Curves the highest grade to 100 and adds the difference to each grade
    public ArrayList<Integer> curveFlat(ArrayList<Integer> gradesToCurve) {
        //Make sure all grades are valid? I think do this in controller instead.

        // Find maximum of list passed in
        int maxGrade = getMaxScore(gradesToCurve);

        //Find the difference between maxGrade and 100
        int flatCurveAmount = 100 - maxGrade;

        //Add the difference to every grade in the list
        for (int index = 0; index < gradesToCurve.size(); index++) {
            gradesToCurve.set(index, gradesToCurve.get(index) + flatCurveAmount);
        }

        return gradesToCurve;
    }


    // Curve 3: Curves the highest grade to 100% and other grades are computed as a percentage of the highest grade
    public ArrayList<Integer> curveAsPercentageOfHighestGrade(ArrayList<Integer> gradesToCurve) {
        int maxScore = getMaxScore(gradesToCurve);
        double newGrade;
        int newGradeInt;
        for (int index = 0; index < gradesToCurve.size(); index++) {
            newGrade = ((double)(gradesToCurve.get(index) * 100) / maxScore);
            newGradeInt = (int)Math.round(newGrade);
            gradesToCurve.set(index, newGradeInt);
        }
        return gradesToCurve;
    }

    // Curve 4: Multiply the square root of the grade by 10.
    // (Rationalization: Take 81, for example. 81 is the same as 9 * 9.
    //                   Instead of giving them 9 * 9, we give them 10 * 9.
    //                   10 is √(100), where 100 is the highest grade.
    public ArrayList<Integer> curveByTakingRoot(ArrayList<Integer> gradesToCurve) {
        double newGrade;
        int newGradeInt;
        for (int index = 0; index < gradesToCurve.size(); index++) {
            newGrade = (10 * Math.sqrt(gradesToCurve.get(index)));
            newGradeInt = (int)Math.round(newGrade);
            gradesToCurve.set(index, newGradeInt);
        }
        return gradesToCurve;
    }


    // returns the highest grade in the array list
    public int getMaxScore(ArrayList<Integer> grades) {
        int maxGrade = grades.get(0);
        for (int index = 1; index < grades.size(); index++) {
            if (maxGrade < grades.get(index)) {
                maxGrade = grades.get(index);
            }
        }
        return maxGrade;
    }
}
