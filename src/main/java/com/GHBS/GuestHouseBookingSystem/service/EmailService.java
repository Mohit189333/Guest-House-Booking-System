package com.GHBS.GuestHouseBookingSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@guesthouse.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Password Reset Request";
        String body = "To reset your password, click the following link:\n" + resetLink +
                "\n\nIf you didn't request this, ignore this email.";
        sendEmail(toEmail, subject, body);
    }

    public void sendBookingConfirmation(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }

    public void sendMailToAdmin(String toEmail,String subject, String body) {
        sendEmail(toEmail, subject, body);
    }

    @Async
    public void sendCancellationNotification(String email, Long bookingId, String roomName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Booking Cancellation Confirmation");
        message.setText(String.format(
                "Your booking #%d for %s has been cancelled successfully.",
                bookingId, roomName
        ));
        mailSender.send(message);
    }
}