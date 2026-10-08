package com.example.hms.billingPaymentManagement.dto.payment;

import com.example.hms.billingPaymentManagement.entity.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID billId, String paymentReference, BigDecimal amount,
                              PaymentMethod paymentMethod, LocalDate paymentDate, String notes,
                              boolean cancelled, PaymentStatus status) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getBill().getId(), payment.getPaymentReference(),
                payment.getAmount(), payment.getPaymentMethod(), payment.getPaymentDate(),
                payment.getNotes(), payment.isCancelled(), payment.getStatus());
    }
}
