package com.GHBS.GuestHouseBookingSystem.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDTO {

    private Long id;
    private String name;
    private String description;
    private Double pricePerNight;
    private Boolean isAvailable;
    private List<String> amenities;

}
