package com.nicol.testfixing.service;

import com.nicol.testfixing.exception.InternalServerException;
import com.nicol.testfixing.exception.ResourceNotFoundException;
import com.nicol.testfixing.model.Room;
import com.nicol.testfixing.repository.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements IRoomService{
    @Autowired
    private IRoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String roomDescription) throws IOException, SQLException {

        Room newRoom = new Room();

        newRoom.setRoomType(roomType);
        newRoom.setRoomPrice(roomPrice);
        newRoom.setRoomDescription(roomDescription);

        if (!photo.isEmpty()){
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            newRoom.setPhoto(photoBlob);
        }


        return roomRepository.save(newRoom);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, room not found");

        }
        Blob photoBlob = theRoom.get().getPhoto();
        if (photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());

        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {

        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isPresent()){
            roomRepository.deleteById(roomId);
        } else {
            throw new ResourceNotFoundException("The room with id: " + roomId + " could not be found");
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType,  BigDecimal roomPrice, byte[] photoBytes, String roomDescription) throws SQLException {
        Room room = roomRepository.findById(roomId).get();


        if (roomType != null) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);
        if (roomDescription != null) room.setRoomDescription(roomDescription);
        if (photoBytes != null && photoBytes.length > 0){
            try{
                room.setPhoto(new SerialBlob(photoBytes));
            }catch (SQLException ex){
                throw new InternalServerException("Error updating room");

            }
        }


        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {

        return Optional.of(roomRepository.findById(roomId).get());
    }
}
