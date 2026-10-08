package com.example.hms.billingPaymentManagement.dto.bill;

import com.example.hms.billingPaymentManagement.entity.BillItemCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BillItemRequest(@NotBlank @Size(max = 255) String description,
                              @NotNull BillItemCategory category,
                              @NotNull @Positive Integer quantity,
                              @NotNull @PositiveOrZero BigDecimal unitPrice) {}
