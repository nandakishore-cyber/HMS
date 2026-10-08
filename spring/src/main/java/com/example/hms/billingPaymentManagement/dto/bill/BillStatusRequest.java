package com.example.hms.billingPaymentManagement.dto.bill;

import com.example.hms.billingPaymentManagement.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record BillStatusRequest(@NotNull PaymentStatus status) {}
