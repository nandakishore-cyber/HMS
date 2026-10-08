package com.example.hms.billingPaymentManagement.dto.bill;

import com.example.hms.billingPaymentManagement.entity.*;
import java.math.BigDecimal;
import java.util.UUID;

public record BillItemResponse(UUID id, String description, BillItemCategory category,
                               Integer quantity, BigDecimal unitPrice, BigDecimal amount) {
    public static BillItemResponse from(BillItem item) {
        return new BillItemResponse(item.getId(), item.getDescription(), item.getCategory(),
                item.getQuantity(), item.getUnitPrice(), item.getAmount());
    }
}
