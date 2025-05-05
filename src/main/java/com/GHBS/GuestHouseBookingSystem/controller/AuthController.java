package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.entity.Role;
import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.RoleRepository;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Registration Endpoint
    @PostMapping("/register")//      http://localhost:8080/api/auth/register?role=${role}
    public String registerUser(@RequestBody User user, @RequestParam(name = "role", defaultValue = "USER") String roleName) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already exists!";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists!";
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
                return "An admin user already exists. Registration for additional admins is not allowed.";
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
        return "User registered successfully!";
    }

    // Login Endpoint
    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Extract roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        // Generate JWT token with username and roles
        return jwtUtils.generateToken(userDetails.getUsername(), roles);
    }

    @GetMapping("/home")
    public Map<String, Object> getUserDetails(Authentication authentication) {
        String username = authentication.getName(); // Get username from Authentication
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());
        userDetails.put("roles", user.getRoles().stream().map(role -> role.getName()).toList());

        return userDetails;
    }
}