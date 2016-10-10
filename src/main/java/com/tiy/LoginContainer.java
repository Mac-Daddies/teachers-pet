package com.tiy;

import java.util.ArrayList;

/**
 * Created by Yehia830 on 10/10/16.
 */
public class LoginContainer {
    String errorMessage;
    Teacher teacher;
    ArrayList<Course> courseArrayList = new ArrayList<Course>();

    public LoginContainer(String errorMessage, Teacher teacher, ArrayList<Course> courseArrayList) {
        this.errorMessage = errorMessage;
        this.teacher = teacher;
        this.courseArrayList = courseArrayList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Course> getCourseArrayList() {
        return courseArrayList;
    }

    public void setCourseArrayList(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }
}
