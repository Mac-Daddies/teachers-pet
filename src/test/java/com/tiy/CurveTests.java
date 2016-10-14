package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by jessicatracy on 10/6/16.
 */
public class CurveTests {
    CurveMyScores myCurver = new CurveMyScores();
    ArrayList<Integer> listOfGradesToCurve;
    ArrayList<Integer> curvedGrades;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    // Tests for getMaxScore
    @Test
    public void testGetMaxScoreHighestLast() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(88, 89, 83, 90));
        int highestGrade = myCurver.getMaxScore(listOfGradesToCurve);
        assertEquals(90, highestGrade);
    }

    @Test
    public void testGetMaxScoreHighestFirst() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(99, 72, 83, 97));
        int highestGrade = myCurver.getMaxScore(listOfGradesToCurve);
        assertEquals(99, highestGrade);
    }

    @Test
    public void testGetMaxScoreHighestMiddle() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(50, 42, 48));
        int highestGrade = myCurver.getMaxScore(listOfGradesToCurve);
        assertEquals(50, highestGrade);
    }

    @Test
    public void testGetMaxScoreAllSame() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(50, 50, 50));
        int highestGrade = myCurver.getMaxScore(listOfGradesToCurve);
        assertEquals(50, highestGrade);
    }

    @Test
    public void testGetMaxScoreJustZero() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(0));
        int highestGrade = myCurver.getMaxScore(listOfGradesToCurve);
        assertEquals(0, highestGrade);
    }

    // Tests for curveByAddingExtraCredit
    @Test
    public void testAddExtraCredit() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(92, 67, 83, 90));
        curvedGrades = myCurver.curveByAddingExtraCredit(listOfGradesToCurve, 10);
        assertEquals(102, (int)curvedGrades.get(0));
        assertEquals(77, (int)curvedGrades.get(1));
        assertEquals(93, (int)curvedGrades.get(2));
        assertEquals(100, (int)curvedGrades.get(3));
    }

    @Test
    public void testAddExtraCreditOneGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(50));
        curvedGrades = myCurver.curveByAddingExtraCredit(listOfGradesToCurve, 5);
        assertEquals(55, (int)curvedGrades.get(0));
    }


    // Tests for curveFlat
    @Test
    public void testFlatCurve1() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(70, 75, 80, 89, 97));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(73, (int)curvedGrades.get(0));
        assertEquals(78, (int)curvedGrades.get(1));
        assertEquals(83, (int)curvedGrades.get(2));
        assertEquals(92, (int)curvedGrades.get(3));
        assertEquals(100, (int)curvedGrades.get(4));
    }

    @Test
    public void testFlatCurveAllSameGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(85, 85, 85));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
        assertEquals(100, (int)curvedGrades.get(2));
    }

    @Test
    public void testFlatCurveTwoHighestSameGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(41, 82, 82, 36));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(59, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
        assertEquals(100, (int)curvedGrades.get(2));
        assertEquals(54, (int)curvedGrades.get(3));
    }

    @Test
    public void testFlatCurveTwoLowestSameGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(99, 80, 80, 90));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(81, (int)curvedGrades.get(1));
        assertEquals(81, (int)curvedGrades.get(2));
        assertEquals(91, (int)curvedGrades.get(3));
    }

    @Test
    public void testFlatCurveOneGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(70));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
    }

    @Test
    public void testFlatCurveAll100() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(100, 100));
        curvedGrades = myCurver.curveFlat(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
    }


    // Tests for curveAsPercentageOfHighestGrade
    @Test
    public void testCurveAsPercentageOfHighestGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(92, 78, 85));
        curvedGrades = myCurver.curveAsPercentageOfHighestGrade(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(85, (int)curvedGrades.get(1));
        assertEquals(92, (int)curvedGrades.get(2));
    }

    @Test
    public void testCurveAsPercentageOfHighestGradeWith0() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(50, 80, 0));
        curvedGrades = myCurver.curveAsPercentageOfHighestGrade(listOfGradesToCurve);
        assertEquals(63, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
        assertEquals(0, (int)curvedGrades.get(2));
    }

    @Test
    public void testCurveAsPercentageOfHighestGradeAllSameGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(50, 50, 50, 50));
        curvedGrades = myCurver.curveAsPercentageOfHighestGrade(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
        assertEquals(100, (int)curvedGrades.get(2));
        assertEquals(100, (int)curvedGrades.get(3));
    }

    @Test
    public void testCurveAsPercentageOfHighestGradeAlready100() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(58, 100));
        curvedGrades = myCurver.curveAsPercentageOfHighestGrade(listOfGradesToCurve);
        assertEquals(58, (int)curvedGrades.get(0));
        assertEquals(100, (int)curvedGrades.get(1));
    }


    // Tests for curveByTakingRoot
    @Test
    public void testCurveByTakingRoot() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(58, 91, 63));
        curvedGrades = myCurver.curveByTakingRoot(listOfGradesToCurve);
        assertEquals(76, (int)curvedGrades.get(0));
        assertEquals(95, (int)curvedGrades.get(1));
        assertEquals(79, (int)curvedGrades.get(2));
    }

    @Test
    public void testCurveByTakingRootWith100() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(100, 81));
        curvedGrades = myCurver.curveByTakingRoot(listOfGradesToCurve);
        assertEquals(100, (int)curvedGrades.get(0));
        assertEquals(90, (int)curvedGrades.get(1));
    }

    @Test
    public void testCurveByTakingRootWith0AndRoundsUp() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(0, 42));
        curvedGrades = myCurver.curveByTakingRoot(listOfGradesToCurve);
        assertEquals(0, (int)curvedGrades.get(0));
        assertEquals(65, (int)curvedGrades.get(1));
    }

    @Test
    public void testAverage() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(70, 72, 61, 89, 100, 53, 79));
        int average = myCurver.getAverage(listOfGradesToCurve);
        assertEquals(75, average);
    }

    @Test
    public void testAverageWith0and100() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(0, 100));
        int average = myCurver.getAverage(listOfGradesToCurve);
        assertEquals(50, average);
    }

    @Test
    public void testAverageOneGrade() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(75));
        int average = myCurver.getAverage(listOfGradesToCurve);
        assertEquals(75, average);
    }

    @Test
    public void testAverageWithPoint5() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(30, 96, 72, 96));
        int average = myCurver.getAverage(listOfGradesToCurve);
        assertEquals(74, average);
    }

    @Test
    public void testAverageRoundDown() throws Exception {
        listOfGradesToCurve = new ArrayList<Integer>(Arrays.asList(28, 96, 72, 89));
        int average = myCurver.getAverage(listOfGradesToCurve);
        assertEquals(71, average);
    }

}