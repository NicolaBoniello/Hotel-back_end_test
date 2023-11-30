package com.nicol.testfixing.repository;


import com.nicol.testfixing.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookingRepository extends JpaRepository<BookedRoom, Long> {
}
