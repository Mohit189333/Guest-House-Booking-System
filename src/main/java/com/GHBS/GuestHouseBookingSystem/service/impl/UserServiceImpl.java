package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.entity.User;
import com.GHBS.GuestHouseBookingSystem.repo.UserRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
            User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setEmail(userDetails.getEmail());
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found!");
    }
}
