package com.ticketapp.payment_service.service;

import com.ticketapp.payment_service.client.BookingClient;
import com.ticketapp.payment_service.dto.PaymentConfirmRequest;
import com.ticketapp.payment_service.dto.PaymentRequest;
import com.ticketapp.payment_service.entity.PaymentRecord;
import com.ticketapp.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * ✅ Initiate a new payment (UPI / coupon / test)
     */
    @Transactional
    public PaymentRecord initiatePayment(PaymentRequest req) {
        PaymentRecord payment = new PaymentRecord();
        payment.setBookingId(req.getBookingId());
        payment.setUserId(req.getUserId());
        payment.setCurrency(req.getCurrency());
        payment.setAmount(req.getAmount());
        payment.setStatus("PENDING");
        payment.setUpiId(req.getUpiId());
        payment.setCouponCode(req.getCouponCode());

        // 💡 Coupon code logic (instant success)
        if (req.getCouponCode() != null) {
            String code = req.getCouponCode().trim().toUpperCase();

            switch (code) {
                case "FREE100" -> {
                    payment.setStatus("SUCCESS");
                    payment.setProviderPaymentId("COUPON-FREE100");
                }
                case "DEVTEST" -> {
                    payment.setStatus("SUCCESS");
                    payment.setProviderPaymentId("COUPON-DEVTEST");
                }
                case "NEWUSER10" -> {
                    // Apply 10% discount, still process payment
                    double discount = req.getAmount() * 0.10;
                    payment.setAmount(req.getAmount() - discount);
                    payment.setStatus("PENDING");
                }
                default -> payment.setStatus("PENDING");
            }

            // If coupon makes payment instantly successful
            if ("SUCCESS".equals(payment.getStatus())) {
                paymentRepository.save(payment);
                bookingClient.confirmBooking(payment.getBookingId(), payment.getProviderPaymentId());
                return payment;
            }
        }

        // 🪙 Otherwise: initiate actual UPI / gateway flow (placeholder)
        paymentRepository.save(payment);
        return payment;
    }

    public Optional<PaymentRecord> getByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Optional<PaymentRecord> getById(UUID id) {
        return paymentRepository.findById(id);
    }

    public List<PaymentRecord> getByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<PaymentRecord> getAllPayments() {
        return paymentRepository.findAll();
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
