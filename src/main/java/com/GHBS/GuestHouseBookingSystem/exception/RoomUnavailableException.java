package com.GHBS.GuestHouseBookingSystem.exception;

public class RoomUnavailableException extends RuntimeException {
    public RoomUnavailableException(String message) {
        super(message);
    }
}

