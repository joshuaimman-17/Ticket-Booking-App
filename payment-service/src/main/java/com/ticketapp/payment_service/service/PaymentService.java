package com.ticketapp.payment_service.service;

import com.ticketapp.payment_service.client.BookingClient;
import com.ticketapp.payment_service.dto.PaymentConfirmRequest;
import com.ticketapp.payment_service.dto.PaymentRequest;
import com.ticketapp.payment_service.entity.PaymentRecord;
import com.ticketapp.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;

    public PaymentService(PaymentRepository paymentRepository, BookingClient bookingClient) {
        this.paymentRepository = paymentRepository;
        this.bookingClient = bookingClient;
    }

    @Transactional
    public PaymentRecord initiatePayment(PaymentRequest req) {
        PaymentRecord payment = new PaymentRecord();
        payment.setBookingId(req.getBookingId());
        payment.setUserId(req.getUserId());
        payment.setAmount(req.getAmount());
        payment.setCurrency(req.getCurrency());
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    public Optional<PaymentRecord> getByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Transactional
    public PaymentRecord handleProviderCallback(PaymentConfirmRequest req) {
        PaymentRecord record = paymentRepository.findById(req.getPaymentRecordId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        record.setProviderPaymentId(req.getProviderPaymentId());
        record.setStatus(req.getStatus());
        paymentRepository.save(record);

        if ("SUCCESS".equalsIgnoreCase(req.getStatus())) {
            try {
                bookingClient.confirmBooking(record.getBookingId(), req.getProviderPaymentId());
            } catch (Exception ex) {
                System.err.println("⚠ Failed to notify Booking Service: " + ex.getMessage());
            }
        }
        return record;
    }
}
