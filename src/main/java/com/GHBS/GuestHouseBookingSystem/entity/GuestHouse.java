package com.GHBS.GuestHouseBookingSystem.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class GuestHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String description;
    private String contactInfo;

    @OneToMany(mappedBy = "guestHouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    public GuestHouse() {}

    public GuestHouse(String name, String location, String description, String contactInfo) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.contactInfo = contactInfo;
    }

    public void addRoom(Room room) {
        rooms.add(room);
        room.setGuestHouse(this);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setGuestHouse(null);
    }


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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}