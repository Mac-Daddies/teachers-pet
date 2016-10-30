package com.tiy;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jessicatracy on 10/11/16.
 * Holds all the methods to curve grades, which are always taken as arraylists of integers.
 */
public class CurveMyScores {
    // Curve 1: Adds any amount of extra credit to each grade
    public ArrayList<Integer> curveByAddingExtraCredit(ArrayList<Integer> gradesToCurve, int amountToAdd) {
        for (int index = 0; index < gradesToCurve.size(); index++) {
            if (gradesToCurve.get(index) != -1) {
                gradesToCurve.set(index, gradesToCurve.get(index) + amountToAdd);
            }
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
            if (gradesToCurve.get(index) != -1) {
                gradesToCurve.set(index, gradesToCurve.get(index) + flatCurveAmount);
            }
        }

        return gradesToCurve;
    }


    // Curve 3: Curves the highest grade to 100% and other grades are computed as a percentage of the highest grade
    public ArrayList<Integer> curveAsPercentageOfHighestGrade(ArrayList<Integer> gradesToCurve) {
        int maxScore = getMaxScore(gradesToCurve);
        double newGrade;
        int newGradeInt;
        for (int index = 0; index < gradesToCurve.size(); index++) {
            //Only curve if the student has been graded on assignment (if grade not -1)
            if (gradesToCurve.get(index) != -1) {
                newGrade = ((double) (gradesToCurve.get(index) * 100) / maxScore);
                newGradeInt = (int) Math.round(newGrade);
                gradesToCurve.set(index, newGradeInt);
            }
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
            //Only curve if the student has been graded on assignment (if grade not -1)
            if (gradesToCurve.get(index) != -1) {
                newGrade = (10 * Math.sqrt(gradesToCurve.get(index)));
                newGradeInt = (int) Math.round(newGrade);
                gradesToCurve.set(index, newGradeInt);
            }
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

    // returns the average of a set of grades
    public int getAverage(ArrayList<Integer> grades) {
        double sum = 0;
        int countOfNegativeOnes = 0;
        for (int currentGrade : grades) {
            if (currentGrade != -1) {
                sum += currentGrade;
            } else {
                countOfNegativeOnes++;
            }
        }

        //If every grade is a negative one, return -1 instead of an average of 0. Else, return the average of all grades that are not -1.
        if (countOfNegativeOnes == grades.size()) {
            return -1;
        } else {
            int average = (int)Math.round(sum / (double)(grades.size() - countOfNegativeOnes));
            return average;
        }
    }

}
