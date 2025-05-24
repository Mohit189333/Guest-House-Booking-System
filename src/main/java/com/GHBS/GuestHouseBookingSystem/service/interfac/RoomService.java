package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {
    RoomDTO addRoom(Long guestHouseId, Room room, MultipartFile file);

    RoomDTO updateRoom(Long id, Room updatedRoom, MultipartFile file, Long guestHouseId);

    void deleteRoomById(Long id);

    List<RoomDTO> getAvailableRooms();

//    URL uploadRoomImage(Long id, MultipartFile file) throws MalformedURLException;

    RoomDTO getRoomById(Long id);
}