package com.tiy;

import javax.persistence.*;

/**
 * Created by jessicatracy on 10/7/16.
 */
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String subject;

    @Column(nullable = false)
    int gradeLevel;

    @ManyToOne
    Teacher teacher;


    // Constructors
    public Course() {
    }

    public Course(String name, String subject, int gradeLevel, Teacher teacher) {
        this.name = name;
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.teacher = teacher;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
