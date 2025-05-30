package com.nicol.testfixing.service;

import com.nicol.testfixing.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public interface IRoomService {

    Room addNewRoom(MultipartFile photo , String roomType, BigDecimal roomPrice, String roomDescription) throws IOException, SQLException;


    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long id) throws SQLException;

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String roomType,  BigDecimal roomPrice, byte[] photoBytes, String roomDescription) throws SQLException;

    Optional<Room> getRoomById(Long roomId);

}

