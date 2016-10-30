package com.tiy;

/**
 * Created by jessicatracy on 10/24/16.
 * Used for returning email settings data for the "Edit email settings" button on the classlist screen
 */
public class EmailContentContainer {
    int highAverage;
    String emailSignature;

    public EmailContentContainer() {
    }

    public EmailContentContainer(int highAverage, String emailSignature) {
        this.highAverage = highAverage;
        this.emailSignature = emailSignature;
    }

    //Getters and setters
    public int getHighAverage() {
        return highAverage;
    }

    public void setHighAverage(int highAverage) {
        this.highAverage = highAverage;
    }

    public String getEmailSignature() {
        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignature = emailSignature;
    }
}
