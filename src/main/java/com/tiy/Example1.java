package com.tiy;


import com.sendgrid.*;



import java.io.IOException;
import java.util.Map;

public class Example1 {
    public static void main(String[] args) {
        String apiKey = System.getenv("SENDGRID_API_KEY");
        System.out.println(apiKey);

    }

    public static void main1(String[] args) throws IOException {

        Email from = new Email("teacher@tester.co");
        String subject = "Hello World from the SendGrid Java Library!";
        Email to = new Email("yehia830@gmail.com");
        Content content = new Content("text/plain", "CLICK HERE FOR SOME FUN");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("API"), false);
        // wpFCAeQIR4iOf_YsurKn0g

        System.out.println("---------");
        System.getenv("API");
        System.out.println("---------");

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("api.sendgrid.com/v3/mail/send");
            request.addHeader("Authorization:" , "Bearer" + System.getenv("API"));
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