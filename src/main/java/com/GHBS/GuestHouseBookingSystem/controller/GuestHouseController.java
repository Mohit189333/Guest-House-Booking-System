package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.dto.GuestHouseDTO;
import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.GuestHouse;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.service.interfac.GuestHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest-houses")
public class GuestHouseController {

    @Autowired
    private GuestHouseService guestHouseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GuestHouseDTO> createGuestHouse(@RequestBody GuestHouse guestHouse) {
        return ResponseEntity.ok(guestHouseService.createGuestHouse(guestHouse));
    }

    @GetMapping
    public ResponseEntity<List<GuestHouseDTO>> getAllGuestHouses() {
        return ResponseEntity.ok(guestHouseService.getAllGuestHouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestHouseDTO> getGuestHouseById(@PathVariable Long id) {
        return ResponseEntity.ok(guestHouseService.getGuestHouseById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GuestHouseDTO> updateGuestHouse(@PathVariable Long id, @RequestBody GuestHouse guestHouse) {
        return ResponseEntity.ok(guestHouseService.updateGuestHouse(id, guestHouse));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuestHouse(@PathVariable Long id) {
        guestHouseService.deleteGuestHouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomsByGuestHouse(@PathVariable Long id) {
        return ResponseEntity.ok(guestHouseService.getRoomsByGuestHouse(id));
    }
}