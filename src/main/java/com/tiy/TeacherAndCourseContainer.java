package com.tiy;

import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/19/16.
 */
public class TeacherAndCourseContainer {
    Teacher teacher;
    ArrayList<Course> courses;

    public TeacherAndCourseContainer() {
    }

    public TeacherAndCourseContainer(Teacher teacher, ArrayList<Course> courses) {
        this.teacher = teacher;
        this.courses = courses;
    }

    //Getters and setters
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
