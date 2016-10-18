package com.tiy;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/18/16.
 */
public class EmailCustomContent {
    EmailSender myEmailSender = new EmailSender();
    public void createGeneralStudentEmail(Course course, Teacher teacher, StudentContainer studentContainer, StudentAssignmentRepository studentAssignmentRepository) throws IOException {
        String emailFrom = teacher.getEmail();
        String subject = course.getName() + ": Update on student progress for " + studentContainer.getStudent().getFirstName();
        String emailTo = studentContainer.getStudent().getParentEmail();
        String emailContent = "To the parent/guardian of " + studentContainer.getStudent().getFirstName() + " " + studentContainer.getStudent().getLastName() + ",\n\n" +
                "Your student currently has an average of " + studentContainer.getAverage() + "%.\n\n";

        ArrayList<StudentAssignment> allStudentAssignments = studentAssignmentRepository.findAllByStudent(studentContainer.getStudent());
        ArrayList<StudentAssignment> studentAssignmentsWithZeros = new ArrayList<>();
        for (StudentAssignment currentStudentAssignment : allStudentAssignments) {
            if (currentStudentAssignment.getGrade() == 0) {
                studentAssignmentsWithZeros.add(currentStudentAssignment);
            }
        }

        if (studentAssignmentsWithZeros.size() > 0) {
            //display missing assignments
            emailContent += studentContainer.getStudent().getFirstName() + "'s missing assignments:\n";
            int counter = 0;
            for (StudentAssignment currentStudentAssignment : studentAssignmentsWithZeros) {
                emailContent += counter + ". " + currentStudentAssignment.getAssignment().getName() + ", due on " + currentStudentAssignment.getAssignment().getDueDate() + "\n";
                counter++;
            }
        } else {
            emailContent += studentContainer.getStudent().getFirstName() + " has no missing assignments. Great job!";
        }

        emailContent += "\n\nPlease contact me at " + teacher.getEmail() + "with any concerns. Thank you!\n\n" +
                teacher.getFirstName() + " " + teacher.getLastName();

        //send the email!
        myEmailSender.sendEmail(emailFrom, subject, emailTo, emailContent);
        System.out.println("General email sent for " + studentContainer.getStudent().getFirstName());
    }
}
