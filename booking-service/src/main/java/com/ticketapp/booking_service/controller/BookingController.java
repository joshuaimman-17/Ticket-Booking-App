package com.ticketapp.booking_service.controller;

import com.ticketapp.booking_service.dto.BookingDTO;
import com.ticketapp.booking_service.entity.Booking;
import com.ticketapp.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO dto) {
        Booking booking = bookingService.createBooking(dto);
        return ResponseEntity.created(URI.create("/bookings/" + booking.getBookingId())).body(booking);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable UUID id, @RequestParam String paymentId) {
        return ResponseEntity.ok(bookingService.confirmBooking(id, paymentId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }
}
