package com.GHBS.GuestHouseBookingSystem.repo;

import com.GHBS.GuestHouseBookingSystem.entity.BookingAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingAuditLogRepository extends JpaRepository<BookingAuditLog, Long> {
    List<BookingAuditLog> findAllByOrderByActionTimeDesc();
}
