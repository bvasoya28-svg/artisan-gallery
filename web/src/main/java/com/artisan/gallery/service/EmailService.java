package com.artisan.gallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your Artisan Gallery Verification Code");
            message.setText("Welcome to The Artisan's Gallery!\n\n" +
                           "Your verification code is: " + otp + "\n\n" +
                           "Please enter this code on the registration page to complete your account setup.\n\n" +
                           "Happy Shopping!");
            System.out.println("DEBUG: Sending OTP " + otp + " to " + toEmail);
            mailSender.send(message);
            System.out.println("DEBUG: Email sent successfully!");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw e; // This will let the Controller know there was an error
        }
    }
}
