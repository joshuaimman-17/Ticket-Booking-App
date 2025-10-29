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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest req) {
        PaymentRecord p = paymentService.initiatePayment(req);
        String simulateUrl = "/payments/simulate?paymentRecordId=" + p.getId();
        return ResponseEntity.created(URI.create("/payments/" + p.getId()))
                .body(new PaymentResponse(simulateUrl, p.getId().toString()));
    }

    @GetMapping("/simulate")
    public ResponseEntity<String> simulateProvider(@RequestParam UUID paymentRecordId,
                                                   @RequestParam(defaultValue = "SUCCESS") String status) {
        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentRecordId(paymentRecordId);
        req.setProviderPaymentId("SIM-" + paymentRecordId);
        req.setStatus(status);
        paymentService.handleProviderCallback(req);
        return ResponseEntity.ok("✅ Simulated payment callback for " + paymentRecordId + " status=" + status);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentRecord> confirmPayment(@RequestBody PaymentConfirmRequest req) {
        return ResponseEntity.ok(paymentService.handleProviderCallback(req));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getByBooking(@PathVariable UUID bookingId) {
        Optional<PaymentRecord> opt = paymentService.getByBookingId(bookingId);
        return opt.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
