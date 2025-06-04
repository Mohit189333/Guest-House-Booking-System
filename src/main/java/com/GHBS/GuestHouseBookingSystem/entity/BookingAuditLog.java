package com.GHBS.GuestHouseBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "booking_audit_log")
public class BookingAuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    private Long bookingId;
    private Long userId;
    private String username;
    private String userEmail;
    private Long roomId;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private String rejectionReason;
    private String action;
    private LocalDateTime actionTime;
    // getters/setters
}
