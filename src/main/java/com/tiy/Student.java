package com.tiy;

import javax.persistence.*;

/**
 * Created by jessicatracy on 10/7/16.
 */
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Column(nullable = false)
    String parentEmail;


    // Constructors
    public Student() {
    }

    public Student(String firstName, String lastName, String parentEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentEmail = parentEmail;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
}
