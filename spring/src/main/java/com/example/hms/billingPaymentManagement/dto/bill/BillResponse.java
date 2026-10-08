package com.example.hms.billingPaymentManagement.dto.bill;

import com.example.hms.billingPaymentManagement.entity.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public record BillResponse(UUID id, String billNumber, UUID patientId, LocalDate billDate,
                           LocalDate dueDate, BigDecimal subtotal, BigDecimal taxRate,
                           BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal paidAmount,
                           BigDecimal balanceAmount, PaymentStatus status, List<BillItemResponse> items) {
    public static BillResponse from(Bill bill, List<BillItem> items) {
        return new BillResponse(bill.getId(), bill.getBillNumber(), bill.getPatientId(), bill.getBillDate(),
                bill.getDueDate(), bill.getSubtotal(), bill.getTaxRate(), bill.getTaxAmount(),
                bill.getTotalAmount(), bill.getPaidAmount(), bill.getBalanceAmount(), bill.getStatus(),
                items.stream().map(BillItemResponse::from).toList());
    }
}
