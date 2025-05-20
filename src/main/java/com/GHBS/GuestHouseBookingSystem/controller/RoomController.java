package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.entity.RoomType;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Admin-only: Add a new room
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomDTO> addRoom(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double pricePerNight,
            @RequestParam boolean isAvailable,
            @RequestParam String amenities, // Comma-separated string
            @RequestParam RoomType roomType,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            // Convert comma-separated amenities to List
            List<String> amenitiesList = new ArrayList<>(Arrays.asList(amenities.split("\\s*,\\s*")));

            Room room = new Room();
            room.setName(name);
            room.setDescription(description);
            room.setPricePerNight(pricePerNight);
            room.setIsAvailable(isAvailable);
            room.setAmenities(amenitiesList);
            room.setRoomType(roomType);

            RoomDTO addedRoom = roomService.addRoom(room, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedRoom);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin-only: Update room details
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double pricePerNight,
            @RequestParam boolean isAvailable,
            @RequestParam String amenities,
            @RequestParam RoomType roomType,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            List<String> amenitiesList = new ArrayList<>(Arrays.asList(amenities.split("\\s*,\\s*")));

            Room updatedRoom = new Room();
            updatedRoom.setName(name);
            updatedRoom.setDescription(description);
            updatedRoom.setPricePerNight(pricePerNight);
            updatedRoom.setIsAvailable(isAvailable);
            updatedRoom.setAmenities(amenitiesList);
            updatedRoom.setRoomType(roomType);

            return ResponseEntity.ok(roomService.updateRoom(id, updatedRoom, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin-only: Delete a room
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long id) {
        try{
            roomService.deleteRoomById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Public: View all available rooms
    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() {
        try{
            return ResponseEntity.ok(roomService.getAvailableRooms());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(roomService.getRoomById(id));
    }catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();}
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{id}/uploadImage")
//    public ResponseEntity<String> uploadRoomImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
//        try {
//            URL imageUrl = roomService.uploadRoomImage(id, file);
//            return ResponseEntity.ok(imageUrl.toString());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed: " + e.getMessage());
//        }
//    }
}