package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.entity.BookingAuditLog;
import com.GHBS.GuestHouseBookingSystem.repo.BookingAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/booking-logs")
@PreAuthorize("hasRole('ADMIN')")
public class BookingLogController {
    @Autowired
    private BookingAuditLogRepository bookingAuditLogRepository;

    @GetMapping
    public List<BookingAuditLog> getAllLogs() {
        return bookingAuditLogRepository.findAllByOrderByActionTimeDesc();
    }
}