package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;

import java.util.List;

public interface RoomService {
    RoomDTO addRoom(Room room);

    RoomDTO updateRoom(Long id, Room updatedRoom);

    void deleteRoomById(Long id);

    List<RoomDTO> getAvailableRooms();
}
