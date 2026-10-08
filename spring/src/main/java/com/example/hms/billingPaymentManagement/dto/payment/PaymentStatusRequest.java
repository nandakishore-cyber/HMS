package com.example.hms.billingPaymentManagement.dto.payment;

import com.example.hms.billingPaymentManagement.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusRequest(@NotNull PaymentStatus status) {}
