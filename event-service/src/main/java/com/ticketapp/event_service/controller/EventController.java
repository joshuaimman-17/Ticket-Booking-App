package com.ticketapp.event_service.controller;

import com.ticketapp.event_service.entity.Event;
import com.ticketapp.event_service.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return service.getAllEvents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event addEvent(@RequestBody Event event) {
        return service.addEvent(event);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable UUID id) {
        return service.getEventById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID id) {
        service.deleteEvent(id);
    }
}
