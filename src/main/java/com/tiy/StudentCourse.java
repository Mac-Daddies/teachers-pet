package com.tiy;

import javax.persistence.*;

/**
 * Created by jessicatracy on 10/11/16.
 * Links together a student with the courses he/she is in AND a course with all of its students.
 */
@Entity
@Table(name="studentCourses")
public class StudentCourse {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    Student student;

    @ManyToOne
    Course course;

    public StudentCourse() {
    }

    public StudentCourse(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    //Getters and setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}

