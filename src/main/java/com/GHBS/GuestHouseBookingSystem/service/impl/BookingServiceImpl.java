package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.BookingRequest;
import com.GHBS.GuestHouseBookingSystem.dto.BookingResponse;
import com.GHBS.GuestHouseBookingSystem.entity.*;
import com.GHBS.GuestHouseBookingSystem.repo.BookingRepository;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.EmailService;
import com.GHBS.GuestHouseBookingSystem.service.NotificationService;
import com.GHBS.GuestHouseBookingSystem.service.interfac.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;


    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if room is available for the requested dates
        List<Booking> conflictingBookings = bookingRepository.findByRoomIdAndCheckOutDateAfterAndCheckInDateBefore(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate());

        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("The selected room is not available for the requested dates");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Send email notification
        String subject = "Your booking request has been received";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s is pending approval.\n\nThank you!",
                user.getUsername(),
                room.getName(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate());
        emailService.sendBookingConfirmation(user.getEmail(), subject, body);

        String adminEmail = "12202080603007@adit.ac.in"; // ✅ Replace this with your actual admin email or fetch from config
        String adminSubject = "Booking Request";
        String adminBody = String.format("Booking ID: %d has been rejected by the system.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s",
                booking.getId(),
                user.getUsername(),
                room.getName(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate());

        emailService.sendMailToAdmin(adminEmail, adminSubject, adminBody);
        return convertToDto(savedBooking);
    }

    public List<BookingResponse> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.APPROVED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Send approval email
        String subject = "Your booking has been approved";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s has been approved.\n\nThank you!",
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        emailService.sendBookingConfirmation(booking.getUser().getEmail(), subject, body);

        String adminEmail = "12202080603007@adit.ac.in"; // ✅ Replace this with your actual admin email or fetch from config
        String adminSubject = "Booking Approved Notification";
        String adminBody = String.format("Booking ID: %d has been rejected by the system.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s",
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());

        emailService.sendMailToAdmin(adminEmail, adminSubject, adminBody);
        return convertToDto(updatedBooking);
    }

    @Transactional
    public BookingResponse rejectBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectionReason(reason);
        Booking updatedBooking = bookingRepository.save(booking);

        // Send rejection email
        String subject = "Your booking has been rejected";
        String body = String.format("Dear %s,\n\nYour booking for room '%s' from %s to %s has been rejected.\nReason: %s\n\nThank you!",
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                reason);
        emailService.sendBookingConfirmation(booking.getUser().getEmail(), subject, body);

        String adminEmail = "12202080603007@adit.ac.in"; // ✅ Replace this with your actual admin email or fetch from config
        String adminSubject = "Booking Rejected Notification";
        String adminBody = String.format("Booking ID: %d has been rejected by the system.\n\nUser: %s\nRoom: %s\nFrom: %s\nTo: %s\nReason: %s",
                booking.getId(),
                booking.getUser().getUsername(),
                booking.getRoom().getName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                reason);

        emailService.sendMailToAdmin(adminEmail, adminSubject, adminBody);

        return convertToDto(updatedBooking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String username) {
        // 1. Find the booking
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // 2. Verify ownership
        if (!booking.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("You can only cancel your own bookings");
        }

        // 3. Check if cancellation is allowed
        if (booking.getStatus() != BookingStatus.PENDING &&
                booking.getStatus() != BookingStatus.APPROVED) {
            throw new BusinessLogicException("Only PENDING or APPROVED bookings can be cancelled");
        }

        // 4. Check if check-in date hasn't passed
        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("Cannot cancel booking after check-in date");
        }

        // 5. Update status instead of deleting (for audit trail)
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // 6. Send notification (optional)
        emailService.sendCancellationNotification(
                booking.getUser().getEmail(),
                booking.getId(),
                booking.getRoom().getName()
        );
    }

    private BookingResponse convertToDto(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setRoomId(booking.getRoom().getId());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setStatus(booking.getStatus());
        response.setRejectionReason(booking.getRejectionReason());
        return response;
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public class BusinessLogicException extends RuntimeException {
        public BusinessLogicException(String message) {
            super(message);
        }
    }
}