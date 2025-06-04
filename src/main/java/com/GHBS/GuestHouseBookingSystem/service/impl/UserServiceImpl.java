package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        if (userRepository.existsById(id)) {
            User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setRoles(userDetails.getRoles());
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found!");
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }



    @Override
    public User updateCurrentUser(Long id, User userDetails) {
        if (userRepository.existsById(id)) {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            // Check if the new username is taken by someone else
            if (!existingUser.getUsername().equals(userDetails.getUsername())) {
                if (userRepository.existsByUsername(userDetails.getUsername())) {
                    throw new RuntimeException("Username already exists!");
                }
            }

            // Check if the new email is taken by someone else
            if (!existingUser.getEmail().equals(userDetails.getEmail())) {
                if (userRepository.existsByEmail(userDetails.getEmail())) {
                    throw new RuntimeException("Email already exists!");
                }
            }

            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());

            // Only update password if a new one is provided and not empty
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found!");
    }
}
