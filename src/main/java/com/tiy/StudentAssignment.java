package com.tiy;

import javax.persistence.*;

/**
 * Created by jessicatracy on 10/7/16.
 * Links a student to all of his/her assignments AND an assignmnet to all students who have that assignment.
 */
@Entity
@Table(name = "studentAssignments")
public class StudentAssignment {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    Student student;

    @ManyToOne
    Assignment assignment;

    @Column(nullable = false)
    int grade;


    // Constructors
    public StudentAssignment() {
    }

    public StudentAssignment(Student student, Assignment assignment, int grade) {
        this.student = student;
        this.assignment = assignment;
        this.grade = grade;
    }


    // Getters and setters
    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
