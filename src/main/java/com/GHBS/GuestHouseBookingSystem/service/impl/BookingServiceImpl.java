package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.BookingRequest;
import com.GHBS.GuestHouseBookingSystem.dto.BookingResponse;
import com.GHBS.GuestHouseBookingSystem.entity.*;
import com.GHBS.GuestHouseBookingSystem.exception.BusinessLogicException;
import com.GHBS.GuestHouseBookingSystem.exception.ResourceNotFoundException;
import com.GHBS.GuestHouseBookingSystem.exception.RoomUnavailableException;
import com.GHBS.GuestHouseBookingSystem.exception.UnauthorizedAccessException;
import com.GHBS.GuestHouseBookingSystem.repo.BookingRepository;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.EmailService;
import com.GHBS.GuestHouseBookingSystem.service.interfac.BookingService;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

        //check if room is approved than not to allow other booking request
        List<Booking> approvedBookings = bookingRepository
                .findByRoomIdAndStatusAndCheckOutDateAfterAndCheckInDateBefore(
                        room.getId(),
                        BookingStatus.APPROVED,
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate());

        if (!approvedBookings.isEmpty()) {
            throw new RoomUnavailableException("The selected room is not available for the requested dates");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Send email notification
        emailService.sendBookingPendingUser(user, savedBooking);
        emailService.sendBookingPendingAdmin(savedBooking);

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
        emailService.sendBookingApprovedUser(updatedBooking.getUser(), updatedBooking);
        emailService.sendBookingApprovedAdmin(updatedBooking);

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
        emailService.sendBookingRejectedUser(updatedBooking.getUser(), updatedBooking, reason);
        emailService.sendBookingRejectedAdmin(updatedBooking, reason);

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

        // 6. Send notification
        emailService.sendCancellationNotification(booking.getUser(), booking);

    }

    private BookingResponse convertToDto(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setRoomId(booking.getRoom().getId());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setStatus(booking.getStatus());
        response.setRejectionReason(booking.getRejectionReason());
        response.setRoomName(booking.getRoom().getName());
        response.setUserName(booking.getUser().getUsername());

        return response;
    }

}