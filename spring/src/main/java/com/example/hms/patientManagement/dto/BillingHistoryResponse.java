package com.example.hms.patientManagement.dto;

import com.example.hms.billingPaymentManagement.entity.Bill;
import com.example.hms.billingPaymentManagement.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BillingHistoryResponse(UUID id, String billNumber, LocalDate billDate,
                                     BigDecimal totalAmount, BigDecimal paidAmount,
                                     BigDecimal balanceAmount, PaymentStatus status) {
    public static BillingHistoryResponse from(Bill bill) {
        return new BillingHistoryResponse(bill.getId(), bill.getBillNumber(), bill.getBillDate(),
                bill.getTotalAmount(), bill.getPaidAmount(), bill.getBalanceAmount(), bill.getStatus());
    }
}
