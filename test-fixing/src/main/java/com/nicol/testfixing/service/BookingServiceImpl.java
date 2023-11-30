package com.nicol.testfixing.service;

import com.nicol.testfixing.model.BookedRoom;
import com.nicol.testfixing.repository.IBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements IBookingService{
    @Autowired
    private IBookingRepository bookingRepository;

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return null;
    }
}
