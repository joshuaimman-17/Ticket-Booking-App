package com.ticketapp.booking_service.repository;

import com.ticketapp.booking_service.entity.Booking;
import com.ticketapp.booking_service.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);
    long countByStatus(BookingStatus status);
}
