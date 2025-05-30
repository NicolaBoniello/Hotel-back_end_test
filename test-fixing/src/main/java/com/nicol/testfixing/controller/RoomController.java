package com.nicol.testfixing.controller;

import com.nicol.testfixing.exception.PhotoRetrievalException;
import com.nicol.testfixing.exception.ResourceNotFoundException;
import com.nicol.testfixing.model.BookedRoom;
import com.nicol.testfixing.model.Room;
import com.nicol.testfixing.response.RoomResponse;
import com.nicol.testfixing.service.IBookingService;
import com.nicol.testfixing.service.IRoomService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IBookingService bookingService;

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam("roomDescription") String roomDescription) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(photo,roomType, roomPrice, roomDescription);

        RoomResponse response = new RoomResponse(savedRoom.getId(),  savedRoom.getRoomType(), savedRoom.getRoomPrice(), savedRoom.getRoomDescription());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") Long roomId){
        Optional<Room> theRoom = roomService.getRoomById( roomId);
        return theRoom.map(room -> {
            try {
                RoomResponse roomResponse = getRoomResponse(room);
                return ResponseEntity.ok(Optional.of(roomResponse));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, PhotoRetrievalException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room: rooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0 ){
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }

        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId){
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable("roomId") Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo,
                                                   @RequestParam(required = false) String roomDescription) throws IOException, SQLException {

        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);

        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;

        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes, roomDescription);
        theRoom.setPhoto(photoBlob);

        RoomResponse roomResponse = getRoomResponse(theRoom);

        return ResponseEntity.ok(roomResponse);

    }


    private RoomResponse getRoomResponse(Room room) throws SQLException, PhotoRetrievalException {
        List<BookedRoom> bookings = getAllBookingByRoomId(room.getId());
        /*List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();*/

        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();

        if (photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());

            }catch (SQLException e){
                throw new PhotoRetrievalException("Error retrieving photo");

            }
        }

        return new RoomResponse(room.getId(),
                room.getRoomType(), room.getRoomPrice(),
                room.isBooked(), photoBytes, room.getRoomDescription());
    }

    private List<BookedRoom> getAllBookingByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);

    }
}
