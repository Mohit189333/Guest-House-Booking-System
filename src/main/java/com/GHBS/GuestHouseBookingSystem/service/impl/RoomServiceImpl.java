package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomDTO addRoom(Room room) {
        Room addRoom = roomRepository.save(room);
        return this.mapRoomEntityToRoomDTO(addRoom);
    }

    @Override
    public RoomDTO updateRoom(Long id, Room updatedRoom) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!"));
        room.setName(updatedRoom.getName());
        room.setDescription(updatedRoom.getDescription());
        room.setPricePerNight(updatedRoom.getPricePerNight());
        room.setIsAvailable(updatedRoom.getIsAvailable());
        room.setAmenities(updatedRoom.getAmenities());
        roomRepository.save(room);
        return this.mapRoomEntityToRoomDTO(room);
    }

    @Override
    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findAll();
        List<RoomDTO>roomDTOList = new ArrayList<>();

        for(Room room : availableRooms){
            RoomDTO roomDTO = this.mapRoomEntityToRoomDTO(room);
            roomDTOList.add(roomDTO);
        }
        return roomDTOList;
    }

    private RoomDTO mapRoomEntityToRoomDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setDescription(room.getDescription());
        roomDTO.setPricePerNight(room.getPricePerNight());
        roomDTO.setIsAvailable(room.getIsAvailable());
        roomDTO.setAmenities(room.getAmenities());
        return roomDTO;
    }
}
