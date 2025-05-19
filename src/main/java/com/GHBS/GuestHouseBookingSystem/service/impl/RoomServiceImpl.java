package com.GHBS.GuestHouseBookingSystem.service.impl;

import com.GHBS.GuestHouseBookingSystem.dto.RoomDTO;
import com.GHBS.GuestHouseBookingSystem.entity.Room;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    private static final String BUCKET_NAME = "myhms-img";
    private static final String REGION = "ap-south-1";
    private static final String ACCESS_KEY = "AKIA3FLDZRGVP4Q63G5A";
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
    public URL uploadRoomImage(Long id, MultipartFile file) throws MalformedURLException {
        // Fetch the room entity
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!"));

        // Generate the S3 file name
        String fileName = "rooms/" + id + "/" + file.getOriginalFilename();

        // Build the S3 PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .contentType(file.getContentType()) // Set the correct content type
                .build();

        try {
            // Use InputStream from MultipartFile for S3 upload
            s3Client.putObject(
                    putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage(), e);
        }

        // Generate the S3 file URL
        String imageUrl = "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/" + fileName;

        // Update the room entity with the image URL
        room.setImageUrl(imageUrl);
        roomRepository.save(room);

        // Return the S3 file URL
        return new URL(imageUrl);
    }

    @Override
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!"));
        return mapRoomEntityToRoomDTO(room);
    }


    @Override
    public RoomDTO addRoom(Room room, MultipartFile file) {
        try {
            // Save the room entity first (without image URL)
            Room savedRoom = roomRepository.save(room);

            // If an image file is provided, upload it
            if (file != null && !file.isEmpty()) {
                String imageUrl = uploadImageToS3(savedRoom.getId(), file);
                // Update the room entity with the image URL
                savedRoom.setImageUrl(imageUrl);
                roomRepository.save(savedRoom);
            }

            return mapRoomEntityToRoomDTO(savedRoom);
        } catch (Exception e) {
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
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public RoomDTO updateRoom(Long id, Room updatedRoom) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!"));
        room.setName(updatedRoom.getName());
        room.setDescription(updatedRoom.getDescription());
        room.setPricePerNight(updatedRoom.getPricePerNight());
        room.setIsAvailable(updatedRoom.getIsAvailable());
        room.setAmenities(updatedRoom.getAmenities());
        room.setImageUrl(updatedRoom.getImageUrl()); // Update image URL
        room.setRoomType(updatedRoom.getRoomType());
        roomRepository.save(room);
        return this.mapRoomEntityToRoomDTO(room);
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
        return roomDTO;
    }
}