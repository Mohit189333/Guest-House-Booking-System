package com.GHBS.GuestHouseBookingSystem.dto;

import java.time.LocalDate;
import java.util.List;

public class BookingRequest {
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<Long> bedIds;

    public List<Long> getBedIds() {
        return bedIds;
    }

    public void setBedIds(List<Long> bedIds) {
        this.bedIds = bedIds;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}