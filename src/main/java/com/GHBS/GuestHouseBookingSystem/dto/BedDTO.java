package com.GHBS.GuestHouseBookingSystem.dto;

import lombok.*;

@Data
@Getter
@Setter
public class BedDTO {
    private Long id;
    private String bedType;
    private Boolean isAvailable;

    public BedDTO(Long id, String bedType, Boolean isAvailable) {
        this.id = id;
        this.bedType = bedType;
        this.isAvailable = isAvailable;
    }

    public BedDTO() {

    }

    // Getters and Setters
}