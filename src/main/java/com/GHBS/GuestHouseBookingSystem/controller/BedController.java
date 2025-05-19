package com.GHBS.GuestHouseBookingSystem.controller;

import com.GHBS.GuestHouseBookingSystem.dto.BedDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Bed;
import com.GHBS.GuestHouseBookingSystem.service.interfac.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") //CORS (Cross-Origin Resource Sharing) allows your API to be accessed from different domains
@RequestMapping("/api/rooms/{roomId}/beds")
public class BedController {

    @Autowired
    private BedService bedService;

    // Add a bed to a room
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BedDTO> addBedToRoom(@PathVariable Long roomId, @RequestBody Bed bed) {
        try {
            BedDTO addedBed = bedService.addBedToRoom(roomId, bed);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedBed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all beds in a room
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Allow both ADMIN and USER to view beds
    public ResponseEntity<?> getBedsByRoom(@PathVariable Long roomId) {
        try {
            List<BedDTO> beds = bedService.getBedsByRoom(roomId);
            return ResponseEntity.ok(beds);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching beds: " + e.getMessage());
        }
    }

    // Update a bed
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bedId}")
    public ResponseEntity<BedDTO> updateBed(@PathVariable Long bedId, @RequestBody Bed updatedBed) {
        try {
            BedDTO bed = bedService.updateBed(bedId, updatedBed);
            return ResponseEntity.ok(bed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete a bed
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bedId}")
    public ResponseEntity<Void> deleteBed(@PathVariable Long bedId) {
        try {
            bedService.deleteBed(bedId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}