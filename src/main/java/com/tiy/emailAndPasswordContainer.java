package com.tiy;

/**
 * Created by Yehia830 on 10/10/16.
 */
public class emailAndPasswordContainer {
    String email;
    String password;

    public emailAndPasswordContainer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public emailAndPasswordContainer() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
