package com.cleaningservice.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

	
    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Cleaning Service OTP");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);
    }
}