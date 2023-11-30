package com.nicol.testfixing.service;

import com.nicol.testfixing.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IBookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

}
