package com.example.hms.patientManagement.dto;

import com.example.hms.patientManagement.entity.BillingHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BillingHistoryResponse(UUID id, String billNumber, LocalDate billDate,
                                     BigDecimal totalAmount, BigDecimal paidAmount,
                                     BigDecimal balanceAmount, String status) {
    public static BillingHistoryResponse from(BillingHistory bill) {
        return new BillingHistoryResponse(bill.getId(), bill.getBillNumber(), bill.getBillDate(),
                bill.getTotalAmount(), bill.getPaidAmount(), bill.getBalanceAmount(), bill.getStatus());
    }
}
