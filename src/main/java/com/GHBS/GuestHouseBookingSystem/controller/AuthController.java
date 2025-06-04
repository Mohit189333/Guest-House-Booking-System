package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.entity.Role;
import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.RoleRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.EmailService;
import com.GHBS.GuestHouseBookingSystem.security.JwtUtils;
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")//      http://localhost:8080/api/auth/register?role=${role}
    public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam(name = "role", defaultValue = "USER") String roleName) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Check if the role is ADMIN and there is already an admin in the system
        if ("ADMIN".equalsIgnoreCase(roleName)) {
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                throw new RuntimeException("Error: Role 'ADMIN' is not found in the database. Please initialize roles.");
            }

            // Check if an admin already exists
            if (userRepository.existsByRolesContaining(adminRole)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("An admin user already exists. Registration for additional admins is not allowed.");
            }

            user.setRoles(List.of(adminRole));
        } else {
            // Default role is USER
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                throw new RuntimeException("Error: Role 'USER' is not found in the database. Please initialize roles.");
            }
            user.setRoles(List.of(userRole));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Extract roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        // Generate JWT token with username and roles
        return ResponseEntity.ok(jwtUtils.generateToken(userDetails.getUsername(), roles));
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

        // Send email
        String subject = "Password Reset Request";
        String text = "To reset your password, click the link below:\n" + resetLink +
                "\n\nIf you didn't request a password reset, you can ignore this email.";

        emailService.sendEmail(user.getEmail(), subject, text);

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