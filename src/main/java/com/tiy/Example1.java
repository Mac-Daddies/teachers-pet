package com.tiy;


import com.sendgrid.*;



import java.io.IOException;

public class Example1 {
    public static void main(String[] args) throws IOException {
        Email from = new Email("yehia@tester.com");
        String subject = "Hello World from the SendGrid Java Library!";
        Email to = new Email("yehia830@gmail.com");
        Content content = new Content("text/plain", "You don't deserve her!!!");
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