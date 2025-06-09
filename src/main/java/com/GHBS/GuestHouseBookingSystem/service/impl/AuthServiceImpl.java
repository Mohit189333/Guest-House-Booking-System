package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.AuthRequest;
import com.GHBS.GuestHouseBookingSystem.dto.AuthResponse;
import com.GHBS.GuestHouseBookingSystem.dto.RegisterRequest;
import com.GHBS.GuestHouseBookingSystem.entity.Role;
import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.RoleRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

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

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        String token = jwtUtils.generateToken(userDetails.getUsername(), roles);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public ResponseEntity<String> register(RegisterRequest request, String roleName) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if ("ADMIN".equalsIgnoreCase(roleName)) {
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                throw new RuntimeException("Error: Role 'ADMIN' is not found in the database. Please initialize roles.");
            }
            if (userRepository.existsByRolesContaining(adminRole)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("An admin user already exists. Registration for additional admins is not allowed.");
            }
            user.setRoles(List.of(adminRole));
        } else {
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                throw new RuntimeException("Error: Role 'USER' is not found in the database. Please initialize roles.");
            }
            user.setRoles(List.of(userRole));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
}