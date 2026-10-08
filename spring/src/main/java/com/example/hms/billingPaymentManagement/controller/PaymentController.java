package com.example.hms.billingPaymentManagement.controller;

import com.example.hms.billingPaymentManagement.dto.payment.*;
import com.example.hms.billingPaymentManagement.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/bills/{billId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse record(@PathVariable UUID billId, @Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.record(billId, request);
    }
    @GetMapping("/bills/{billId}/payments")
    public List<PaymentResponse> history(@PathVariable UUID billId) { return paymentService.history(billId); }
    @GetMapping("/payments/{paymentId}")
    public PaymentResponse get(@PathVariable UUID paymentId) { return paymentService.get(paymentId); }
    @PatchMapping("/payments/{paymentId}/status")
    public PaymentResponse status(@PathVariable UUID paymentId,
                                  @Valid @RequestBody PaymentStatusRequest request) {
        return paymentService.setStatus(paymentId, request.status());
    }
    @GetMapping("/payments")
    public Page<PaymentResponse> search(Pageable pageable) { return paymentService.search(pageable); }
}
