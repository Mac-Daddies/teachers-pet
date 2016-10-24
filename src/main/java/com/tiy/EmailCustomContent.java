package com.tiy;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jessicatracy on 10/18/16.
 */
public class EmailCustomContent {
    EmailSender myEmailSender = new EmailSender();
    String emailSignatureGlobal;

    public static int highAverageAmount = 95;

    public static void setHighAverageAmount(int newHighAverage) {
        highAverageAmount = newHighAverage;
    }

    public void sendEmailOneStudent(Course course, Teacher teacher, StudentContainer studentContainer, StudentAssignmentRepository studentAssignmentRepository) throws IOException {
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
            String dueDate;
            emailContent += studentContainer.getStudent().getFirstName() + "'s missing assignments:\n";
            int counter = 0;
            for (StudentAssignment currentStudentAssignment : studentAssignmentsWithZeros) {
                dueDate = formatDueDate(currentStudentAssignment.getAssignment().getDueDate());
                emailContent += " - " + currentStudentAssignment.getAssignment().getName() + ", due on " + dueDate + "\n";
            }
        } else {
            emailContent += studentContainer.getStudent().getFirstName() + " has no missing assignments. Great job!";
        }

        emailContent += "\n\n" + getEmailSignature(teacher);

        //send the email!
        myEmailSender.sendEmail(emailFrom, subject, emailTo, emailContent);
        System.out.println("General email sent for " + studentContainer.getStudent().getFirstName() + " to email " + emailTo);
    }

    public void sendEmailForAllZeros(Course course, Teacher teacher, ArrayList<StudentContainer> studentContainers, StudentAssignmentRepository studentAssignmentRepository) throws IOException {
        String emailFrom = teacher.getEmail();
        //send an email for each student who has zeros!!
        //Make an arraylist of studentAssignments that have grade of zero for each student.
        for (StudentContainer currentStudentContainer : studentContainers) {
            ArrayList<StudentAssignment> currentStudentsStudentAssignments = studentAssignmentRepository.findAllByStudent(currentStudentContainer.getStudent());
            ArrayList<StudentAssignment> currentStudentsStudentAssignmentsWithGradeOfZero = new ArrayList<>();
            for (StudentAssignment currentStudentsCurrentStudentAssignment : currentStudentsStudentAssignments) {
                if (currentStudentsCurrentStudentAssignment.getGrade() == 0) {
                    currentStudentsStudentAssignmentsWithGradeOfZero.add(currentStudentsCurrentStudentAssignment);
                }
            }
            //if this student has assignments that have a grade of 0, send an email out.
            if (currentStudentsStudentAssignmentsWithGradeOfZero.size() > 0) {
                String subject = course.getName() + ": Missing assignments from " + currentStudentContainer.getStudent().getFirstName();
                String emailTo = currentStudentContainer.getStudent().getParentEmail();
                String dueDate;
                String emailContent = "To the parent/guardian of " + currentStudentContainer.getStudent().getFirstName() + " " + currentStudentContainer.getStudent().getLastName() + ",\n\n" +
                        "This email is to let you know that " + currentStudentContainer.getStudent().getFirstName() + " has " + currentStudentsStudentAssignmentsWithGradeOfZero.size() + " missing assignment(s):\n";
                for (StudentAssignment currentStudentAssignment : currentStudentsStudentAssignmentsWithGradeOfZero) {
                    dueDate = formatDueDate(currentStudentAssignment.getAssignment().getDueDate());
                    emailContent += " - " + currentStudentAssignment.getAssignment().getName() + ", due on " + dueDate + "\n";
                }
                emailContent += "\n\n" + getEmailSignature(teacher);

                myEmailSender.sendEmail(emailFrom, subject, emailTo, emailContent);
            }
        }
    }

    public void sendEmailForAllHighAverages(Course course, Teacher teacher, ArrayList<StudentContainer> studentContainers, StudentAssignmentRepository studentAssignmentRepository) throws IOException {
        System.out.println("In sendEmailForAllHighAverages method in EmailCustomContent...");
        String emailFrom = teacher.getEmail();
        String subject;
        String emailTo;
        String emailContent;
        //send an email for each student who has an average over highAverageAmount.
        //Make an arraylist of studentAssignments that have grade of zero for each student.
        for (StudentContainer currentStudentContainer : studentContainers) {
            System.out.println(currentStudentContainer.getStudent().getFirstName() + "'s average: " + currentStudentContainer.getAverage());
            if(currentStudentContainer.getAverage() > highAverageAmount) {
                //send email
                subject = course.getName() + ": Great job " + currentStudentContainer.getStudent().getFirstName() + "!";
                emailTo = currentStudentContainer.getStudent().getParentEmail();
                emailContent = getEmailContentForHighAverages(currentStudentContainer, teacher);
                myEmailSender.sendEmail(emailFrom, subject, emailTo, emailContent);
            }
        }
    }

    public String formatDueDate(String dueDate) {
        String dateString = dueDate.substring(5, 7) + "/" + dueDate.substring(8, 10) + "/" + dueDate.substring(0, 4);
        return dateString;

    }

    public String getEmailContentForHighAverages(StudentContainer studentContainer, Teacher teacher) {
        String emailContent = "To the parent/guardian of " + studentContainer.getStudent().getFirstName() + " " + studentContainer.getStudent().getLastName() + ",\n\n" +
                "This email is to let you know that your student is doing exceptionally well in my class!\n" +
                studentContainer.getStudent().getFirstName() + " currently has an average of " + studentContainer.getAverage() + "%. " +
                "Keep up the great work!\n\n" + getEmailSignature(teacher);

        return emailContent;
    }

    public String getEmailSignature(Teacher teacher) {
        String emailSignature;
        if (emailSignatureGlobal != null) {
            emailSignature = emailSignatureGlobal;
        } else {
            emailSignature = "Please contact me at " + teacher.getEmail() + " with any concerns. Thank you!\n\n" +
                    teacher.getFirstName() + " " + teacher.getLastName();
        }

        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignatureGlobal = emailSignature;
    }
}
