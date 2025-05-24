package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.GuestHouseDTO;
import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.GuestHouse;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.exception.ResourceNotFoundException;
import com.GHBS.GuestHouseBookingSystem.repo.GuestHouseRepository;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.GuestHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestHouseServiceImpl implements GuestHouseService {

    private final GuestHouseRepository guestHouseRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public GuestHouseServiceImpl(GuestHouseRepository guestHouseRepository,
                                 RoomRepository roomRepository) {
        this.guestHouseRepository = guestHouseRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public GuestHouseDTO createGuestHouse(GuestHouse guestHouse) {
        validateGuestHouse(guestHouse);
        GuestHouse savedGuestHouse = guestHouseRepository.save(guestHouse);
        return convertToGuestHouseDTO(savedGuestHouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestHouseDTO> getAllGuestHouses() {
        return guestHouseRepository.findAll().stream()
                .map(this::convertToGuestHouseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GuestHouseDTO getGuestHouseById(Long id) {
        GuestHouse guestHouse = guestHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found with id: " + id));
        return convertToGuestHouseDTO(guestHouse);
    }

    @Override
    @Transactional
    public GuestHouseDTO updateGuestHouse(Long id, GuestHouse guestHouseDetails) {
        GuestHouse guestHouse = guestHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found with id: " + id));

        updateGuestHouseFields(guestHouse, guestHouseDetails);
        GuestHouse updatedGuestHouse = guestHouseRepository.save(guestHouse);
        return convertToGuestHouseDTO(updatedGuestHouse);
    }

    @Override
    @Transactional
    public void deleteGuestHouse(Long id) {
        GuestHouse guestHouse = guestHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found with id: " + id));

        guestHouseRepository.delete(guestHouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByGuestHouse(Long guestHouseId) {
        GuestHouse guestHouse = guestHouseRepository.findById(guestHouseId)
                .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found with id: " + guestHouseId));

        return roomRepository.findByGuestHouse(guestHouse).stream()
                .map(this::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private void validateGuestHouse(GuestHouse guestHouse) {
        if (guestHouse.getName() == null || guestHouse.getName().isEmpty()) {
            throw new IllegalArgumentException("Guest house name cannot be empty");
        }
        if (guestHouse.getLocation() == null || guestHouse.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Guest house location cannot be empty");
        }
    }

    private void updateGuestHouseFields(GuestHouse guestHouse, GuestHouse guestHouseDetails) {
        if (guestHouseDetails.getName() != null) {
            guestHouse.setName(guestHouseDetails.getName());
        }
        if (guestHouseDetails.getLocation() != null) {
            guestHouse.setLocation(guestHouseDetails.getLocation());
        }
        if (guestHouseDetails.getDescription() != null) {
            guestHouse.setDescription(guestHouseDetails.getDescription());
        }
        if (guestHouseDetails.getContactInfo() != null) {
            guestHouse.setContactInfo(guestHouseDetails.getContactInfo());
        }
    }

    private GuestHouseDTO convertToGuestHouseDTO(GuestHouse guestHouse) {
        GuestHouseDTO dto = new GuestHouseDTO();
        dto.setId(guestHouse.getId());
        dto.setName(guestHouse.getName());
        dto.setLocation(guestHouse.getLocation());
        dto.setDescription(guestHouse.getDescription());
        dto.setContactInfo(guestHouse.getContactInfo());

        // Convert rooms to RoomDTOs without causing circular reference
        if (guestHouse.getRooms() != null) {
            dto.setRooms(guestHouse.getRooms().stream()
                    .map(room -> {
                        RoomDTO roomDTO = new RoomDTO();
                        roomDTO.setId(room.getId());
                        roomDTO.setName(room.getName());
                        roomDTO.setDescription(room.getDescription());
                        roomDTO.setPricePerNight(room.getPricePerNight());
                        roomDTO.setIsAvailable(room.getIsAvailable());
                        roomDTO.setAmenities(room.getAmenities());
                        roomDTO.setImageUrl(room.getImageUrl());
                        roomDTO.setRoomType(room.getRoomType());
                        roomDTO.setGuestHouseId(guestHouse.getId());
                        roomDTO.setGuestHouseName(guestHouse.getName());                        return roomDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private RoomDTO convertToRoomDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setIsAvailable(room.getIsAvailable());
        dto.setAmenities(room.getAmenities());
        dto.setImageUrl(room.getImageUrl());
        dto.setRoomType(room.getRoomType());

        // Only include guest house ID to prevent circular reference
        if (room.getGuestHouse() != null) {
            dto.setGuestHouseId(room.getGuestHouse().getId());
            dto.setGuestHouseName(room.getGuestHouse().getName());
        }

        return dto;
    }
}