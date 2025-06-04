package com.GHBS.GuestHouseBookingSystem.repo;

import com.GHBS.GuestHouseBookingSystem.entity.Booking;
import com.GHBS.GuestHouseBookingSystem.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByRoomIdAndCheckOutDateAfterAndCheckInDateBefore(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT DISTINCT b.room.id FROM Booking b " +
            "WHERE b.status <> 'REJECTED' AND b.status <> 'CANCELLED' " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Long> findRoomIdsWithConflictingBookings(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);
}