package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.BookingRequest;
import com.GHBS.GuestHouseBookingSystem.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest bookingRequest);

    List<BookingResponse> getUserBookings();

    List<BookingResponse> getPendingBookings();

    BookingResponse approveBooking(Long id);

    BookingResponse rejectBooking(Long id, String reason);

    void cancelBooking(Long id, String username);
}
