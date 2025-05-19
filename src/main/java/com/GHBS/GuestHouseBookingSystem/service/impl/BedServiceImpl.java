package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.BedDTO;
import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Bed;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.repo.BedRepository;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BedServiceImpl implements BedService {

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public BedDTO addBedToRoom(Long roomId, Bed bed) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        bed.setRoom(room);
        Bed savedBed = bedRepository.save(bed);
        return mapBedEntityToBedDTO(savedBed);
    }

    @Override
    public List<BedDTO> getBedsByRoom(Long roomId) {
        try {
            // Verify room exists
            if (!roomRepository.existsById(roomId)) {
                throw new RuntimeException("Room not found with id: " + roomId);
            }
            // Fetch beds by room using a custom query to avoid N+1 problem
            List<Bed> beds = bedRepository.findByRoomId(roomId);
            if (beds.isEmpty()) {
                return Collections.emptyList(); // Return empty list instead of null
            }
            return beds.stream()
                    .map(this::mapBedEntityToBedDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error for debugging
            throw new RuntimeException("Failed to fetch beds for room: " + roomId, e);
        }
    }

    @Override
    public BedDTO updateBed(Long bedId, Bed updatedBed) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found!"));
        bed.setBedType(updatedBed.getBedType());
        bed.setIsAvailable(updatedBed.getIsAvailable());
        Bed savedBed = bedRepository.save(bed);
        return mapBedEntityToBedDTO(savedBed);
    }

    @Override
    public void deleteBed(Long bedId) {
        bedRepository.deleteById(bedId);
    }

    private BedDTO mapBedEntityToBedDTO(Bed bed) {
        return new BedDTO(
                bed.getId(),
                bed.getBedType(),
                bed.getIsAvailable()
        );
    }
}