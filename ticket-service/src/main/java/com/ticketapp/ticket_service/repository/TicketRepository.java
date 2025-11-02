package com.ticketapp.ticket_service.repository;

import com.ticketapp.ticket_service.entity.TicketRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketRecord, UUID> {
    Optional<TicketRecord> findByBookingId(UUID bookingId);
}
