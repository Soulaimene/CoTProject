package com.lifeguardian.lifeguardian.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    // Update these values with your email configuration
    private static final String EMAIL_USERNAME = System.getenv("EmailCotProject");

    private static final String EMAIL_PASSWORD =System.getenv("EmailPswdCotProject") ;
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        // Create a MimeMessage
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        // Send the message
        Transport.send(message);
    }

    public static void main(String[] args) {
        try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(EMAIL_USERNAME, "Test Subject", "Hello, this is a test email!");
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
