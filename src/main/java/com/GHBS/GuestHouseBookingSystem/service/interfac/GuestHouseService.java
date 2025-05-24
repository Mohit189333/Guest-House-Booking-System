package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.GuestHouseDTO;
import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.GuestHouse;
import com.GHBS.GuestHouseBookingSystem.entity.Room;

import java.util.List;

public interface GuestHouseService {
    GuestHouseDTO createGuestHouse(GuestHouse guestHouse);

    List<GuestHouseDTO> getAllGuestHouses();

    GuestHouseDTO getGuestHouseById(Long id);

    GuestHouseDTO updateGuestHouse(Long id, GuestHouse guestHouse);

    void deleteGuestHouse(Long id);

    List<RoomDTO> getRoomsByGuestHouse(Long id);
}
