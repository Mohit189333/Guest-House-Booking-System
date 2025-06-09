package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.AuthRequest;
import com.GHBS.GuestHouseBookingSystem.dto.AuthResponse;
import com.GHBS.GuestHouseBookingSystem.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthResponse> login(AuthRequest authRequest);
    ResponseEntity<String> register(RegisterRequest registerRequest, String roleName);
}