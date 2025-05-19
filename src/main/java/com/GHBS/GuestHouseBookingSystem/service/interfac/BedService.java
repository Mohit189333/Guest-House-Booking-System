package com.GHBS.GuestHouseBookingSystem.service.interfac;

import com.GHBS.GuestHouseBookingSystem.dto.BedDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Bed;

import java.util.List;

public interface BedService {
    BedDTO addBedToRoom(Long roomId, Bed bed);
    List<BedDTO> getBedsByRoom(Long roomId);
    BedDTO updateBed(Long bedId, Bed updatedBed);
    void deleteBed(Long bedId);
}