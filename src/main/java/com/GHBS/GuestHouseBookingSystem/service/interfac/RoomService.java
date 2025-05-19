package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface RoomService {
    RoomDTO addRoom(Room room, MultipartFile file);

    RoomDTO updateRoom(Long id, Room updatedRoom);

    void deleteRoomById(Long id);

    List<RoomDTO> getAvailableRooms();

    URL uploadRoomImage(Long id, MultipartFile file) throws MalformedURLException;

    RoomDTO getRoomById(Long id);
}