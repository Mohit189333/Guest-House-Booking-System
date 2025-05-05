package com.GHBS.GuestHouseBookingSystem.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double pricePerNight;
    private Boolean isAvailable;

    @ElementCollection // This annotation is used to specify that the field is a collection of basic types or embeddable classes
    private List<String> amenities;

    private String imageUrl; // New field for storing the image URL

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    public Room() {}

    public Room(String name, String description, Double pricePerNight, Boolean isAvailable, List<String> amenities) {
        this.name = name;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
        this.amenities = amenities;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}