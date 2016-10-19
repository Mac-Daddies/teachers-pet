package com.tiy;


import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
public class EmailSender {
    String apiKey;

    public void sendEmail(String emailFrom, String subject, String emailTo, String emailContent) throws IOException {

//        com.sendgrid.Email from = new com.sendgrid.Email("studentreports@teacherspet.com");
        Email from = new Email(emailFrom);
//        com.sendgrid.Email to = new com.sendgrid.Email(studentContainer.getStudent().getParentEmail());
        Email to = new Email(emailTo);
        Content content = new Content("text/plain", emailContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"), false);
        apiKey = System.getenv("SENDGRID_API_KEY");
        System.out.println(apiKey);
        try {
            System.out.println("\nIn emailSender...");
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("api.sendgrid.com/v3/mail/send");
            request.addHeader("Authorization", "Bearer " + System.getenv("SENDGRID_API_KEY"));
            request.setBody(mail.build());
            Response response = sg.makeCall(request);
            System.out.println("Status code: " + response.statusCode);
            System.out.println("Body: " + response.body);
            System.out.println("Headers: " + response.headers);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}