package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.dto.AuthRequest;
import com.GHBS.GuestHouseBookingSystem.dto.AuthResponse;
import com.GHBS.GuestHouseBookingSystem.dto.RegisterRequest;
import com.GHBS.GuestHouseBookingSystem.entity.Role;
import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.RoleRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.EmailService;
import com.GHBS.GuestHouseBookingSystem.security.JwtUtils;
import com.GHBS.GuestHouseBookingSystem.service.interfac.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController //combines controller + response body(returns a json response)
@CrossOrigin(origins = "http://localhost:3000") //CORS (Cross-Origin Resource Sharing) allows your API to be accessed from different domains
@RequestMapping("/api/auth") //base URL for authentication, all endpoints will start with this
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registration
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request, @RequestParam(name = "role", defaultValue = "USER") String roleName) {
        return authService.register(request, roleName);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendForgotPasswordEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        // Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            // For security, do not reveal if email exists
            return ResponseEntity.ok("If an account with this email exists, a reset link has been sent.");
        }

        User user = optionalUser.get();

        // Generate token and expiry
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Build reset link (adjust the frontend URL as needed)
        String resetLink = "http://localhost:3000/reset-password/" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        // Always return a generic message for security
        return ResponseEntity.ok("If an account with this email exists, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        System.out.println("Token: " + token + " | Password: " + newPassword);
        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "Token and new password are required."));
        }

        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "Invalid or expired reset token."));
        }

        User user = optionalUser.get();

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("message", "Reset token has expired."));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password has been reset successfully."));
    }
}