package com.GHBS.GuestHouseBookingSystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private JavaMailSender mailSender;

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