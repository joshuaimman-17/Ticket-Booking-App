package com.ticketapp.ticket_service.controller;

import com.ticketapp.ticket_service.entity.TicketRecord;
import com.ticketapp.ticket_service.service.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // ✅ Generate ticket using only bookingId
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, String> body) {
        try {
            UUID bookingId = UUID.fromString(body.get("bookingId"));
            TicketRecord rec = ticketService.generateTicket(bookingId);
            return ResponseEntity.ok(rec);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed to generate ticket: " + e.getMessage());
        }
    }

    // Download ticket
    @GetMapping("/download/{bookingId}")
    public ResponseEntity<?> download(@PathVariable UUID bookingId) {
        Optional<TicketRecord> opt = ticketService.findByBookingId(bookingId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        TicketRecord rec = opt.get();
        try {
            byte[] data = ticketService.getTicketPdfData(rec.getTicketPdfPath());
            Path p = Path.of(rec.getTicketPdfPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to read ticket file: " + e.getMessage());
        }
    }
}
