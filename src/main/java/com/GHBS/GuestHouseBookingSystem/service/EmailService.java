package com.GHBS.GuestHouseBookingSystem.service;

import com.GHBS.GuestHouseBookingSystem.entity.Booking;
import com.GHBS.GuestHouseBookingSystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String adminEmail = "12202080603007@adit.ac.in"; // TODO: Move to config

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

    public void sendBookingPendingUser(User user, Booking booking) {
        String subject = "Your booking request has been received";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s is pending approval.\n\nThank you!",
                user.getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendBookingPendingAdmin(Booking booking) {
        String subject = "Booking Request - Pending Approval";
        String body = String.format(
                "Booking ID: %d is pending approval.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s",
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        sendEmail(adminEmail, subject, body);
    }

    public void sendBookingApprovedUser(User user, Booking booking) {
        String subject = "Your booking has been approved";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s has been approved.\n\nThank you!",
                user.getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendBookingApprovedAdmin(Booking booking) {
        String subject = "Booking Approved Notification";
        String body = String.format(
                "Booking ID: %d has been approved.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s",
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        sendEmail(adminEmail, subject, body);
    }

    public void sendBookingRejectedUser(User user, Booking booking, String reason) {
        String subject = "Your booking has been rejected";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s has been rejected.\nReason: %s\n\nThank you!",
                user.getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                reason);
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendBookingRejectedAdmin(Booking booking, String reason) {
        String subject = "Booking Rejected Notification";
        String body = String.format(
                "Booking ID: %d has been rejected.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s\nReason: %s",
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                reason);
        sendEmail(adminEmail, subject, body);
    }

    @Async
    public void sendCancellationNotification(User user, Booking booking) {
        String subject = "Booking Cancellation Confirmation";
        String body = String.format(
                "Your booking #%d for %s has been cancelled successfully.",
                booking.getId(), booking.getRoom().getName());
        sendEmail(user.getEmail(), subject, body);
    }
}