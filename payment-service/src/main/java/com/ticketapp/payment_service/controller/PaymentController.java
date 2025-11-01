package com.ticketapp.payment_service.controller;

import com.ticketapp.payment_service.dto.PaymentConfirmRequest;
import com.ticketapp.payment_service.dto.PaymentRequest;
import com.ticketapp.payment_service.dto.PaymentResponse;
import com.ticketapp.payment_service.entity.PaymentRecord;
import com.ticketapp.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * ✅ Initiate a new UPI or coupon-based payment
     * - If a valid coupon code is entered → auto-success (no gateway call)
     * - Else → Creates UPI payment request via Razorpay / Simulated provider
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest req) {
        PaymentRecord record = paymentService.initiatePayment(req);

        String message;
        if ("SUCCESS".equalsIgnoreCase(record.getStatus())) {
            message = "✅ Payment completed instantly using coupon or testing code.";
        } else {
            message = "💳 Payment initiated successfully. Complete payment via UPI gateway.";
        }

        PaymentResponse response = new PaymentResponse(
                message,
                record.getId().toString(),
                record.getAmount()
        );

        return ResponseEntity.created(URI.create("/payments/" + record.getId())).body(response);
    }

    /**
     * ✅ Simulate a provider callback manually (useful for dev/testing)
     */
    @GetMapping("/simulate")
    public ResponseEntity<String> simulatePayment(@RequestParam UUID paymentRecordId,
                                                  @RequestParam(defaultValue = "SUCCESS") String status) {
        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentRecordId(paymentRecordId);
        req.setProviderPaymentId("SIM-" + paymentRecordId);
        req.setStatus(status);

        paymentService.handleProviderCallback(req);

        return ResponseEntity.ok("✅ Simulated callback for " + paymentRecordId + " with status: " + status);
    }

    /**
     * ✅ Payment provider webhook (Razorpay / Cashfree callback)
     */
    @PostMapping("/confirm")
    public ResponseEntity<PaymentRecord> confirmPayment(@RequestBody PaymentConfirmRequest req) {
        return ResponseEntity.ok(paymentService.handleProviderCallback(req));
    }

    /**
     * ✅ Retrieve payment info by bookingId
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getByBooking(@PathVariable UUID bookingId) {
        Optional<PaymentRecord> record = paymentService.getByBookingId(bookingId);
        return record.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ Retrieve all payments by userId
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentRecord>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getByUserId(userId));
    }

    /**
     * ✅ Retrieve all payments (admin/debug)
     */
    @GetMapping
    public ResponseEntity<List<PaymentRecord>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * ✅ Retrieve specific payment by paymentId
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getById(@PathVariable UUID paymentId) {
        Optional<PaymentRecord> record = paymentService.getById(paymentId);
        return record.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
