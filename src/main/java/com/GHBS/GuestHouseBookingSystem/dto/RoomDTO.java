package com.GHBS.GuestHouseBookingSystem.dto;

import com.GHBS.GuestHouseBookingSystem.entity.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private String imageUrl; // New field for storing the image URL

    private RoomType roomType;
    private Long guestHouseId; // Add this field
    private String guestHouseName;

    private Integer bedCount; // Add this new field


}

