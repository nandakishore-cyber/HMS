package com.example.hms.billingPaymentManagement.dto.bill;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BillCreateRequest(@NotNull UUID patientId, @NotNull LocalDate billDate,
                                LocalDate dueDate, @NotNull @PositiveOrZero BigDecimal taxRate) {}
