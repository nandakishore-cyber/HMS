package com.example.hms.billingPaymentManagement.dto.bill;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record BillUpdateRequest(@NotNull LocalDate dueDate,
                                @NotNull @PositiveOrZero java.math.BigDecimal taxRate) {}
