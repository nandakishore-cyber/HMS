package com.example.hms.billingPaymentManagement.dto.payment;

import com.example.hms.billingPaymentManagement.entity.PaymentMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentCreateRequest(@NotBlank @Size(max = 60) String paymentReference,
                                   @NotNull @Positive BigDecimal amount,
                                   @NotNull PaymentMethod paymentMethod,
                                   @NotNull LocalDate paymentDate,
                                   @Size(max = 500) String notes) {}
