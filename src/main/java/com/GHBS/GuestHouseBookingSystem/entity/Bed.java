package com.GHBS.GuestHouseBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bedType; // Example: Single, Double, Queen, King
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
//    @JsonBackReference
    private Room room;

    public Bed() {}

    public Bed(String bedType, Boolean isAvailable, Room room) {
        this.bedType = bedType;
        this.isAvailable = isAvailable;
        this.room = room;
    }
}