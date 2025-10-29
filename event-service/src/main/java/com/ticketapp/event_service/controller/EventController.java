package com.ticketapp.event_service.controller;

import com.ticketapp.event_service.entity.Event;
import com.ticketapp.event_service.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*") // Allow frontend access (important for microservices / web frontend)
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    // ✅ Fetch all events
    @GetMapping("/allEvents")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = service.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // ✅ Add a new event
    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event createdEvent = service.addEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // ✅ Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable UUID id) {
        Event event = service.getEventById(id);
        return (event != null)
                ? ResponseEntity.ok(event)
                : ResponseEntity.notFound().build();
    }

    // ✅ Delete event by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ PATCH endpoint to update ticket availability (e.g. increment sold tickets)
    @PatchMapping("/{id}/tickets")
    public ResponseEntity<Event> updateTicketAvailability(
            @PathVariable UUID id,
            @RequestParam int ticketsSold) {

        Event updatedEvent = service.updateTicketAvailability(id, ticketsSold);
        return (updatedEvent != null)
                ? ResponseEntity.ok(updatedEvent)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
