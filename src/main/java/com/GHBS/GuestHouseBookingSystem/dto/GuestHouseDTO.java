package com.GHBS.GuestHouseBookingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestHouseDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String contactInfo;
    private List<RoomDTO> rooms; // Include list of RoomDTOs
}