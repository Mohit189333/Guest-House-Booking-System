package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.GuestHouse;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
import com.GHBS.GuestHouseBookingSystem.exception.ResourceNotFoundException;
import com.GHBS.GuestHouseBookingSystem.repo.BookingRepository;
import com.GHBS.GuestHouseBookingSystem.repo.GuestHouseRepository;
import com.GHBS.GuestHouseBookingSystem.repo.RoomRepository;
import com.GHBS.GuestHouseBookingSystem.service.interfac.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private GuestHouseRepository guestHouseRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;


    private static final String BUCKET_NAME = "myhms-img";
    private static final String REGION = "ap-south-1";
    private static final String ACCESS_KEY = "AKIA3FLDZRGVE2OIQ34L";
    private static final String SECRET_KEY = "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";

    private final S3Client s3Client;

    public RoomServiceImpl() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        this.s3Client = S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    @Override
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!"));
        return mapRoomEntityToRoomDTO(room);
    }


    @Override
    public RoomDTO addRoom(Long guestHouseId, Room room, MultipartFile file) {
        try {
            GuestHouse guestHouse = guestHouseRepository.findById(guestHouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found"));

            room.setGuestHouse(guestHouse);
            guestHouse.getRooms().add(room);
            // Save the room entity first (without image URL)
            Room savedRoom = roomRepository.save(room);

            // If an image file is provided, upload it
            if (file != null && !file.isEmpty()) {
                String imageUrl = uploadImageToS3(savedRoom.getId(), file);
                // Update the room entity with the image URL
                savedRoom.setImageUrl(imageUrl);
                roomRepository.save(savedRoom);
            }
            guestHouseRepository.save(guestHouse);
            return mapRoomEntityToRoomDTO(savedRoom);
        } catch (Exception e) {
            e.printStackTrace(); // Add this line
            throw new RuntimeException("Failed to add room: " + e.getMessage(), e);
        }
    }

    private String uploadImageToS3(Long roomId, MultipartFile file) {
        try {
            // Generate unique file name to prevent overwrites
            String fileName = "rooms/" + roomId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/" + fileName;
        } catch (Exception e) {
            e.printStackTrace(); // Add this line
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public RoomDTO updateRoom(Long id, Room updatedRoom, MultipartFile file, Long guestHouseId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found!"));

        room.setName(updatedRoom.getName());
        room.setDescription(updatedRoom.getDescription());
        room.setPricePerNight(updatedRoom.getPricePerNight());
        room.setIsAvailable(updatedRoom.getIsAvailable());
        room.setAmenities(updatedRoom.getAmenities());
        room.setRoomType(updatedRoom.getRoomType());
        room.setBedCount(updatedRoom.getBedCount());  // Make sure this line exists

        // If guestHouseId is provided and different, move the room
        if (guestHouseId != null && (room.getGuestHouse() == null || !room.getGuestHouse().getId().equals(guestHouseId))) {
            GuestHouse newGuestHouse = guestHouseRepository.findById(guestHouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("GuestHouse not found"));
            room.setGuestHouse(newGuestHouse); // <-- This is enough
        }

        // If a new image is provided, upload it
        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadImageToS3(id, file);
            room.setImageUrl(imageUrl);
        }

        Room saved = roomRepository.save(room);
        return mapRoomEntityToRoomDTO(saved);
    }


    @Override
    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findAll();
        List<RoomDTO>roomDTOList = new ArrayList<>();

        for(Room room : availableRooms){
            RoomDTO roomDTO = this.mapRoomEntityToRoomDTO(room);
            roomDTOList.add(roomDTO);
        }
        return roomDTOList;
    }

    @Override
    public List<RoomDTO> getAvailableRoomsBetweenDates(LocalDate checkInDate, LocalDate checkOutDate) {
        // Get all rooms
        List<Room> allRooms = roomRepository.findAll();
        List<Long> unavailableRoomIds = bookingRepository.findRoomIdsWithConflictingBookings(checkInDate, checkOutDate);

        // Filter out unavailable rooms
        List<RoomDTO> availableRooms = new ArrayList<>();
        for (Room room : allRooms) {
            if (!unavailableRoomIds.contains(room.getId())) {
                availableRooms.add(mapRoomEntityToRoomDTO(room));
            }
        }
        return availableRooms;
    }


    private RoomDTO mapRoomEntityToRoomDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setDescription(room.getDescription());
        roomDTO.setPricePerNight(room.getPricePerNight());
        roomDTO.setIsAvailable(room.getIsAvailable());
        roomDTO.setAmenities(room.getAmenities());
        roomDTO.setImageUrl(room.getImageUrl());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setBedCount(room.getBedCount()); // Add this line

        if (room.getGuestHouse() != null) {
            roomDTO.setGuestHouseId(room.getGuestHouse().getId());
            roomDTO.setGuestHouseName(room.getGuestHouse().getName());
        }
        return roomDTO;
    }
}

