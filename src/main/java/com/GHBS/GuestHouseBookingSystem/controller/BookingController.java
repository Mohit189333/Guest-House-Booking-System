package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.dto.BookingRequest;
import com.GHBS.GuestHouseBookingSystem.dto.BookingResponse;
import com.GHBS.GuestHouseBookingSystem.entity.BookingStatus;
import com.GHBS.GuestHouseBookingSystem.service.interfac.BookingService;
import com.GHBS.GuestHouseBookingSystem.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest bookingRequest) {

        BookingResponse response = bookingService.createBooking(bookingRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        List<BookingResponse> responses = bookingService.getUserBookings();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        bookingService.cancelBooking(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getPendingBookings() {
        List<BookingResponse> responses = bookingService.getPendingBookings();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> approveBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.approveBooking(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> rejectBooking(@PathVariable Long id, @RequestParam String reason) {
        BookingResponse response = bookingService.rejectBooking(id, reason);
        return ResponseEntity.ok(response);
    }
}