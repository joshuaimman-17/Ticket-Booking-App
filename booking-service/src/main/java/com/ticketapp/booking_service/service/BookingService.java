package com.ticketapp.booking_service.service;

import com.ticketapp.booking_service.client.EventClient;
import com.ticketapp.booking_service.client.TicketClient;
import com.ticketapp.booking_service.dto.BookingDTO;
import com.ticketapp.booking_service.exception.ResourceNotFoundException;
import com.ticketapp.booking_service.entity.Booking;
import com.ticketapp.booking_service.entity.BookingStatus;
import com.ticketapp.booking_service.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventClient eventClient;
    private final TicketClient ticketClient;

    public BookingService(BookingRepository bookingRepository, EventClient eventClient, TicketClient ticketClient) {
        this.bookingRepository = bookingRepository;
        this.eventClient = eventClient;
        this.ticketClient = ticketClient;
    }

    @Transactional
    public Booking createBooking(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setUserId(dto.getUserId());
        booking.setEventId(dto.getEventId());
        booking.setTicketType(dto.getTicketType());
        booking.setQuantity(dto.getQuantity());
        booking.setStatus(BookingStatus.HOLD);
        booking.setHoldExpiry(LocalDateTime.now().plusMinutes(5));
        booking.setPaymentStatus("PENDING");
        booking.setCancellationAllowed(true);

        Booking saved = bookingRepository.save(booking);

        // Decrease available tickets
        Map<String, Integer> payload = Map.of(dto.getTicketType(), -dto.getQuantity());
        eventClient.updateTicketAvailability(dto.getEventId(), payload);

        return saved;
    }

    @Transactional
    public Booking confirmBooking(UUID bookingId, String paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus("SUCCESS");
        booking.setPaymentId(paymentId);
        Booking saved = bookingRepository.save(booking);

        try {
            ticketClient.generateTicket(saved.getBookingId());
        } catch (Exception e) {
            System.err.println("Ticket generation failed: " + e.getMessage());
        }

        return saved;
    }

    @Transactional
    public Booking cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus("CANCELLED");

        // Release tickets
        Map<String, Integer> payload = Map.of(booking.getTicketType(), booking.getQuantity());
        eventClient.updateTicketAvailability(booking.getEventId(), payload);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Auto-cancel HOLD bookings older than 5 minutes
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelExpiredHolds() {
        bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.HOLD
                        && b.getHoldExpiry() != null
                        && b.getHoldExpiry().isBefore(LocalDateTime.now()))
                .forEach(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    b.setPaymentStatus("EXPIRED");
                    Map<String, Integer> payload = Map.of(b.getTicketType(), b.getQuantity());
                    try {
                        eventClient.updateTicketAvailability(b.getEventId(), payload);
                    } catch (Exception e) {
                        System.err.println("Failed to release tickets: " + e.getMessage());
                    }
                    bookingRepository.save(b);
                });
    }
}
