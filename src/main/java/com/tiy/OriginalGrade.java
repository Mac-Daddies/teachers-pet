package com.tiy;

import javax.persistence.*;

/**
 * Created by jessicatracy on 10/17/16.
 * Holds a record of the grade information for a single student on a single assignment the first time grade info
 * is entered.
 */
@Entity
@Table(name = "originalGrades")
public class OriginalGrade {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    Assignment assignment;

    @ManyToOne
    Student student;

    @Column
    int grade;

    public OriginalGrade() {
    }

    public OriginalGrade(Student student, Assignment assignment, int grade) {
        this.student = student;
        this.assignment = assignment;
        this.grade = grade;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
