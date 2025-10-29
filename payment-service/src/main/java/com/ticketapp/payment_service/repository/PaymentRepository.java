package com.ticketapp.payment_service.repository;

import com.ticketapp.payment_service.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentRecord, UUID> {
    Optional<PaymentRecord> findByBookingId(UUID bookingId);
}
