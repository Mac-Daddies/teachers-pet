package com.tiy;


import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
public class EmailSender {

    public void sendEmail(String emailFrom, String subject, String emailTo, String emailContent) throws IOException {

//        com.sendgrid.Email from = new com.sendgrid.Email("studentreports@teacherspet.com");
        Email from = new Email(emailFrom);
//        com.sendgrid.Email to = new com.sendgrid.Email(studentContainer.getStudent().getParentEmail());
        Email to = new Email(emailTo);
        Content content = new Content("text/plain", emailContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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", false);
        // wpFCAeQIR4iOf_YsurKn0g
        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("api.sendgrid.com/v3/mail/send");
            request.addHeader("Authorization", "Bearer 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");
            request.setBody(mail.build());
            Response response = sg.makeCall(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}